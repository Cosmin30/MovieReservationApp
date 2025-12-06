/// <reference types="cypress" />

describe('User Registration', () => {
  beforeEach(() => {
    cy.clearLocalStorage();
    cy.clearCookies();
  });

  describe('Successful Registration', () => {
    it('should complete full registration flow', () => {
      const timestamp = Date.now();
      const email = `newuser${timestamp}@example.com`;
      
      cy.intercept('POST', '**/api/auth/register', {
        statusCode: 200,
        body: {
          token: 'fake-jwt-token-' + timestamp,
          role: 'USER',
          userId: 'user-' + timestamp
        }
      }).as('registerRequest');

      cy.intercept('GET', '**/api/movies**', {
        statusCode: 200,
        body: []
      }).as('getMovies');

      // Navigate to register page
      cy.visit('/register');
      cy.url().should('include', '/register');
      
      // Verify page elements
      cy.contains('Movie Reservation').should('be.visible');
      cy.contains(/creare.*cont|create.*account/i).should('be.visible');
      
      // Fill registration form
      cy.get('input[name="fullName"]').type('John Doe');
      cy.get('input[name="email"]').type(email);
      cy.get('input[name="password"]').type('securePass123');
      
      // Verify submit button is enabled and has correct text BEFORE clicking
      // Use specific selector for register button to avoid confusion with login button
      cy.get('button.btn-register, button[type="submit"].btn-register').should('not.be.disabled');
      cy.get('button.btn-register, button[type="submit"].btn-register').should('contain.text', 'Creează');
      
      // Submit form
      cy.get('button.btn-register, button[type="submit"].btn-register').click();
      
      // Wait for registration
      cy.wait('@registerRequest');
      
      // Register redirects to login after 1.5 seconds (as per code)
      cy.wait(2000);
      cy.url().should('include', '/login');
      
      // Note: Register doesn't auto-login, user needs to login manually
      cy.contains(/success|succes|cont.*creat/i).should('exist');
    });

    it('should allow navigation to login from register page', () => {
      cy.visit('/register');
      cy.get('a[routerLink="/login"], a[href="/login"]').should('be.visible');
      cy.get('a[routerLink="/login"], a[href="/login"]').click();
      cy.url().should('include', '/login');
    });
  });

  describe('Form Validation', () => {
    beforeEach(() => {
      cy.visit('/register');
    });

    it('should validate full name field', () => {
      // Test empty
      cy.get('input[name="fullName"]').focus().blur();
      cy.contains(/nume.*obligatoriu|name.*required/i).should('exist');
      
      // Test too short
      cy.get('input[name="fullName"]').clear().type('A');
      cy.get('input[name="fullName"]').blur();
      cy.contains(/nume.*minim.*2|name.*minimum.*2/i).should('exist');
      
      // Test valid
      cy.get('input[name="fullName"]').clear().type('John Doe');
      cy.get('input[name="fullName"]').should('not.have.class', 'is-invalid');
    });

    it('should validate email field', () => {
      // Test empty
      cy.get('input[name="email"]').focus().blur();
      cy.contains(/email.*obligatoriu|email.*required/i).should('exist');
      
      // Test invalid format
      cy.get('input[name="email"]').clear().type('invalid-email');
      cy.get('input[name="email"]').blur();
      cy.contains(/email.*valid|email.*format/i).should('exist');
      
      // Test valid
      cy.get('input[name="email"]').clear().type('test@example.com');
      cy.get('input[name="email"]').should('not.have.class', 'is-invalid');
    });

    it('should validate password field', () => {
      // Test empty
      cy.get('input[name="password"]').focus().blur();
      cy.contains(/parola.*obligatorie|password.*required/i).should('exist');
      
      // Test too short
      cy.get('input[name="password"]').clear().type('123');
      cy.get('input[name="password"]').blur();
      cy.contains(/parola.*minim.*6|password.*minimum.*6/i).should('exist');
      
      // Test valid
      cy.get('input[name="password"]').clear().type('password123');
      cy.get('input[name="password"]').should('not.have.class', 'is-invalid');
    });

    it('should disable submit button when form is invalid', () => {
      // Empty form
      cy.get('button[type="submit"]').should('be.disabled');
      
      // Partial fill
      cy.get('input[name="fullName"]').type('John');
      cy.get('button[type="submit"]').should('be.disabled');
      
      // Complete but invalid
      cy.get('input[name="email"]').type('invalid');
      cy.get('input[name="password"]').type('123');
      cy.get('button[type="submit"]').should('be.disabled');
      
      // Valid form
      cy.get('input[name="email"]').clear().type('test@example.com');
      cy.get('input[name="password"]').clear().type('password123');
      cy.get('button[type="submit"]').should('not.be.disabled');
    });
  });

  describe('Error Handling', () => {
    it('should handle email already exists error', () => {
      cy.intercept('POST', '**/api/auth/register', {
        statusCode: 409,
        body: { message: 'Email already exists' }
      }).as('registerFailed');

      cy.visit('/register');
      cy.get('input[name="fullName"]').type('John Doe');
      cy.get('input[name="email"]').type('existing@example.com');
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').click();
      
      cy.wait('@registerFailed');
      cy.contains(/already.*exists|deja.*exista|email.*folosit/i).should('exist');
      cy.url().should('include', '/register');
    });

    it('should handle server error', () => {
      cy.intercept('POST', '**/api/auth/register', {
        statusCode: 500,
        body: { message: 'Internal server error' }
      }).as('serverError');

      cy.visit('/register');
      cy.get('input[name="fullName"]').type('John Doe');
      cy.get('input[name="email"]').type('test@example.com');
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').click();
      
      cy.wait('@serverError');
      cy.contains(/server.*error|eroare/i).should('exist');
    });

    it('should handle network error', () => {
      cy.intercept('POST', '**/api/auth/register', {
        forceNetworkError: true
      }).as('networkError');

      cy.visit('/register');
      cy.get('input[name="fullName"]').type('John Doe');
      cy.get('input[name="email"]').type('test@example.com');
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').click();
      
      cy.contains(/error|eroare|network/i).should('exist');
    });
  });

  describe('User Experience', () => {
    it('should show loading state during registration', () => {
      cy.intercept('POST', '**/api/auth/register', {
        delay: 1000,
        statusCode: 200,
        body: {
          token: 'fake-jwt-token',
          role: 'USER',
          userId: 'user-123'
        }
      }).as('registerRequest');

      cy.visit('/register');
      cy.get('input[name="fullName"]').type('John Doe');
      cy.get('input[name="email"]').type('test@example.com');
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').click();
      
      // Button should be disabled during request
      cy.get('button[type="submit"]').should('be.disabled');
      
      cy.wait('@registerRequest');
    });

    it('should preserve form data on validation error', () => {
      cy.visit('/register');
      cy.get('input[name="fullName"]').type('John Doe');
      cy.get('input[name="email"]').type('test@example.com');
      cy.get('input[name="password"]').type('123');
      
      // Button should be disabled because password is too short
      cy.get('button[type="submit"]').should('be.disabled');
      
      // Try to submit with force (even though disabled)
      cy.get('button[type="submit"]').click({ force: true });
      
      // Form data should still be there
      cy.get('input[name="fullName"]').should('have.value', 'John Doe');
      cy.get('input[name="email"]').should('have.value', 'test@example.com');
      cy.get('input[name="password"]').should('have.value', '123');
    });
  });
});

