/// <reference types="cypress" />

declare global {
  namespace Cypress {
    interface Chainable {
      /**
       * Custom command to login a user
       * @example cy.login('test@example.com', 'password123')
       */
      login(email: string, password: string): Chainable<void>
      
      /**
       * Custom command to register a new user
       * @example cy.register('John Doe', 'test@example.com', 'password123')
       */
      register(fullName: string, email: string, password: string): Chainable<void>
      
      /**
       * Custom command to logout
       * @example cy.logout()
       */
      logout(): Chainable<void>
      
      /**
       * Custom command to check if user is authenticated
       * @example cy.isAuthenticated()
       */
      isAuthenticated(): Chainable<boolean>
    }
  }
}

Cypress.Commands.add('login', (email: string, password: string) => {
  cy.intercept('POST', '**/api/auth/login', (req) => {
    req.reply({
      statusCode: 200,
      body: {
        token: 'fake-jwt-token-' + Date.now(),
        role: 'USER',
        userId: 'user-123'
      }
    });
  }).as('loginRequest');

  cy.visit('/login');
  cy.get('input[name="email"]').type(email);
  cy.get('input[name="password"]').type(password);
  cy.get('button[type="submit"]').click();
  cy.wait('@loginRequest');
  cy.url().should('not.include', '/login');
});

Cypress.Commands.add('register', (fullName: string, email: string, password: string) => {
  cy.intercept('POST', '**/api/auth/register', (req) => {
    req.reply({
      statusCode: 200,
      body: {
        token: 'fake-jwt-token-' + Date.now(),
        role: 'USER',
        userId: 'user-123'
      }
    });
  }).as('registerRequest');

  cy.visit('/register');
  cy.get('input[name="fullName"]').type(fullName);
  cy.get('input[name="email"]').type(email);
  cy.get('input[name="password"]').type(password);
  cy.get('button[type="submit"]').click();
  cy.wait('@registerRequest');
  cy.url().should('not.include', '/register');
});

Cypress.Commands.add('logout', () => {
  cy.get('.logout-btn, button').contains(/logout|Logout/i).click({ force: true });
  cy.url().should('include', '/login');
  cy.window().then((win) => {
    expect(win.localStorage.getItem('token')).to.be.null;
  });
});

Cypress.Commands.add('isAuthenticated', () => {
  return cy.window().then((win) => {
    const token = win.localStorage.getItem('token');
    return token !== null && token !== undefined;
  });
});

export {};
