/// <reference types="cypress" />

describe('Reservation Details Page', () => {
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

  it('should display reservation details', () => {
    cy.intercept('GET', '**/api/reservations/r1**', {
      statusCode: 200,
      body: {
        id: 'r1',
        user: { id: 'user-123', fullName: 'Test User' },
        screening: {
          id: 's1',
          movie: { id: '1', title: 'The Matrix' },
          startTime: '2024-12-25T18:00:00Z',
          hall: { name: 'Hall 1', capacity: 100 }
        },
        status: 'CONFIRMED',
        totalPrice: 50.00,
        tickets: [
          { id: 't1', seat: { row: 'A', number: 1 }, price: 25.00 },
          { id: 't2', seat: { row: 'A', number: 2 }, price: 25.00 }
        ],
        createdAt: '2024-12-20T10:00:00Z'
      }
    }).as('getReservation');

    cy.visit('/reservations/r1');
    cy.wait('@getReservation');

    // Check reservation details are displayed
    cy.contains(/detalii rezervare|reservation details/i).should('exist');
    cy.contains('r1').should('exist'); // Reservation ID
    cy.contains('The Matrix').should('exist');
    cy.contains('Hall 1').should('exist');
    // Status is displayed as "Paid" for CONFIRMED reservations (due to helper function)
    cy.contains(/paid|CONFIRMED/i).should('exist');
    cy.contains('50').should('exist'); // Total price
  });

  it('should display tickets information', () => {
    cy.intercept('GET', '**/api/reservations/r1**', {
      statusCode: 200,
      body: {
        id: 'r1',
        user: { id: 'user-123', fullName: 'Test User' },
        screening: {
          id: 's1',
          movie: { id: '1', title: 'The Matrix' },
          startTime: '2024-12-25T18:00:00Z',
          hall: { name: 'Hall 1' }
        },
        status: 'CONFIRMED',
        totalPrice: 50.00,
        tickets: [
          { id: 't1', seat: { row: 'A', number: 1 }, price: 25.00 },
          { id: 't2', seat: { row: 'B', number: 5 }, price: 25.00 }
        ]
      }
    }).as('getReservation');

    cy.visit('/reservations/r1');
    cy.wait('@getReservation');

    // Check tickets are displayed
    cy.contains(/bilete|tickets/i).should('exist');
    cy.contains(/rând.*a|row.*a/i).should('exist');
    cy.contains(/loc.*1|seat.*1/i).should('exist');
    cy.contains(/rând.*b|row.*b/i).should('exist');
    cy.contains(/loc.*5|seat.*5/i).should('exist');
  });

  it('should display QR code for reservation', () => {
    cy.intercept('GET', '**/api/reservations/r1**', {
      statusCode: 200,
      body: {
        id: 'r1',
        user: { id: 'user-123', fullName: 'Test User' },
        screening: {
          id: 's1',
          movie: { id: '1', title: 'The Matrix' },
          startTime: '2024-12-25T18:00:00Z',
          hall: { name: 'Hall 1' }
        },
        status: 'CONFIRMED',
        totalPrice: 50.00,
        tickets: [
          { id: 't1', seat: { row: 'A', number: 1 }, price: 25.00 }
        ]
      }
    }).as('getReservation');

    cy.visit('/reservations/r1');
    cy.wait('@getReservation');

    // Check QR code section exists
    cy.contains(/cod qr|qr code/i).should('exist');
    // QR code might take time to generate
    cy.get('img[alt*="QR"], img[src*="data:image"]').should('exist');
  });

  it('should display screening information', () => {
    cy.intercept('GET', '**/api/reservations/r1**', {
      statusCode: 200,
      body: {
        id: 'r1',
        user: { id: 'user-123', fullName: 'Test User' },
        screening: {
          id: 's1',
          movie: { id: '1', title: 'The Matrix' },
          startTime: '2024-12-25T18:00:00Z',
          hall: { name: 'Hall 1', capacity: 100 }
        },
        status: 'CONFIRMED',
        totalPrice: 50.00,
        tickets: []
      }
    }).as('getReservation');

    cy.visit('/reservations/r1');
    cy.wait('@getReservation');

    // Check screening information
    cy.contains(/film|movie/i).should('exist');
    cy.contains('The Matrix').should('exist');
    cy.contains(/sală|hall/i).should('exist');
    cy.contains('Hall 1').should('exist');
    cy.contains(/data.*ora|date.*time/i).should('exist');
  });

  it('should navigate back to reservations list', () => {
    cy.intercept('GET', '**/api/reservations/r1**', {
      statusCode: 200,
      body: {
        id: 'r1',
        user: { id: 'user-123', fullName: 'Test User' },
        screening: {
          id: 's1',
          movie: { id: '1', title: 'The Matrix' },
          startTime: '2024-12-25T18:00:00Z',
          hall: { name: 'Hall 1' }
        },
        status: 'CONFIRMED',
        totalPrice: 50.00,
        tickets: []
      }
    }).as('getReservation');

    cy.intercept('GET', '**/api/reservations/user/**', {
      statusCode: 200,
      body: []
    }).as('getReservations');

    cy.visit('/reservations/r1');
    cy.wait('@getReservation');

    // Click back button
    cy.contains(/înapoi.*rezervări|back.*reservations/i).click();
    
    cy.url().should('include', '/reservations');
    cy.url().should('not.include', '/reservations/r1');
    cy.wait('@getReservations');
  });

  it('should show loading state', () => {
    cy.intercept('GET', '**/api/reservations/r1**', {
      delay: 1000,
      statusCode: 200,
      body: {
        id: 'r1',
        user: { id: 'user-123', fullName: 'Test User' },
        screening: {
          id: 's1',
          movie: { id: '1', title: 'The Matrix' },
          startTime: '2024-12-25T18:00:00Z',
          hall: { name: 'Hall 1' }
        },
        status: 'CONFIRMED',
        totalPrice: 50.00,
        tickets: []
      }
    }).as('getReservation');

    cy.visit('/reservations/r1');
    
    // Check loading state
    cy.contains(/se încarcă.*rezervare|loading.*reservation/i).should('exist');
    
    cy.wait('@getReservation');
  });

  it('should show error when reservation is not found', () => {
    cy.intercept('GET', '**/api/reservations/999**', {
      statusCode: 404,
      body: { message: 'Reservation not found' }
    }).as('getReservationError');

    cy.visit('/reservations/999');
    cy.wait('@getReservationError');

    // Check error message
    cy.contains(/nu am putut încărca|error|not found/i).should('exist');
  });

  it('should display different reservation statuses', () => {
    const statuses = ['CONFIRMED', 'PENDING', 'CANCELLED'];
    
    statuses.forEach((status, index) => {
      cy.intercept('GET', `**/api/reservations/r${index + 1}**`, {
        statusCode: 200,
        body: {
          id: `r${index + 1}`,
          user: { id: 'user-123', fullName: 'Test User' },
          screening: {
            id: 's1',
            movie: { id: '1', title: 'The Matrix' },
            startTime: '2024-12-25T18:00:00Z',
            hall: { name: 'Hall 1' }
          },
          status: status,
          totalPrice: 50.00,
          tickets: []
        }
      }).as(`getReservation${index}`);

      cy.visit(`/reservations/r${index + 1}`);
      cy.wait(`@getReservation${index}`);

      // Check status is displayed
      // Note: Statuses are transformed by helper functions:
      // CONFIRMED -> "Paid", PENDING -> "Pending", CANCELLED -> "Cancelled"
      if (status === 'CONFIRMED') {
        cy.contains(/paid/i).should('exist');
      } else if (status === 'PENDING') {
        cy.contains(/pending/i).should('exist');
      } else if (status === 'CANCELLED') {
        cy.contains(/cancelled/i).should('exist');
      } else {
        cy.contains(status).should('exist');
      }
    });
  });
});

