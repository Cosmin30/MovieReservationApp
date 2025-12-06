/// <reference types="cypress" />
describe("Basic UI smoke tests", () => {
  it("loads home and movies pages", () => {
    cy.visit("http://localhost:4200/");
    cy.get("header").should("exist");
    cy.visit("http://localhost:4200/movies");
    cy.get(".movie-card").should("exist");
  });
});
