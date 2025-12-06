/// <reference types="cypress" />

describe('Home Page', () => {
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

  it('should display home page with welcome message', () => {
    cy.visit('/home');
    cy.wait('@getUserProfile');

    // Check welcome message
    cy.contains(/bine ai venit|welcome/i).should('exist');
    cy.contains('Test User').should('exist');
  });

  it('should display quick action cards', () => {
    cy.visit('/home');
    cy.wait('@getUserProfile');

    // Check for quick action cards
    cy.contains(/filme|movies/i).should('exist');
    cy.contains(/proiecții|screenings/i).should('exist');
    cy.contains(/săli|halls/i).should('exist');
  });

  it('should navigate to movies from home', () => {
    cy.intercept('GET', '**/api/movies**', {
      statusCode: 200,
      body: []
    }).as('getMovies');

    cy.visit('/home');
    cy.wait('@getUserProfile');

    // Click on "Vezi filme" link
    cy.contains(/vezi filme|view movies/i).click();
    cy.url().should('include', '/movies');
    cy.wait('@getMovies');
  });

  it('should navigate to screenings from home', () => {
    cy.intercept('GET', '**/api/screenings**', {
      statusCode: 200,
      body: []
    }).as('getScreenings');

    cy.visit('/home');
    cy.wait('@getUserProfile');

    // Click on "Vezi proiecții" link
    cy.contains(/vezi proiecții|view screenings/i).click();
    cy.url().should('include', '/screenings');
    cy.wait('@getScreenings');
  });

  it('should navigate to halls from home', () => {
    cy.intercept('GET', '**/api/halls**', {
      statusCode: 200,
      body: []
    }).as('getHalls');

    cy.visit('/home');
    cy.wait('@getUserProfile');

    // Click on "Vezi săli" link
    cy.contains(/vezi săli|view halls/i).click();
    cy.url().should('include', '/halls');
    cy.wait('@getHalls');
  });
});

