/// <reference types="cypress" />
describe("Movies list", () => {
  beforeEach(() => {
    cy.intercept("GET", "/movies", { fixture: "movies.json" }).as("getMovies");
  });

  it("shows a list of movies and opens details", () => {
    cy.visit("http://localhost:4200/movies");
    cy.wait("@getMovies");
    cy.get(".movie-card").should("have.length.at.least", 1);
    cy.contains("The Example Movie").should("exist");
    cy.get(".movie-card").first().click();
    cy.url().should("include", "/movies/");
    cy.get(".movie-details").should("exist");
  });

  it("filters and sorts movies", () => {
    // intercept filter/search endpoint
    cy.intercept("GET", "/movies?genre=action", { fixture: "movies-action.json" }).as("getAction");
    cy.visit("http://localhost:4200/movies");
    cy.wait("@getMovies");

    // Open filters, select genre action
    cy.get(".filter-toggle").click({ force: true });
    cy.get('select[name="genre"]').select("action");
    cy.get("button").contains("Aplică filtre").click();
    cy.wait("@getAction");
    cy.get(".movie-card").should("have.length.at.least", 1);

    // Sort by rating desc (if select exists; optional)
    cy.get('select[name="sort"]').then($sel => {
      if ($sel.length > 0) {
        cy.wrap($sel).select("rating_desc");
        // Verify sort applied (basic check)
        cy.get(".movie-card").should("exist");
      }
    });
  });

  it("handles empty and error responses", () => {
    // Empty list
    cy.intercept("GET", "/movies?search=none", { statusCode: 200, body: [] }).as("getEmpty");
    cy.visit("http://localhost:4200/movies");
    cy.get('input[placeholder="Caută"]').type("none{enter}");
    cy.wait("@getEmpty");
    cy.get("body").then($body => {
      const hasEmptyMsg = $body.text().match(/Niciun film|No results|Nu s-au găsit/i);
      expect(hasEmptyMsg).to.exist;
    });

    // Server error
    cy.intercept("GET", "/movies", { statusCode: 500, body: { message: "Server error" } }).as("getMoviesErr");
    cy.visit("http://localhost:4200/movies");
    cy.wait("@getMoviesErr");
    cy.contains(/eroare|error|server/i).should("exist");
  });
});
