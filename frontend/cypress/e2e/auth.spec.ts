/// <reference types="cypress" />

describe('Authentication Flow', () => {
  beforeEach(() => {
    cy.clearLocalStorage();
    cy.clearCookies();
  });

  describe('Login', () => {
    it('should successfully login with valid credentials', () => {
      cy.intercept('POST', '**/api/auth/login', {
        statusCode: 200,
        body: {
          token: 'fake-jwt-token-123',
          role: 'USER',
          userId: 'user-123'
        }
      }).as('loginRequest');

      cy.visit('/login');
      
      // Check form is visible
      cy.get('form').should('be.visible');
      cy.get('input[name="email"]').should('be.visible');
      cy.get('input[name="password"]').should('be.visible');
      
      // Fill and submit form
      cy.get('input[name="email"]').type('test@example.com');
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').should('not.be.disabled');
      cy.get('button[type="submit"]').click();
      
      // Wait for login request
      cy.wait('@loginRequest');
      
      // Should redirect to movies or home
      cy.url().should('not.include', '/login');
      cy.url().should('match', /\/(movies|home)/);
      
      // Token should be stored
      cy.window().then((win) => {
        expect(win.localStorage.getItem('token')).to.exist;
        expect(win.localStorage.getItem('role')).to.equal('USER');
      });
    });

    it('should show validation errors for empty fields', () => {
      cy.visit('/login');
      
      // Button should be disabled when form is invalid
      cy.get('button[type="submit"]').should('be.disabled');
      
      // Try to interact with fields to trigger validation
      cy.get('input[name="email"]').focus().blur();
      cy.get('input[name="password"]').focus().blur();
      
      // Check validation messages appear
      cy.contains(/email.*obligatoriu|email.*required/i).should('exist');
      cy.contains(/parola.*obligatorie|password.*required/i).should('exist');
      
      // Fields should show invalid state
      cy.get('input[name="email"]').should('have.class', 'is-invalid');
      cy.get('input[name="password"]').should('have.class', 'is-invalid');
    });

    it('should show error for invalid email format', () => {
      cy.visit('/login');
      
      cy.get('input[name="email"]').type('invalid-email');
      cy.get('input[name="email"]').blur();
      
      cy.contains(/email.*valid|email.*format/i).should('exist');
    });

    it('should show error for short password', () => {
      cy.visit('/login');
      
      cy.get('input[name="password"]').type('123');
      cy.get('input[name="password"]').blur();
      
      cy.contains(/parola.*minim.*6|password.*minimum.*6/i).should('exist');
    });

    it('should show error message on failed login', () => {
      cy.intercept('POST', '**/api/auth/login', {
        statusCode: 401,
        body: { message: 'Invalid credentials' }
      }).as('loginFailed');

      cy.visit('/login');
      cy.get('input[name="email"]').type('wrong@example.com');
      cy.get('input[name="password"]').type('wrongpass');
      cy.get('button[type="submit"]').click();
      
      cy.wait('@loginFailed');
      cy.contains(/invalid|incorrect|credentiale|eroare/i).should('exist');
      cy.url().should('include', '/login');
    });

    it('should handle server error gracefully', () => {
      cy.intercept('POST', '**/api/auth/login', {
        statusCode: 500,
        body: { message: 'Server error' }
      }).as('serverError');

      cy.visit('/login');
      cy.get('input[name="email"]').type('test@example.com');
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').click();
      
      cy.wait('@serverError');
      cy.contains(/server.*error|eroare.*server/i).should('exist');
    });

    it('should redirect to home after successful login', () => {
      cy.intercept('POST', '**/api/auth/login', {
        statusCode: 200,
        body: {
          token: 'fake-jwt-token',
          role: 'USER',
          userId: 'user-123'
        }
      }).as('loginRequest');

      cy.intercept('GET', '**/api/users/me**', {
        statusCode: 200,
        body: {
          id: 'user-123',
          fullName: 'Test User',
          email: 'test@example.com',
          role: 'USER'
        }
      }).as('getUserProfile');

      cy.visit('/login');
      cy.get('input[name="email"]').type('test@example.com');
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').click();
      
      cy.wait('@loginRequest');
      cy.wait('@getUserProfile');
      cy.url().should('include', '/home');
    });
  });

  describe('Register', () => {
    it('should successfully register a new user', () => {
      const timestamp = Date.now();
      const email = `test${timestamp}@example.com`;
      
      cy.intercept('POST', '**/api/auth/register', {
        statusCode: 200,
        body: {
          token: 'fake-jwt-token-123',
          role: 'USER',
          userId: 'user-123'
        }
      }).as('registerRequest');

      cy.visit('/register');
      
      // Check form is visible
      cy.get('form').should('be.visible');
      cy.get('input[name="fullName"]').should('be.visible');
      cy.get('input[name="email"]').should('be.visible');
      cy.get('input[name="password"]').should('be.visible');
      
      // Fill and submit form
      cy.get('input[name="fullName"]').type('John Doe');
      cy.get('input[name="email"]').type(email);
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').should('not.be.disabled');
      cy.get('button[type="submit"]').click();
      
      // Wait for register request
      cy.wait('@registerRequest');
      
      // Register redirects to login after 1.5 seconds (as per code)
      cy.wait(2000);
      cy.url().should('include', '/login');
      
      // Note: Register doesn't auto-login, user needs to login manually
      cy.contains(/success|succes|cont.*creat/i).should('exist');
    });

    it('should show validation errors for empty fields', () => {
      cy.visit('/register');
      
      // Button should be disabled when form is invalid
      cy.get('button[type="submit"]').should('be.disabled');
      
      // Try to interact with fields to trigger validation
      cy.get('input[name="fullName"]').focus().blur();
      cy.get('input[name="email"]').focus().blur();
      cy.get('input[name="password"]').focus().blur();
      
      // Check validation messages appear
      cy.contains(/nume.*obligatoriu|name.*required/i).should('exist');
      cy.contains(/email.*obligatoriu|email.*required/i).should('exist');
      cy.contains(/parola.*obligatorie|password.*required/i).should('exist');
    });

    it('should show error for invalid email format', () => {
      cy.visit('/register');
      
      cy.get('input[name="email"]').type('invalid-email');
      cy.get('input[name="email"]').blur();
      
      cy.contains(/email.*valid|email.*format/i).should('exist');
    });

    it('should show error for short password', () => {
      cy.visit('/register');
      
      cy.get('input[name="password"]').type('123');
      cy.get('input[name="password"]').blur();
      
      cy.contains(/parola.*minim.*6|password.*minimum.*6/i).should('exist');
    });

    it('should show error for short full name', () => {
      cy.visit('/register');
      
      cy.get('input[name="fullName"]').type('A');
      cy.get('input[name="fullName"]').blur();
      
      cy.contains(/nume.*minim.*2|name.*minimum.*2/i).should('exist');
    });

    it('should show error if email already exists', () => {
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
    });

    it('should navigate to login page from register', () => {
      cy.visit('/register');
      cy.get('a[routerLink="/login"], a[href="/login"]').click();
      cy.url().should('include', '/login');
    });
  });

  describe('Logout', () => {
    beforeEach(() => {
      // Login first
      cy.intercept('POST', '**/api/auth/login', {
        statusCode: 200,
        body: {
          token: 'fake-jwt-token',
          role: 'USER',
          userId: 'user-123'
        }
      }).as('loginRequest');

      cy.intercept('GET', '**/api/users/me**', {
        statusCode: 200,
        body: {
          id: 'user-123',
          fullName: 'Test User',
          email: 'test@example.com',
          role: 'USER'
        }
      }).as('getUserProfile');

      cy.visit('/login');
      cy.get('input[name="email"]').type('test@example.com');
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').click();
      cy.wait('@loginRequest');
      cy.wait('@getUserProfile');
      
      // Wait for navigation to complete
      cy.url().should('not.include', '/login');
    });

    it('should logout and clear token', () => {
      // Find and click logout button by class or text
      cy.get('.logout-btn, button').contains(/logout|Logout/i).click({ force: true });
      
      // Should redirect to login
      cy.url().should('include', '/login');
      
      // Token should be cleared
      cy.window().then((win) => {
        expect(win.localStorage.getItem('token')).to.be.null;
        expect(win.localStorage.getItem('role')).to.be.null;
      });
    });

    it('should redirect to login when accessing protected routes after logout', () => {
      // Logout button should be available after login
      cy.get('.logout-btn, button').contains(/logout|Logout/i).should('be.visible');
      
      // Logout
      cy.get('.logout-btn, button').contains(/logout|Logout/i).click({ force: true });
      
      // Should redirect to login
      cy.url().should('include', '/login');
      
      // Wait for logout to complete
      cy.window().then((win) => {
        expect(win.localStorage.getItem('token')).to.be.null;
      });
      
      // Try to access protected route
      cy.visit('/movies');
      cy.url().should('include', '/login');
    });
  });

  describe('Navigation Guards', () => {
    it('should redirect authenticated users away from login page', () => {
      cy.intercept('POST', '**/api/auth/login', {
        statusCode: 200,
        body: {
          token: 'fake-jwt-token',
          role: 'USER',
          userId: 'user-123'
        }
      }).as('loginRequest');

      cy.visit('/login');
      cy.get('input[name="email"]').type('test@example.com');
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').click();
      cy.wait('@loginRequest');

      // Try to visit login page again
      cy.visit('/login');
      cy.url().should('not.include', '/login');
    });

    it('should redirect unauthenticated users to login', () => {
      cy.clearLocalStorage();
      cy.visit('/movies');
      cy.url().should('include', '/login');
    });
  });
});
