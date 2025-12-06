/// <reference types="cypress" />
describe("Profile page", () => {
  beforeEach(() => {
    cy.intercept("GET", "/auth/me", {
      statusCode: 200,
      body: { id: "1", name: "Test User", email: "test@example.com", phone: "+40123456789", preferences: { newsletter: true } }
    }).as("getMe");
  });

  it("loads profile info", () => {
    cy.visit("http://localhost:4200/profile");
    cy.wait("@getMe");
    cy.contains("Test User").should("exist");
    cy.contains("test@example.com").should("exist");
  });

  it("allows editing profile name and phone", () => {
    cy.intercept("PUT", "/auth/me", {
      statusCode: 200,
      body: { id: "1", name: "Updated User", email: "test@example.com", phone: "+40987654321" }
    }).as("updateProfile");

    cy.visit("http://localhost:4200/profile");
    cy.wait("@getMe");

    cy.get('input[name="name"]').clear().type("Updated User");
    cy.get('input[name="phone"]').clear().type("+40987654321");
    cy.get("button").contains(/Save|Salvează/i).click();
    cy.wait("@updateProfile");
    cy.contains(/Saved|success/i).should("exist");
  });

  it("shows validation errors on update", () => {
    cy.visit("http://localhost:4200/profile");
    cy.wait("@getMe");

    cy.get('input[name="name"]').clear(); // empty
    cy.get("button").contains(/Save|Salvează/i).click();
    cy.contains(/required|obligatoriu/i).should("exist");

    cy.get('input[name="phone"]').type("invalid"); // bad format
    cy.get("button").contains(/Save|Salvează/i).click();
    cy.contains(/phone|format|valid/i).should("exist");
  });

  it("allows changing password", () => {
    cy.intercept("POST", "/auth/change-password", {
      statusCode: 200,
      body: { message: "Password updated" }
    }).as("changePass");

    cy.visit("http://localhost:4200/profile");
    cy.wait("@getMe");

    cy.get("button").contains(/Change Password|Schimbă parola/i).click({ force: true });
    cy.get(".password-form").should("exist");
    cy.get('input[name="oldPassword"]').type("oldpass");
    cy.get('input[name="newPassword"]').type("newpass123");
    cy.get('input[name="confirmPassword"]').type("newpass123");
    cy.get("button").contains(/Update|Actualizează/i).click();
    cy.wait("@changePass");
    cy.contains(/success|updated/i).should("exist");
  });

  it("validates password change (mismatch)", () => {
    cy.visit("http://localhost:4200/profile");
    cy.wait("@getMe");

    cy.get("button").contains(/Change Password|Schimbă parola/i).click({ force: true });
    cy.get('input[name="newPassword"]').type("newpass123");
    cy.get('input[name="confirmPassword"]').type("newpass456"); // mismatch
    cy.get("button").contains(/Update|Actualizează/i).click();
    cy.contains(/not match|do not match|nu se potrivesc/i).should("exist");
  });

  it("toggles newsletter preference", () => {
    cy.intercept("PUT", "/auth/preferences", {
      statusCode: 200,
      body: { preferences: { newsletter: false } }
    }).as("updatePref");

    cy.visit("http://localhost:4200/profile");
    cy.wait("@getMe");

    cy.get('input[type="checkbox"][name="newsletter"]').uncheck();
    cy.wait("@updatePref");
    cy.get('input[type="checkbox"][name="newsletter"]').should("not.be.checked");
  });

  it("handles profile load error and shows retry", () => {
    cy.intercept("GET", "/auth/me", { statusCode: 500, body: { message: "Error" } }).as("getMeErr");
    cy.visit("http://localhost:4200/profile");
    cy.wait("@getMeErr");
    cy.contains(/error|eroare/i).should("exist");
    cy.get("button").contains(/Retry|Încearcă din nou/i).click({ force: true });
    cy.wait("@getMeErr");
  });
});
