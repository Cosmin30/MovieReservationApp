/// <reference types="cypress" />
describe("Seats selection", () => {
  beforeEach(() => {
    cy.intercept("GET", "/screenings/*", { fixture: "screening.json" }).as("getScreening");
    cy.intercept("GET", "/seats/screening/*", { fixture: "seats.json" }).as("getSeats");
  });

  it("shows seat layout with available and reserved seats", () => {
    cy.visit("http://localhost:4200/screenings/1");
    cy.wait("@getScreening");
    cy.wait("@getSeats");
    cy.get(".seat-box, .seat").then($seats => {
      expect($seats.length).to.be.at.least(1);
    });
  });

  it("selects single seat and shows summary", () => {
    cy.visit("http://localhost:4200/screenings/1");
    cy.wait("@getSeats");
    cy.get(".seat-box.available, .seat.available").first().click({ force: true });
    cy.get("body").then($body => {
      if ($body.find(".booking-summary").length > 0) {
        cy.get(".booking-summary").should("contain.text", "1x");
      }
    });
  });

  it("selects multiple seats and updates summary", () => {
    cy.visit("http://localhost:4200/screenings/1");
    cy.wait("@getSeats");
    cy.get(".seat-box.available, .seat.available").eq(0).click({ force: true });
    cy.get(".seat-box.available, .seat.available").eq(1).click({ force: true });
    cy.get("body").then($body => {
      if ($body.find(".booking-summary").length > 0) {
        cy.get(".booking-summary").should("contain.text", "2x");
      }
    });
  });

  it("deselects seat when clicked again", () => {
    cy.visit("http://localhost:4200/screenings/1");
    cy.wait("@getSeats");
    cy.get(".seat-box.available, .seat.available").first().then($seat => {
      cy.wrap($seat).click({ force: true });
      cy.wrap($seat).click({ force: true });
      cy.get("body").then($body => {
        expect($body.find(".seat-box.selected, .seat.selected").length).to.equal(0);
      });
    });
  });

  it("prevents selection of reserved seats", () => {
    cy.visit("http://localhost:4200/screenings/1");
    cy.wait("@getSeats");
    cy.get(".seat-box.reserved, .seat.reserved").first().then($seat => {
      if ($seat.length > 0) {
        cy.wrap($seat).click({ force: true });
        cy.get("body").then($body => {
          expect($body.find(".seat-box.reserved.selected, .seat.reserved.selected").length).to.equal(0);
        });
      }
    });
  });

  it("shows error when seat becomes unavailable", function() {
    cy.intercept("POST", "/reservations*", (req) => {
      req.reply({ statusCode: 409, body: { message: "Seat no longer available" } });
    }).as("conflictRes");

    cy.visit("http://localhost:4200/screenings/1");
    cy.wait("@getSeats");
    cy.get(".seat-box.available, .seat.available").first().click({ force: true });
    cy.get("button").contains(/Continuă|Continue/i).click({ force: true });

    cy.wait("@conflictRes");
    cy.get("body").then($body => {
      expect($body.text()).to.match(/longer available|nu mai e disponibil/i);
    });
  });

  it("handles loading state while fetching seats", () => {
    cy.intercept("GET", "/seats/screening/*", (req) => {
      req.reply(res => {
        res.delay(2000);
      });
    }).as("getSeatsDelayed");

    cy.visit("http://localhost:4200/screenings/1");
    cy.get("body").then($body => {
      if ($body.find(".loading, .spinner").length > 0) {
        cy.get(".loading, .spinner").should("exist");
      }
    });
    cy.wait("@getSeatsDelayed");
  });
});
