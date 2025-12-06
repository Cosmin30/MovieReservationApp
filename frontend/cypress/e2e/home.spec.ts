/// <reference types="cypress" />
describe("Home page", () => {
  it("loads home with header and footer elements", () => {
    cy.visit("http://localhost:4200/");
    cy.get("header").should("exist");
    cy.get("footer").should("exist");
    cy.get("nav a").should("have.length.at.least", 2);
  });

  it("displays featured movies on home", () => {
    cy.intercept("GET", "/movies?featured=true", { fixture: "featured-movies.json" }).as("getFeatured");
    cy.visit("http://localhost:4200/");
    cy.wait("@getFeatured");
    cy.get(".featured-movie, .movie-card").should("have.length.at.least", 1);
  });

  it("navigates to movies from home link", () => {
    cy.visit("http://localhost:4200/");
    cy.get("a").contains(/Movies|Filme|Browse/i).click();
    cy.url().should("include", "/movies");
  });

  it("handles empty featured movies gracefully", () => {
    cy.intercept("GET", "/movies?featured=true", { statusCode: 200, body: [] }).as("getEmptyFeatured");
    cy.visit("http://localhost:4200/");
    cy.wait("@getEmptyFeatured");
    cy.get("body").then($body => {
      const hasEmptyMsg = $body.text().match(/No featured|Nu s-au găsit/i);
      if (hasEmptyMsg) {
        cy.contains(/No featured|Nu s-au găsit/i).should("exist");
      } else {
        cy.get(".featured-movie, .movie-card").should("not.exist");
      }
    });
  });
});
