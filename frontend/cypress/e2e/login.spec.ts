/// <reference types="cypress" />
describe("Auth & redirect", () => {
  beforeEach(() => {
    cy.intercept("POST", "/auth/login", {
      statusCode: 200,
      body: { token: "fake-jwt-token", user: { id: "1", name: "Test User", email: "test@example.com" } }
    }).as("login");

    cy.intercept("GET", "/movies", { fixture: "movies.json" }).as("getMovies");
  });

  it("logs in and navigates to movies list", () => {
    cy.visit("/login");
    cy.get('input[name="email"]').type("test@example.com");
    cy.get('input[name="password"]').type("password");
    cy.get('button[type="submit"]').click();
    cy.wait("@login");
    cy.url().should("include", "/movies");
    cy.get(".movie-card").should("exist");
  });
});
