/// <reference types="cypress" />

describe('Hall Details (Layout) Page', () => {
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

  it('should display hall details', () => {
    cy.intercept('GET', '**/api/halls/h1**', {
      statusCode: 200,
      body: {
        id: 'h1',
        name: 'Hall 1',
        number: 1,
        capacity: 100,
        description: 'Main hall',
        type: 'Standard',
        seats: []
      }
    }).as('getHall');

    cy.visit('/halls/h1');
    cy.wait('@getHall');

    // Check hall details are displayed
    cy.contains('Hall 1').should('exist');
    cy.contains(/sala.*1|hall.*1/i).should('exist');
    cy.contains('100').should('exist'); // Capacity
    cy.contains(/capacitate|capacity/i).should('exist');
  });

  it('should display hall capacity information', () => {
    cy.intercept('GET', '**/api/halls/h1**', {
      statusCode: 200,
      body: {
        id: 'h1',
        name: 'Hall 1',
        number: 1,
        capacity: 100,
        description: 'Main hall',
        type: 'Standard',
        seats: []
      }
    }).as('getHall');

    cy.visit('/halls/h1');
    cy.wait('@getHall');

    // Check capacity information
    cy.contains(/capacitate|capacity/i).should('exist');
    cy.contains('100').should('exist');
    cy.contains(/locuri|seats/i).should('exist');
  });

  it('should navigate back to halls list', () => {
    // First visit halls list to establish history
    cy.intercept('GET', '**/api/halls**', {
      statusCode: 200,
      body: [
        {
          id: 'h1',
          name: 'Hall 1',
          number: 1,
          capacity: 100,
          description: 'Main hall'
        }
      ]
    }).as('getHalls');

    // Visit halls list first to establish history
    cy.visit('/halls');
    cy.wait('@getHalls');
    
    // Verify we're on halls list
    cy.url().should('include', '/halls');
    cy.url().should('not.include', '/halls/h1');
    cy.contains('Hall 1').should('exist'); // Verify halls list is loaded

    // Now navigate to hall details
    cy.intercept('GET', '**/api/halls/h1**', {
      statusCode: 200,
      body: {
        id: 'h1',
        name: 'Hall 1',
        number: 1,
        capacity: 100,
        description: 'Main hall',
        type: 'Standard',
        seats: []
      }
    }).as('getHall');

    // Navigate to hall details by clicking on "Vezi detalii" button
    cy.contains('Hall 1').closest('.hall-card, .card').find('button').contains(/vezi detalii|detalii|details/i).click();
    cy.wait('@getHall');
    cy.url().should('include', '/halls/h1');
    
    // Verify we're on hall details page
    cy.contains(/sala.*1|hall.*1/i).should('exist');

    // Click back button (uses window.history.back())
    cy.contains(/înapoi|back/i).should('be.visible').click();
    
    // window.history.back() should navigate back to /halls (the previous page)
    cy.url({ timeout: 5000 }).should('include', '/halls');
    cy.url().should('not.include', '/halls/h1');
    
    // Verify we're back on halls list
    cy.contains(/salile|halls/i).should('exist');
  });

  it('should navigate to screenings from hall details', () => {
    cy.intercept('GET', '**/api/halls/h1**', {
      statusCode: 200,
      body: {
        id: 'h1',
        name: 'Hall 1',
        number: 1,
        capacity: 100,
        description: 'Main hall',
        type: 'Standard',
        seats: []
      }
    }).as('getHall');

    cy.intercept('GET', '**/api/screenings**', {
      statusCode: 200,
      body: []
    }).as('getScreenings');

    cy.visit('/halls/h1');
    cy.wait('@getHall');

    // Click on "Vezi Proiecțiile Disponibile" button
    cy.contains(/vezi proiecțiile|view screenings/i).click();
    
    cy.url().should('include', '/screenings');
    cy.wait('@getScreenings');
  });

  it('should show loading state', () => {
    cy.intercept('GET', '**/api/halls/h1**', {
      delay: 1000,
      statusCode: 200,
      body: {
        id: 'h1',
        name: 'Hall 1',
        number: 1,
        capacity: 100,
        description: 'Main hall',
        type: 'Standard',
        seats: []
      }
    }).as('getHallDelayed');

    cy.visit('/halls/h1');
    
    // Check loading state
    cy.contains(/se încarcă|loading/i).should('exist');
    
    cy.wait('@getHallDelayed');
  });

  it('should show error state when hall is not found', () => {
    cy.intercept('GET', '**/api/halls/999**', {
      statusCode: 404,
      body: { message: 'Hall not found' }
    }).as('getHallError');

    cy.visit('/halls/999');
    cy.wait('@getHallError');

    // Check error message
    cy.contains(/nu am putut încărca|error|not found/i).should('exist');
  });

  it('should show retry button on error', () => {
    cy.intercept('GET', '**/api/halls/h1**', {
      statusCode: 500,
      body: { message: 'Internal server error' }
    }).as('getHallError');

    cy.visit('/halls/h1');
    cy.wait('@getHallError');

    // Check retry button exists
    cy.contains(/încearcă din nou|retry|try again/i).should('exist');
  });

  it('should retry loading hall on retry button click', () => {
    // First request fails
    cy.intercept('GET', '**/api/halls/h1**', {
      statusCode: 500,
      body: { message: 'Internal server error' }
    }).as('getHallError');

    cy.visit('/halls/h1');
    cy.wait('@getHallError');

    // Second request succeeds
    cy.intercept('GET', '**/api/halls/h1**', {
      statusCode: 200,
      body: {
        id: 'h1',
        name: 'Hall 1',
        number: 1,
        capacity: 100,
        description: 'Main hall',
        type: 'Standard',
        seats: []
      }
    }).as('getHallRetry');

    // Click retry button
    cy.contains(/încearcă din nou|retry|try again/i).click();
    
    cy.wait('@getHallRetry');
    cy.contains('Hall 1').should('exist');
  });
});

