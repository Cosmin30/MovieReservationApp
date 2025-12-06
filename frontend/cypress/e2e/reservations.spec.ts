/// <reference types="cypress" />

describe('Reservations Feature', () => {
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

  describe('Reservations List', () => {
    it('should display user reservations', () => {
      cy.intercept('GET', '**/api/reservations/user/**', {
        statusCode: 200,
        body: [
          {
            id: 'r1',
            user: { id: 'user-123', fullName: 'Test User' },
            screening: {
              id: 's1',
              movie: { title: 'The Matrix' },
              startTime: '2024-12-25T18:00:00Z',
              hall: { name: 'Hall 1' }
            },
            status: 'CONFIRMED',
            totalPrice: 50.00,
            tickets: [
              { id: 't1', seat: { row: 'A', number: 1 }, price: 25.00 },
              { id: 't2', seat: { row: 'A', number: 2 }, price: 25.00 }
            ],
            createdAt: '2024-12-20T10:00:00Z'
          },
          {
            id: 'r2',
            user: { id: 'user-123', fullName: 'Test User' },
            screening: {
              id: 's2',
              movie: { title: 'Inception' },
              startTime: '2024-12-26T20:00:00Z',
              hall: { name: 'Hall 2' }
            },
            status: 'PENDING',
            totalPrice: 30.00,
            tickets: [
              { id: 't3', seat: { row: 'B', number: 5 }, price: 30.00 }
            ],
            createdAt: '2024-12-21T11:00:00Z'
          }
        ]
      }).as('getReservations');

      cy.visit('/reservations');
      cy.wait('@getReservations');

      // Check page title
      cy.contains(/rezervările mele|my reservations/i).should('exist');

      // Check reservations are displayed
      cy.contains('The Matrix').should('be.visible');
      cy.contains('Inception').should('be.visible');
      // Statuses are transformed by helper functions: CONFIRMED -> "Paid", PENDING -> "Pending"
      cy.contains(/paid/i).should('exist');
      cy.contains(/pending/i).should('exist');
      cy.contains('50').should('exist'); // Total price
      cy.contains('30').should('exist'); // Total price
    });

    it('should show loading state', () => {
      cy.intercept('GET', '**/api/reservations/user/**', {
        delay: 1000,
        statusCode: 200,
        body: []
      }).as('getReservations');

      cy.visit('/reservations');
      
      // Check loading state
      cy.contains(/se încarcă rezervările|loading/i).should('exist');
      
      cy.wait('@getReservations');
    });

    it('should show empty state when no reservations', () => {
      cy.intercept('GET', '**/api/reservations/user/**', {
        statusCode: 200,
        body: []
      }).as('getReservations');

      cy.visit('/reservations');
      cy.wait('@getReservations');

      // Check empty state
      cy.contains(/nu ai nici o rezervare|no reservations/i).should('exist');
      cy.contains(/explorează filmele|explore movies/i).should('exist');
    });

    it('should show error state on API error', () => {
      cy.intercept('GET', '**/api/reservations/user/**', {
        statusCode: 500,
        body: { message: 'Internal server error' }
      }).as('getReservationsError');

      cy.visit('/reservations');
      cy.wait('@getReservationsError');

      // Check error state
      cy.contains(/nu am putut încărca rezervările|error/i).should('exist');
    });

    it('should refresh reservations when refresh query param is present', () => {
      cy.intercept('GET', '**/api/reservations/user/**', {
        statusCode: 200,
        body: [
          {
            id: 'r1',
            screening: {
              movie: { title: 'The Matrix' }
            },
            status: 'CONFIRMED',
            totalPrice: 50.00
          }
        ]
      }).as('getReservations');

      cy.visit('/reservations?refresh=true');
      cy.wait('@getReservations');

      // URL should be cleaned (no query params)
      cy.url().should('not.include', 'refresh=true');
    });
  });
});
