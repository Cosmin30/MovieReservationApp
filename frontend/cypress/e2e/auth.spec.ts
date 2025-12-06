/// <reference types="cypress" />
describe("Auth: login/logout", () => {
  beforeEach(() => {
    cy.intercept("POST", "/auth/login", {
      statusCode: 200,
      body: { token: "fake-jwt-token", user: { id: "1", name: "Test User", email: "test@example.com" } }
    }).as("login");
  });

  it("logs in successfully and stores token", () => {
    cy.visit("http://localhost:4200/login");
    cy.get('input[name="email"]').type("test@example.com");
    cy.get('input[name="password"]').type("password");
    cy.get('button[type="submit"]').click();
    cy.wait("@login");
    cy.url().should("include", "/movies");
    cy.window().then(win => {
      const token = win.localStorage.getItem("token") || win.localStorage.getItem("authToken") || win.sessionStorage.getItem("token");
      expect(token).to.exist;
    });
  });

  it("shows validation errors for empty fields", () => {
    cy.visit("http://localhost:4200/login");
    cy.get('button[type="submit"]').click();
    cy.contains(/email|required|obligatoriu/i).should("exist");
    cy.contains(/password|required|obligatoriu/i).should("exist");
  });

  it("shows error message on failed login", () => {
    cy.intercept("POST", "/auth/login", {
      statusCode: 401,
      body: { message: "Invalid credentials" }
    }).as("loginFailed");

    cy.visit("http://localhost:4200/login");
    cy.get('input[name="email"]').type("wrong@example.com");
    cy.get('input[name="password"]').type("wrongpass");
    cy.get('button[type="submit"]').click();
    cy.wait("@loginFailed");
    cy.contains(/Invalid|incorrect|credentiale/i).should("exist");
    cy.url().should("include", "/login");
  });

  it("handles server error gracefully", () => {
    cy.intercept("POST", "/auth/login", { statusCode: 500, body: { message: "Server error" } }).as("loginError");

    cy.visit("http://localhost:4200/login");
    cy.get('input[name="email"]').type("test@example.com");
    cy.get('input[name="password"]').type("password");
    cy.get('button[type="submit"]').click();
    cy.wait("@loginError");
    cy.contains(/server error|eroare/i).should("exist");
  });

  it("logs out and clears token", () => {
    // First login
    cy.visit("http://localhost:4200/login");
    cy.get('input[name="email"]').type("test@example.com");
    cy.get('input[name="password"]').type("password");
    cy.get('button[type="submit"]').click();
    cy.wait("@login");

    // Then logout
    cy.get("header").contains(/Logout|Sign out|Deconectare/i).click({ force: true });
    cy.url().should("include", "/login");
    cy.window().then(win => {
      const token = win.localStorage.getItem("token") || win.localStorage.getItem("authToken") || win.sessionStorage.getItem("token");
      expect(token).to.be.null;
    });
  });
});
