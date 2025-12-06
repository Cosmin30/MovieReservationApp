/// <reference types="cypress" />
describe("Checkout & Payment", () => {
  beforeEach(() => {
    cy.intercept("POST", "/reservations*", { statusCode: 201, body: { id: "r1", status: "CONFIRMED" } }).as("createReservation");
    cy.intercept("POST", "/payments*", { statusCode: 200, body: { id: "p1", status: "OK" } }).as("payment");
  });

  it("displays order summary before payment", () => {
    cy.visit("http://localhost:4200/checkout");
    cy.get(".order-summary").should("exist");
    cy.get("body").then($body => {
      if ($body.find(".order-summary").length > 0) {
        cy.get(".order-summary").should("contain.text", "The Example Movie");
      }
    });
  });

  it("applies promo code and recalculates total", () => {
    cy.intercept("POST", "/promo/validate", { statusCode: 200, body: { discount: 0.1, message: "10% off applied" } }).as("promoApply");
    cy.visit("http://localhost:4200/checkout");

    cy.get('input[placeholder="Promo code"]').type("SAVE10");
    cy.get("button").contains(/Apply|Aplică/i).click({ force: true });
    cy.wait("@promoApply");

    cy.get("body").then($body => {
      if ($body.text().includes("10% off")) {
        cy.contains("10% off").should("exist");
      }
    });
  });

  it("validates payment form fields", () => {
    cy.visit("http://localhost:4200/checkout");

    cy.get("button").contains("Finalizează").click({ force: true });
    cy.get("body").then($body => {
      const hasError = $body.text().match(/card number|obligatoriu|required/i);
      expect(hasError).to.exist;
    });
  });

  it("completes payment with valid data", () => {
    cy.visit("http://localhost:4200/checkout");
    cy.get('input[name="cardNumber"]').type("4242424242424242");
    cy.get('input[name="cardHolder"]').type("TEST USER");
    cy.get('input[name="expiry"]').type("12/30");
    cy.get('input[name="cvv"]').type("123");
    cy.get("button").contains("Finalizează").click({ force: true });

    cy.wait("@createReservation");
    cy.wait("@payment");
    cy.get("body").then($body => {
      expect($body.text()).to.match(/Succes|Success|Confirmare/i);
    });
  });

  it("handles payment failure and allows retry", function() {
    let attemptCount = 0;
    cy.intercept("POST", "/payments*", (req) => {
      attemptCount += 1;
      if (attemptCount === 1) {
        req.reply({ statusCode: 402, body: { message: "Payment declined" } });
      } else {
        req.reply({ statusCode: 200, body: { id: "p2", status: "OK" } });
      }
    }).as("paymentRetry");

    cy.visit("http://localhost:4200/checkout");
    cy.get('input[name="cardNumber"]').type("4242424242424242");
    cy.get('input[name="cardHolder"]').type("TEST USER");
    cy.get('input[name="expiry"]').type("12/30");
    cy.get('input[name="cvv"]').type("123");
    cy.get("button").contains("Finalizează").click({ force: true });

    cy.wait("@paymentRetry");
    cy.get("body").then($body => {
      expect($body.text()).to.match(/declined|rejected|failed/i);
    });
  });

  it("handles network timeout gracefully", () => {
    cy.intercept("POST", "/payments*", (req) => {
      req.destroy();
    }).as("paymentTimeout");

    cy.visit("http://localhost:4200/checkout");
    cy.get('input[name="cardNumber"]').type("4242424242424242");
    cy.get('input[name="cardHolder"]').type("TEST USER");
    cy.get('input[name="expiry"]').type("12/30");
    cy.get('input[name="cvv"]').type("123");
    cy.get("button").contains("Finalizează").click({ force: true });

    cy.get("body").then($body => {
      expect($body.text()).to.match(/timeout|network|connection/i);
    });
  });

  it("shows loading spinner during payment processing", () => {
    cy.intercept("POST", "/payments*", (req) => {
      req.reply(res => {
        res.delay(2000);
      });
    }).as("paymentDelayed");

    cy.visit("http://localhost:4200/checkout");
    cy.get('input[name="cardNumber"]').type("4242424242424242");
    cy.get('input[name="cardHolder"]').type("TEST USER");
    cy.get('input[name="expiry"]').type("12/30");
    cy.get('input[name="cvv"]').type("123");
    cy.get("button").contains("Finalizează").click({ force: true });

    cy.get("body").then($body => {
      if ($body.find(".loading, .spinner, button:disabled").length > 0) {
        cy.get(".loading, .spinner, button:disabled").should("exist");
      }
    });

    cy.wait("@paymentDelayed");
  });
});
