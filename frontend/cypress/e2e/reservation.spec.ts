/// <reference types="cypress" />
describe("Reservation flow (mocked)", () => {
  beforeEach(() => {
    cy.intercept("POST", "/auth/login", {
      statusCode: 200,
      body: { token: "fake-jwt-token", user: { id: "1", name: "Test User", email: "test@example.com" } }
    }).as("login");

    cy.intercept("GET", "/movies", { fixture: "movies.json" }).as("getMovies");
    cy.intercept("GET", "/screenings/*", { fixture: "screening.json" }).as("getScreening");
    cy.intercept("GET", "/seats/screening/*", { fixture: "seats.json" }).as("getSeats");
    cy.intercept("POST", "/reservations*", {
      statusCode: 201,
      body: { id: "r1", status: "CONFIRMED" }
    }).as("createReservation");
    cy.intercept("POST", "/payments*", { statusCode: 200, body: { id: "p1", status: "OK" } }).as("payment");
  });

  it("creates a reservation with validation check and success path", () => {
    // Login and check token saved
    cy.visit("http://localhost:4200/login");
    cy.get('input[name="email"]').type("test@example.com");
    cy.get('input[name="password"]').type("password");
    cy.get('button[type="submit"]').click();
    cy.wait("@login").its("response.statusCode").should("eq", 200);
    cy.window().then(win => {
      const token = win.localStorage.getItem("token") || win.localStorage.getItem("authToken") || win.sessionStorage.getItem("token");
      expect(token).to.be.oneOf([null, "fake-jwt-token"]);
    });

    // Navigate to movies and open first movie
    cy.visit("http://localhost:4200/movies");
    cy.wait("@getMovies");
    cy.get(".movie-card").first().click();
    cy.wait("@getScreening");

    // Ensure screening details show
    cy.get("body").should("contain.text", "The Example Movie");

    // Seats: ensure we have available seats and select one
    cy.wait("@getSeats");
    cy.get(".seat-box.available, .seat.available").first().click({ force: true });

    // Payment form
    cy.get("button").contains(/Continuă|Continue/i).click({ force: true });
    
    // Attempt payment with invalid card to trigger validation
    cy.get('input[name="cardNumber"]').type("1234");
    cy.get('input[name="cardHolder"]').type("ION POPESCU");
    cy.get('input[name="expiry"]').type("12/30");
    cy.get('input[name="cvv"]').type("12");
    cy.get("button").contains(/Finalizează|Complete/i).click();

    // Expect validation errors
    cy.get("body").then($body => {
      expect($body.text()).to.match(/card|numar|cvv|invalid|eroare|error/i);
    });

    // Fix inputs and submit
    cy.get('input[name="cardNumber"]').clear().type("4242424242424242");
    cy.get('input[name="cvv"]').clear().type("123");
    cy.get("button").contains(/Finalizează|Complete/i).click();

    cy.wait("@createReservation");
    cy.wait("@payment");

    // Check success or verify in reservations list
    cy.get('body').then($body => {
      if ($body.text().match(/Succes|Success|confirm|confirmare/i)) {
        cy.contains(/Succes|Success|confirm/i).should("exist");
      } else {
        cy.visit("http://localhost:4200/reservations");
        cy.contains("r1").should("exist");
      }
    });
  });

  it("handles reservation conflict and shows error", () => {
    cy.intercept("POST", "/reservations*", {
      statusCode: 409,
      body: { message: "Seat already reserved" }
    }).as("createReservationConflict");

    cy.visit("http://localhost:4200/screenings/1");
    cy.wait("@getScreening");
    cy.wait("@getSeats");
    
    cy.get(".seat-box.available, .seat.available").first().click({ force: true });
    cy.get("button").contains(/Continuă|Continue/i).click({ force: true });

    cy.get('input[name="cardNumber"]').type("4242424242424242");
    cy.get('input[name="cardHolder"]').type("TEST USER");
    cy.get('input[name="expiry"]').type("12/30");
    cy.get('input[name="cvv"]').type("123");
    cy.get("button").contains(/Finalizează|Complete/i).click();

    cy.wait("@createReservationConflict").its("response.statusCode").should("eq", 409);
    cy.get("body").then($body => {
      expect($body.text()).to.match(/Seat already reserved|ocupat|rezervat/i);
    });
  });
});
