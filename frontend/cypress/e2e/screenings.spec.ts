/// <reference types="cypress" />
describe("Screenings", () => {
  beforeEach(() => {
    cy.intercept("GET", "/movies", { fixture: "movies.json" }).as("getMovies");
    cy.intercept("GET", "/screenings/*", { fixture: "screening.json" }).as("getScreening");
  });

  it("opens movie details and shows screenings", () => {
    cy.visit("http://localhost:4200/movies");
    cy.wait("@getMovies");
    cy.get(".movie-card").first().click();
    cy.wait("@getScreening");
    cy.get(".screening-card").should("exist");
  });
});
