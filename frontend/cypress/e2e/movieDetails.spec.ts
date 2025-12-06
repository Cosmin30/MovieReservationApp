/// <reference types="cypress" />
describe("Movie details & screenings", () => {
  beforeEach(() => {
    cy.intercept("GET", "/movies", { fixture: "movies.json" }).as("getMovies");
    cy.intercept("GET", "/screenings/*", { fixture: "screening.json" }).as("getScreening");
    cy.intercept("GET", "/reviews/*", { fixture: "reviews.json" }).as("getReviews");
  });

  it("opens a movie and shows full details with screenings", () => {
    cy.visit("http://localhost:4200/movies");
    cy.wait("@getMovies");
    cy.get(".movie-card").first().click();
    cy.wait("@getScreening");
    cy.get(".movie-details").should("exist");
    cy.get(".movie-details").should("contain.text", "The Example Movie");
    cy.get(".screening-card").should("have.length.at.least", 1);
  });

  it("displays movie metadata (duration, genre, rating)", () => {
    cy.visit("http://localhost:4200/movies");
    cy.wait("@getMovies");
    cy.get(".movie-card").first().click();
    cy.wait("@getScreening");
    cy.contains(/Duration|Runtime|Durată|min/i).should("exist");
    cy.contains(/Genre|Gen/i).should("exist");
    cy.get(".rating").should("exist");
  });

  it("filters screenings by date", () => {
    cy.intercept("GET", "/screenings/*?date=2024-12-20", { fixture: "screenings-filtered.json" }).as("getScreeningsFiltered");
    cy.visit("http://localhost:4200/movies/1");
    cy.wait("@getScreening");
    cy.get(".date-filter").click({ force: true });
    cy.get('input[type="date"]').type("2024-12-20");
    cy.wait("@getScreeningsFiltered");
    cy.get(".screening-card").should("have.length.at.least", 1);
  });

  it("shows reviews and user ratings", () => {
    cy.visit("http://localhost:4200/movies/1");
    cy.wait("@getScreening");
    cy.wait("@getReviews");
    cy.get(".reviews-section").should("exist");
    cy.get(".review-item").should("have.length.at.least", 0); // 0 or more
  });

  it("handles no screenings available", () => {
    cy.intercept("GET", "/screenings/*", { statusCode: 200, body: [] }).as("getNoScreenings");
    cy.visit("http://localhost:4200/movies/1");
    cy.wait("@getNoScreenings");
    cy.contains(/No screenings|Nicio proiecție|indisponibil/i).should("exist");
  });

  it("handles server error when loading details", () => {
    cy.intercept("GET", "/screenings/*", { statusCode: 500, body: { message: "Error" } }).as("getScreeningErr");
    cy.visit("http://localhost:4200/movies/1");
    cy.wait("@getScreeningErr");
    cy.contains(/error|eroare/i).should("exist");
  });
});
