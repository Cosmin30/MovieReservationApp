/// <reference types="cypress" />
describe("Payment flow", () => {
  beforeEach(() => {
    cy.intercept("POST", "/reservations*", {
      statusCode: 201,
      body: { id: "r1", status: "CONFIRMED" }
    }).as("createReservation");
    cy.intercept("POST", "/payments*", { statusCode: 200, body: { id: "p1", status: "OK" } }).as("payment");
  });

  it("completes payment and shows success", () => {
    cy.visit("http://localhost:4200/checkout");
    cy.get('input[name="cardNumber"]').type("4242424242424242");
    cy.get('input[name="cardHolder"]').type("ION POPESCU");
    cy.get('input[name="expiry"]').type("12/30");
    cy.get('input[name="cvv"]').type("123");
    cy.get("button").contains(/Finaliz/i).click();
    cy.wait("@createReservation");
    cy.wait("@payment");
    cy.contains(/Succes|Success|confirm/i).should("exist");
  });
});
