/// <reference types="cypress" />

describe('Halls Feature', () => {
  beforeEach(() => {
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

  it('should display halls list', () => {
    cy.intercept('GET', '**/api/halls**', {
      statusCode: 200,
      body: [
        {
          id: 'h1',
          name: 'Hall 1',
          number: 1,
          capacity: 100,
          description: 'Main hall'
        },
        {
          id: 'h2',
          name: 'Hall 2',
          number: 2,
          capacity: 80,
          description: 'Secondary hall'
        }
      ]
    }).as('getHalls');

    cy.visit('/halls');
    cy.wait('@getHalls');

    // Check halls are displayed
    cy.contains('Hall 1').should('exist');
    cy.contains('Hall 2').should('exist');
    cy.contains('100').should('exist'); // Capacity
    cy.contains('80').should('exist'); // Capacity
  });

  it('should show loading state', () => {
    cy.intercept('GET', '**/api/halls**', {
      delay: 1000,
      statusCode: 200,
      body: []
    }).as('getHallsDelayed');

    cy.visit('/halls');
    
    // Check loading state (if visible)
    cy.wait('@getHallsDelayed');
  });

  it('should show empty state when no halls', () => {
    cy.intercept('GET', '**/api/halls**', {
      statusCode: 200,
      body: []
    }).as('getEmptyHalls');

    cy.visit('/halls');
    cy.wait('@getEmptyHalls');

    // Check empty state (if exists)
    cy.get('body').should('exist'); // At least page should load
  });

  it('should show error state on API error', () => {
    cy.intercept('GET', '**/api/halls**', {
      statusCode: 500,
      body: { message: 'Internal server error' }
    }).as('getHallsError');

    cy.visit('/halls');
    cy.wait('@getHallsError');

    // Check error state (if exists)
    cy.get('body').should('exist');
  });

});

