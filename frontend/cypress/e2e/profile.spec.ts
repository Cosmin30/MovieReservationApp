/// <reference types="cypress" />

describe('User Profile', () => {
  beforeEach(() => {
    // Login before each test
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
  });

  describe('Profile Display', () => {
    it('should load and display user profile information', () => {
      cy.intercept('GET', '**/api/users/me**', {
        statusCode: 200,
        body: {
          id: 'user-123',
          fullName: 'Test User',
          email: 'test@example.com',
          role: 'USER',
          createdAt: '2024-01-01T00:00:00Z'
        }
      }).as('getProfile');

      cy.visit('/profile');
      cy.wait('@getProfile');

      // Check profile information is displayed
      cy.contains('Test User').should('be.visible');
      cy.contains('test@example.com').should('be.visible');
    });

    it('should handle profile load error', () => {
      cy.intercept('GET', '**/api/users/me**', {
        statusCode: 500,
        body: { message: 'Server error' }
      }).as('getProfileError');

      cy.visit('/profile');
      cy.wait('@getProfileError');

      // Should show error message
      cy.contains(/error|eroare|nu.*putut/i).should('exist');
    });

    it('should show loading state while fetching profile', () => {
      cy.intercept('GET', '**/api/users/me**', {
        delay: 1000,
        statusCode: 200,
        body: {
          id: 'user-123',
          fullName: 'Test User',
          email: 'test@example.com'
        }
      }).as('getProfile');

      cy.visit('/profile');

      // Should show loading indicator
      cy.contains(/loading|se.*încarcă|spinner/i).should('exist');

      cy.wait('@getProfile');
    });
  });

  describe('Profile Information Display', () => {
    beforeEach(() => {
      cy.intercept('GET', '**/api/users/me**', {
        statusCode: 200,
        body: {
          id: 'user-123',
          fullName: 'Test User',
          email: 'test@example.com',
          role: 'USER'
        }
      }).as('getProfile');
    });

    it('should display all profile information correctly', () => {
      cy.visit('/profile');
      cy.wait('@getProfile');

      // Check all profile fields are displayed
      cy.contains('Test User').should('be.visible');
      cy.contains('test@example.com').should('be.visible');
      cy.contains(/profil|profile/i).should('exist');
    });

    it('should display quick action links', () => {
      cy.visit('/profile');
      cy.wait('@getProfile');

      // Check quick action links exist
      cy.contains(/rezervări|reservations/i).should('exist');
      cy.contains(/proiecții|screenings/i).should('exist');
      cy.contains(/filme|movies/i).should('exist');
    });
  });
});
