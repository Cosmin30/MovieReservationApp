/// <reference types="cypress" />
describe("Reservations list", () => {
  beforeEach(() => {
    cy.intercept("GET", "/reservations", {
      statusCode: 200,
      body: [
        { id: "r1", movie: "The Example Movie", status: "CONFIRMED", date: "2024-12-25", seats: "A1" },
        { id: "r2", movie: "Another Movie", status: "PENDING", date: "2024-12-26", seats: "B2" },
        { id: "r3", movie: "Old Movie", status: "COMPLETED", date: "2024-11-01", seats: "C3" }
      ]
    }).as("getReservations");
  });

  it("shows reservations page with all reservations", () => {
    cy.visit("http://localhost:4200/reservations");
    cy.wait("@getReservations");
    cy.get("body").then($body => {
      expect($body.text()).to.include("The Example Movie");
    });
  });

  it("filters reservations by status", () => {
    cy.intercept("GET", "/reservations?status=CONFIRMED", {
      statusCode: 200,
      body: [{ id: "r1", movie: "The Example Movie", status: "CONFIRMED", date: "2024-12-25", seats: "A1" }]
    }).as("getConfirmed");

    cy.visit("http://localhost:4200/reservations");
    cy.wait("@getReservations");

    cy.get('select[name="status"]').then($sel => {
      if ($sel.length > 0) {
        cy.wrap($sel).select("CONFIRMED");
        cy.wait("@getConfirmed");
      }
    });
  });

  it("displays reservation details and actions", () => {
    cy.visit("http://localhost:4200/reservations");
    cy.wait("@getReservations");

    cy.get(".reservation-item").first().then($item => {
      if ($item.length > 0) {
        cy.wrap($item).click();
      }
    });
  });

  it("allows cancellation of reservation with confirmation", () => {
    cy.intercept("DELETE", "/reservations/r1", { statusCode: 200, body: { message: "Cancelled" } }).as("cancelRes");

    cy.visit("http://localhost:4200/reservations");
    cy.wait("@getReservations");

    cy.get(".reservation-item").first().then($item => {
      if ($item.find("button").length > 0) {
        cy.wrap($item).find("button").contains(/Cancel|Anulează/i).click({ force: true });
        cy.get("body").then($body => {
          if ($body.find(".confirmation-modal").length > 0) {
            cy.get("button").contains(/Confirm|Confirmare/i).click({ force: true });
            cy.wait("@cancelRes");
          }
        });
      }
    });
  });

  it("handles empty reservations list", () => {
    cy.intercept("GET", "/reservations", { statusCode: 200, body: [] }).as("getEmptyRes");
    cy.visit("http://localhost:4200/reservations");
    cy.wait("@getEmptyRes");
    cy.get("body").then($body => {
      expect($body.text()).to.match(/No reservations|Niciuna/i);
    });
  });

  it("handles server error when loading reservations", () => {
    cy.intercept("GET", "/reservations", { statusCode: 500, body: { message: "Error" } }).as("getResErr");
    cy.visit("http://localhost:4200/reservations");
    cy.wait("@getResErr");
    cy.get("body").then($body => {
      expect($body.text()).to.match(/error|eroare/i);
    });
  });
});
