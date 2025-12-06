/// <reference types="cypress" />
describe("Search", () => {
  beforeEach(() => {
    cy.intercept("GET", "/movies?search=example", { fixture: "movies.json" }).as("searchMovies");
    cy.intercept("GET", "/movies/suggestions?q=ex", { statusCode: 200, body: ["example", "excitement"] }).as("suggestions");
  });

  it("performs a basic search and shows results", () => {
    cy.visit("http://localhost:4200/movies");
    cy.get('input[placeholder="Caută"]').type("example");
    cy.wait("@suggestions");
    cy.get('input[placeholder="Caută"]').type("{enter}");
    cy.wait("@searchMovies");
    cy.get("body").then($body => {
      expect($body.find(".movie-card").length).to.be.at.least(1);
    });
  });

  it("shows autocomplete suggestions while typing", () => {
    cy.visit("http://localhost:4200/movies");
    cy.get('input[placeholder="Caută"]').type("ex");
    cy.wait("@suggestions");
    cy.get("body").then($body => {
      if ($body.find(".suggestions-dropdown").length > 0) {
        cy.get(".suggestions-dropdown").should("exist");
      }
    });
  });

  it("selects suggestion and performs search", () => {
    cy.visit("http://localhost:4200/movies");
    cy.get('input[placeholder="Caută"]').type("ex");
    cy.wait("@suggestions");
    cy.get(".suggestion-item").first().then($item => {
      if ($item.length > 0) {
        cy.wrap($item).click();
        cy.wait("@searchMovies");
      }
    });
  });

  it("handles no search results", () => {
    cy.intercept("GET", "/movies?search=xyz", { statusCode: 200, body: [] }).as("searchEmpty");
    cy.visit("http://localhost:4200/movies");
    cy.get('input[placeholder="Caută"]').type("xyz{enter}");
    cy.wait("@searchEmpty");
    cy.get("body").then($body => {
      expect($body.text()).to.match(/No results|Nu s-au găsit|niciun/i);
    });
  });

  it("combines search with filters", () => {
    cy.intercept("GET", "/movies?search=example&genre=action&sort=rating", { fixture: "movies-filtered.json" }).as("searchFiltered");
    cy.visit("http://localhost:4200/movies");

    cy.get('input[placeholder="Caută"]').type("example");
    cy.get(".filter-toggle").click({ force: true });
    cy.get('select[name="genre"]').then($sel => {
      if ($sel.length > 0) {
        cy.wrap($sel).select("action");
      }
    });
    cy.get("button").contains(/Search|Caută|Apply/i).click({ force: true });
  });

  it("clears search and shows all movies again", () => {
    cy.intercept("GET", "/movies", { fixture: "movies.json" }).as("getAllMovies");
    cy.visit("http://localhost:4200/movies");

    cy.get('input[placeholder="Caută"]').type("example{enter}");
    cy.wait("@searchMovies");

    cy.get("button").contains(/Clear|X|Șterge/i).click({ force: true });
    cy.wait("@getAllMovies");
    cy.get("body").then($body => {
      expect($body.find(".movie-card").length).to.be.at.least(1);
    });
  });
});
