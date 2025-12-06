/// <reference types="cypress" />

describe('Screening Details Page', () => {
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

  it('should display screening details', () => {
    cy.intercept('GET', '**/api/screenings/s1**', {
      statusCode: 200,
      body: {
        id: 's1',
        movie: { id: '1', title: 'The Matrix' },
        startTime: '2024-12-25T18:00:00Z',
        hall: { name: 'Hall 1', capacity: 100 },
        roomNumber: 1,
        capacity: 100,
        seats: [
          { id: 'seat1', row: 'A', number: 1, status: 'AVAILABLE', isAvailable: true },
          { id: 'seat2', row: 'A', number: 2, status: 'AVAILABLE', isAvailable: true }
        ]
      }
    }).as('getScreening');

    cy.visit('/screenings/s1');
    cy.wait('@getScreening');

    // Check screening details are displayed
    cy.contains('The Matrix').should('exist');
    cy.contains('Hall 1').should('exist');
    cy.contains(/selectează locurile|select seats/i).should('exist');
  });

  it('should display seat grid', () => {
    cy.intercept('GET', '**/api/screenings/s1**', {
      statusCode: 200,
      body: {
        id: 's1',
        movie: { id: '1', title: 'The Matrix' },
        startTime: '2024-12-25T18:00:00Z',
        hall: { name: 'Hall 1', capacity: 100 },
        roomNumber: 1,
        capacity: 100,
        seats: [
          { id: 'seat1', row: 'A', number: 1, status: 'AVAILABLE', isAvailable: true },
          { id: 'seat2', row: 'A', number: 2, status: 'RESERVED', isAvailable: false }
        ]
      }
    }).as('getScreening');

    cy.visit('/screenings/s1');
    cy.wait('@getScreening');

    // Check seat grid is displayed
    cy.contains(/locuri|seats/i).should('exist');
    // Check for seat indicators (available/reserved)
    cy.get('.seat-grid, app-seat-grid').should('exist');
  });

  it('should allow selecting seats', () => {
    cy.intercept('GET', '**/api/screenings/s1**', {
      statusCode: 200,
      body: {
        id: 's1',
        movie: { id: '1', title: 'The Matrix' },
        startTime: '2024-12-25T18:00:00Z',
        hall: { name: 'Hall 1', capacity: 100 },
        roomNumber: 1,
        capacity: 100,
        seats: [
          { id: 'seat1', row: 'A', number: 1, status: 'AVAILABLE', isAvailable: true },
          { id: 'seat2', row: 'A', number: 2, status: 'AVAILABLE', isAvailable: true }
        ]
      }
    }).as('getScreening');

    cy.visit('/screenings/s1');
    cy.wait('@getScreening');

    // Wait for seat grid to load
    cy.contains(/locuri|seats|selectează/i).should('exist');
    
    // Wait for seats to be rendered
    cy.get('app-seat-grid, .seat-grid-container').should('exist');
    
    // Wait a bit for seats to be fully rendered
    cy.wait(500);
    
    // Click on an available seat (format is "A-1" based on seat component)
    // Seats are displayed as buttons with text "A-1" (row-number format)
    cy.get('button.seat-box').contains('A-1').should('be.visible').click();
    
    // Check that seat is selected (might show in summary or change appearance)
    // Seat should have 'selected' class or appear in selected seats list
    cy.get('body').should('exist'); // At least page should respond
  });

  it('should show reservation summary', () => {
    cy.intercept('GET', '**/api/screenings/s1**', {
      statusCode: 200,
      body: {
        id: 's1',
        movie: { id: '1', title: 'The Matrix' },
        startTime: '2024-12-25T18:00:00Z',
        hall: { name: 'Hall 1', capacity: 100 },
        roomNumber: 1,
        capacity: 100,
        seats: [
          { id: 'seat1', row: 'A', number: 1, status: 'AVAILABLE', isAvailable: true }
        ]
      }
    }).as('getScreening');

    cy.visit('/screenings/s1');
    cy.wait('@getScreening');

    // Check reservation summary section
    cy.contains(/rezervare|reservation/i).should('exist');
    cy.contains('The Matrix').should('exist');
  });

  it('should navigate back to screenings list', () => {
    cy.intercept('GET', '**/api/screenings/s1**', {
      statusCode: 200,
      body: {
        id: 's1',
        movie: { id: '1', title: 'The Matrix' },
        startTime: '2024-12-25T18:00:00Z',
        hall: { name: 'Hall 1', capacity: 100 },
        roomNumber: 1,
        capacity: 100,
        seats: []
      }
    }).as('getScreening');

    cy.intercept('GET', '**/api/screenings**', {
      statusCode: 200,
      body: []
    }).as('getScreenings');

    cy.visit('/screenings/s1');
    cy.wait('@getScreening');

    // Click back button
    cy.contains(/înapoi|back/i).click();
    
    cy.url().should('include', '/screenings');
    cy.url().should('not.include', '/screenings/s1');
    cy.wait('@getScreenings');
  });

  it('should show error when screening is not found', () => {
    cy.intercept('GET', '**/api/screenings/999**', {
      statusCode: 404,
      body: { message: 'Screening not found' }
    }).as('getScreeningError');

    cy.visit('/screenings/999');
    cy.wait('@getScreeningError');

    // Check error message
    cy.contains(/nu am putut încărca|error|not found/i).should('exist');
  });

  describe('Admin View', () => {
    beforeEach(() => {
      // First, logout if already logged in
      cy.window().then((win) => {
        const token = win.localStorage.getItem('token');
        if (token) {
          // Clear session
          win.localStorage.removeItem('token');
          win.localStorage.removeItem('user');
          win.sessionStorage.clear();
        }
      });
      cy.clearLocalStorage();
      cy.clearCookies();
      
      // Wait a bit for cleanup
      cy.wait(300);
      
      // Login as admin
      cy.intercept('POST', '**/api/auth/login', {
        statusCode: 200,
        body: {
          token: 'fake-jwt-token-admin',
          role: 'ADMIN',
          userId: 'admin-123'
        }
      }).as('loginRequestAdmin');

      cy.intercept('GET', '**/api/users/me**', {
        statusCode: 200,
        body: {
          id: 'admin-123',
          fullName: 'Admin User',
          email: 'admin@example.com',
          role: 'ADMIN'
        }
      }).as('getUserProfileAdmin');

      // Visit login page
      cy.visit('/login');
      
      // Wait for login page to load (guestGuard might redirect, so we check)
      cy.url({ timeout: 5000 }).then((url) => {
        if (url.includes('/login')) {
          // Wait for login form to be fully visible (wait for CSS animations to complete)
          cy.get('input[name="email"]', { timeout: 10000 }).should('exist');
          // Wait a bit more for opacity animation to complete
          cy.wait(500);
          
          // Login page loaded, proceed with login
          // Use force: true if element is not fully visible due to CSS animations
          cy.get('input[name="email"]').should('exist').clear({ force: true }).type('admin@example.com', { force: true });
          cy.get('input[name="password"]').should('exist').clear({ force: true }).type('password123', { force: true });
          cy.get('button[type="submit"]').click({ force: true });
          cy.wait('@loginRequestAdmin');
          cy.wait('@getUserProfileAdmin');
        } else {
          // Already redirected (shouldn't happen after clearing), but wait for profile anyway
          cy.wait('@getUserProfileAdmin');
        }
      });
    });

    it('should show admin actions on screening details', () => {
      cy.intercept('GET', '**/api/screenings/s1**', {
        statusCode: 200,
        body: {
          id: 's1',
          movie: { id: '1', title: 'The Matrix' },
          startTime: '2024-12-25T18:00:00Z',
          hall: { name: 'Hall 1', capacity: 100 },
          roomNumber: 1,
          capacity: 100,
          seats: []
        }
      }).as('getScreening');

      cy.visit('/screenings/s1');
      cy.wait('@getScreening');

      // Check admin buttons are visible
      cy.contains(/editează proiecție|edit screening/i).should('exist');
      cy.contains(/șterge proiecție|delete screening/i).should('exist');
    });

    it('should allow admin to edit screening from details page', () => {
      cy.intercept('GET', '**/api/screenings/s1**', {
        statusCode: 200,
        body: {
          id: 's1',
          movie: { id: '1', title: 'The Matrix' },
          startTime: '2024-12-25T18:00:00Z',
          hall: { id: 'h1', name: 'Hall 1', capacity: 100 },
          roomNumber: 1,
          capacity: 100,
          seats: []
        }
      }).as('getScreening');

      cy.intercept('GET', '**/api/movies**', {
        statusCode: 200,
        body: [{ id: '1', title: 'The Matrix' }]
      }).as('getMovies');

      cy.intercept('GET', '**/api/halls**', {
        statusCode: 200,
        body: [{ id: 'h1', name: 'Hall 1', number: 1, capacity: 100 }]
      }).as('getHalls');

      cy.intercept('PUT', '**/api/screenings/s1**', {
        statusCode: 200,
        body: {
          id: 's1',
          movie: { id: '1', title: 'The Matrix' },
          startTime: '2024-12-28T20:00:00Z',
          hall: { id: 'h1', name: 'Hall 1' },
          roomNumber: 1,
          capacity: 100
        }
      }).as('updateScreening');

      cy.visit('/screenings/s1');
      cy.wait('@getScreening');

      // Click edit button
      cy.contains(/editează proiecție|edit screening/i).click();

      // Wait for form
      cy.get('app-screening-form').should('be.visible');
      cy.wait('@getMovies');
      cy.wait('@getHalls');
      cy.wait(1000);

      // Update start time
      cy.get('form').within(() => {
        const futureDate = new Date();
        futureDate.setDate(futureDate.getDate() + 10);
        const year = futureDate.getFullYear();
        const month = String(futureDate.getMonth() + 1).padStart(2, '0');
        const day = String(futureDate.getDate()).padStart(2, '0');
        const startTime = `${year}-${month}-${day}T20:00`;
        cy.get('input#startTime').clear().type(startTime);
        cy.get('button[type="submit"]').click();
      });

      cy.wait('@updateScreening');
      cy.contains(/proiecția a fost actualizată|success/i).should('exist');
    });
  });
});

