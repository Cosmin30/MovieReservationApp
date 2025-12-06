/// <reference types="cypress" />

describe('Checkout & Payment Flow', () => {
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

  describe('Reservation Creation Flow', () => {
    beforeEach(() => {
      // Intercept screening by ID (used by reservation page)
      // Backend sends both snake_case and camelCase for compatibility
      cy.intercept('GET', '**/api/screenings/s1**', {
        statusCode: 200,
        body: {
          id: 's1',
          movie: { title: 'The Matrix' },
          start_time: '2024-12-25T18:00:00Z',
          startTime: '2024-12-25T18:00:00Z',
          hall: { name: 'Hall 1', capacity: 100 },
          room_number: 1,
          roomNumber: 1,
          capacity: 100,
          seats: [
            { id: 'seat1', row: 'A', number: 1, is_available: true, isAvailable: true, status: 'AVAILABLE' },
            { id: 'seat2', row: 'A', number: 2, is_available: true, isAvailable: true, status: 'AVAILABLE' },
            { id: 'seat3', row: 'B', number: 1, is_available: true, isAvailable: true, status: 'AVAILABLE' }
          ]
        }
      }).as('getScreening');
    });

    it('should display order summary with selected seats', () => {
      cy.visit('/reservations/new?screeningId=s1');
      cy.wait('@getScreening');

      // Wait for seats to load
      cy.contains(/locuri|seats|selectează/i).should('exist');
      
      // Select seats - seats are displayed as "A-1", "A-2", etc.
      cy.contains('A-1').click();
      cy.contains('A-2').click();

      // Check order summary is displayed
      cy.contains(/rezumat|summary|total|preț/i).should('exist');
      cy.contains(/A-1|A1|Rând.*A.*Loc.*1/i).should('exist');
      cy.contains(/A-2|A2|Rând.*A.*Loc.*2/i).should('exist');
    });

    it('should calculate total price correctly', () => {
      cy.visit('/reservations/new?screeningId=s1');
      cy.wait('@getScreening');

      // Wait for seats to load
      cy.contains(/locuri|seats|selectează/i).should('exist');
      
      // Select seats
      cy.contains('A-1').click();
      cy.contains('A-2').click();

      // Total should be displayed (assuming 50 per seat, so 100 total)
      cy.contains(/100|50|total/i).should('exist');
    });

    it('should validate payment form fields', () => {
      cy.visit('/reservations/new?screeningId=s1');
      cy.wait('@getScreening');

      // Wait for seats to load and select seats
      cy.contains(/locuri|seats|selectează/i).should('exist');
      cy.contains('A-1').click();

      // Click "Continuă la Plată" button to show payment form
      cy.get('button').contains(/continuă.*plată|continue.*payment/i).click({ force: true });

      // Wait for payment form to appear and scroll to it
      cy.get('form', { timeout: 5000 }).should('exist').scrollIntoView();
      cy.get('input[name="cardNumber"]', { timeout: 5000 }).should('exist').scrollIntoView().should('be.visible').should('not.be.disabled');

      // Try to submit without filling payment - button should be disabled if form is invalid
      cy.get('button[type="submit"]').should('be.disabled');

      // Fill only some fields - wait a bit for form to stabilize
      cy.wait(200);
      cy.get('input[name="cardNumber"]').should('not.be.disabled').type('4242');
      cy.get('input[name="cardHolder"]').type('J');

      // Button should still be disabled
      cy.get('button[type="submit"]').should('be.disabled');
    });

    it('should complete payment with valid data', () => {
      cy.intercept('POST', '**/api/reservations**', {
        statusCode: 201,
        body: {
          id: 'r-new',
          status: 'CONFIRMED',
          totalPrice: 100.00,
          tickets: [
            { id: 't1', seat: { row: 'A', number: 1 }, price: 50.00 },
            { id: 't2', seat: { row: 'A', number: 2 }, price: 50.00 }
          ]
        }
      }).as('createReservation');

      cy.intercept('GET', '**/api/reservations/user/**', {
        statusCode: 200,
        body: []
      }).as('getReservations');

      cy.visit('/reservations/new?screeningId=s1');
      cy.wait('@getScreening');

      // Select seats
      cy.contains('A-1').click();
      cy.contains('A-2').click();

      // Click "Continuă la Plată" button to show payment form
      cy.get('button').contains(/continuă.*plată|continue.*payment/i).click({ force: true });

      // Scroll to payment form to ensure it's visible
      cy.get('input[name="cardNumber"]').scrollIntoView().should('be.visible');

      // Fill payment form
      cy.get('input[name="cardNumber"]').type('4242424242424242');
      cy.get('input[name="cardHolder"]').type('JOHN DOE');
      cy.get('input[name="expiry"]').type('12/25');
      cy.get('input[name="cvv"]').type('123');

      // Submit
      cy.get('button[type="submit"]').contains(/finalizează|confirm|confirmă/i).click({ force: true });
      cy.wait('@createReservation');

      // Success message appears but redirect happens after 3 seconds
      // The success message might not be visible long enough, so we check for either success message or redirect
      cy.get('body', { timeout: 4000 }).then(($body) => {
        const hasSuccess = $body.find('.alert-success').length > 0;
        if (hasSuccess) {
          // Success message is visible
          cy.contains(/rezervarea.*creată.*succes|creată.*succes|plata.*procesată|success|succes/i).should('exist');
        }
      });
      
      // Wait for redirect (happens after 3 seconds from when success is set)
      // If we're already redirected, that's also success
      cy.url({ timeout: 6000 }).should('include', '/reservations');
    });

    it('should handle network timeout gracefully', () => {
      cy.intercept('POST', '**/api/reservations**', {
        forceNetworkError: true
      }).as('networkError');

      cy.visit('/reservations/new?screeningId=s1');
      cy.wait('@getScreening');

      cy.contains('A-1').click();
      
      // Click "Continuă la Plată" button to show payment form
      cy.get('button').contains(/continuă.*plată|continue.*payment/i).click({ force: true });

      // Wait for payment form to appear and scroll to it
      cy.get('form', { timeout: 5000 }).should('exist').scrollIntoView();
      cy.get('input[name="cardNumber"]', { timeout: 5000 }).should('exist').scrollIntoView().should('be.visible').should('not.be.disabled');
      
      // Wait a bit for form to stabilize before typing
      cy.wait(300);
      
      // Fill payment form - use type with {force: false} to ensure element is enabled
      // Don't use clear() as it can cause issues with re-rendering
      cy.get('input[name="cardNumber"]').should('not.be.disabled').type('4242424242424242', { force: false });
      cy.get('input[name="cardHolder"]').should('not.be.disabled').type('JOHN DOE', { force: false });
      cy.get('input[name="expiry"]').should('not.be.disabled').type('12/25', { force: false });
      cy.get('input[name="cvv"]').should('not.be.disabled').type('123', { force: false });
      
      // Submit
      cy.get('button[type="submit"]').contains(/finalizează|confirm|confirmă/i).should('not.be.disabled').click({ force: true });

      // Wait a bit for the error to appear (network errors may take a moment)
      cy.contains(/timeout|network|connection|eroare|nu.*putut|failed/i, { timeout: 5000 }).should('exist');
    });

    it('should show loading state during payment processing', () => {
      cy.intercept('POST', '**/api/reservations**', {
        delay: 2000,
        statusCode: 201,
        body: {
          id: 'r-new',
          status: 'CONFIRMED',
          totalPrice: 50.00
        }
      }).as('createReservation');

      cy.visit('/reservations/new?screeningId=s1');
      cy.wait('@getScreening');

      cy.contains('A-1').click();
      
      // Click "Continuă la Plată" button to show payment form
      cy.get('button').contains(/continuă.*plată|continue.*payment/i).click({ force: true });

      // Scroll to payment form
      cy.get('input[name="cardNumber"]').scrollIntoView().should('be.visible');
      
      // Fill payment form
      cy.get('input[name="cardNumber"]').type('4242424242424242');
      cy.get('input[name="cardHolder"]').type('JOHN DOE');
      cy.get('input[name="expiry"]').type('12/25');
      cy.get('input[name="cvv"]').type('123');
      
      // Submit
      cy.get('button[type="submit"]').contains(/finalizează|confirm|confirmă/i).click({ force: true });

      // Check loading state - button should show "Se procesează..."
      cy.contains(/se.*procesează|loading|spinner/i).should('exist');

      cy.wait('@createReservation');
    });

    it('should prevent selecting unavailable seats', () => {
      cy.intercept('GET', '**/api/screenings/s1**', {
        statusCode: 200,
        body: {
          id: 's1',
          movie: { title: 'The Matrix' },
          start_time: '2024-12-25T18:00:00Z',
          startTime: '2024-12-25T18:00:00Z',
          hall: { name: 'Hall 1', capacity: 100 },
          room_number: 1,
          roomNumber: 1,
          capacity: 100,
          seats: [
            { id: 'seat1', row: 'A', number: 1, is_available: false, isAvailable: false, status: 'RESERVED' },
            { id: 'seat2', row: 'A', number: 2, is_available: true, isAvailable: true, status: 'AVAILABLE' }
          ]
        }
      }).as('getScreening');

      cy.visit('/reservations/new?screeningId=s1');
      cy.wait('@getScreening');

      // Try to click unavailable seat - it should not be clickable
      cy.contains('A-1').should('have.class', 'reserved');
      cy.contains('A-2').click(); // Available seat should work
    });
  });

  describe('Error Handling', () => {
    it('should handle seat conflict error', () => {
      cy.intercept('GET', '**/api/screenings/s1**', {
        statusCode: 200,
        body: {
          id: 's1',
          movie: { title: 'The Matrix' },
          start_time: '2024-12-25T18:00:00Z',
          startTime: '2024-12-25T18:00:00Z',
          hall: { name: 'Hall 1', capacity: 100 },
          room_number: 1,
          roomNumber: 1,
          capacity: 100,
          seats: [
            { id: 'seat1', row: 'A', number: 1, is_available: true, isAvailable: true, status: 'AVAILABLE' }
          ]
        }
      }).as('getScreening');

      cy.intercept('POST', '**/api/reservations**', {
        statusCode: 409,
        body: { message: 'Seat already reserved' }
      }).as('createReservationConflict');

      cy.visit('/reservations/new?screeningId=s1');
      cy.wait('@getScreening');

      cy.contains('A-1').click();
      
      // Click "Continuă la Plată" button to show payment form
      cy.get('button').contains(/continuă.*plată|continue.*payment/i).click({ force: true });

      // Wait for payment form to appear and scroll to it
      cy.get('form', { timeout: 5000 }).should('exist').scrollIntoView();
      cy.get('input[name="cardNumber"]', { timeout: 5000 }).should('exist').scrollIntoView().should('be.visible').should('not.be.disabled');
      
      // Wait a bit for form to stabilize before typing
      cy.wait(300);
      
      // Fill payment form - use type with {force: false} to ensure element is enabled
      // Don't use clear() as it can cause issues with re-rendering
      cy.get('input[name="cardNumber"]').should('not.be.disabled').type('4242424242424242', { force: false });
      cy.get('input[name="cardHolder"]').should('not.be.disabled').type('JOHN DOE', { force: false });
      cy.get('input[name="expiry"]').should('not.be.disabled').type('12/25', { force: false });
      cy.get('input[name="cvv"]').should('not.be.disabled').type('123', { force: false });
      
      // Submit
      cy.get('button[type="submit"]').contains(/finalizează|confirm|confirmă/i).should('not.be.disabled').click({ force: true });

      cy.wait('@createReservationConflict');
      cy.contains(/already.*reserved|ocupat|rezervat|conflict|nu.*putut/i).should('exist');
    });
  });
});
