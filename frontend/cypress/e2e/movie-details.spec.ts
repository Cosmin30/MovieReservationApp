/// <reference types="cypress" />

describe('Movie Details Page', () => {
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

  it('should display movie details', () => {
    cy.intercept('GET', '**/api/movies/1**', {
      statusCode: 200,
      body: {
        id: '1',
        title: 'The Matrix',
        description: 'A computer hacker learns about the true nature of reality.',
        duration: 136,
        genre: 'Sci-Fi',
        releaseDate: '1999-03-31',
        rating: 8.7
      }
    }).as('getMovie');

    cy.intercept('GET', '**/api/screenings?movieId=1**', {
      statusCode: 200,
      body: []
    }).as('getScreenings');

    cy.visit('/movies/1');
    cy.wait('@getMovie');
    cy.wait('@getScreenings');

    // Check movie details are displayed
    cy.contains('The Matrix').should('exist');
    cy.contains(/sci-fi|genre/i).should('exist');
    cy.contains(/136|2h.*16m/i).should('exist');
  });

  it('should display screenings for the movie', () => {
    cy.intercept('GET', '**/api/movies/1**', {
      statusCode: 200,
      body: {
        id: '1',
        title: 'The Matrix',
        description: 'A computer hacker learns about the true nature of reality.',
        duration: 136,
        genre: 'Sci-Fi',
        releaseDate: '1999-03-31',
        rating: 8.7
      }
    }).as('getMovie');

    cy.intercept('GET', '**/api/screenings?movieId=1**', {
      statusCode: 200,
      body: [
        {
          id: 's1',
          movie: { id: '1', title: 'The Matrix' },
          startTime: '2024-12-25T18:00:00Z',
          hall: { name: 'Hall 1' },
          roomNumber: 1,
          capacity: 100
        }
      ]
    }).as('getScreenings');

    cy.visit('/movies/1');
    cy.wait('@getMovie');
    cy.wait('@getScreenings');

    // Check screenings are displayed
    cy.contains(/proiecții|screenings/i).should('exist');
    cy.contains('Hall 1').should('exist');
  });

  it('should navigate to screening details when clicking on screening', () => {
    cy.intercept('GET', '**/api/movies/1**', {
      statusCode: 200,
      body: {
        id: '1',
        title: 'The Matrix',
        description: 'A computer hacker learns about the true nature of reality.',
        duration: 136,
        genre: 'Sci-Fi',
        releaseDate: '1999-03-31',
        rating: 8.7
      }
    }).as('getMovie');

    cy.intercept('GET', '**/api/screenings?movieId=1**', {
      statusCode: 200,
      body: [
        {
          id: 's1',
          movie: { id: '1', title: 'The Matrix' },
          startTime: '2024-12-25T18:00:00Z',
          hall: { name: 'Hall 1' },
          roomNumber: 1,
          capacity: 100
        }
      ]
    }).as('getScreenings');

    cy.intercept('GET', '**/api/screenings/s1**', {
      statusCode: 200,
      body: {
        id: 's1',
        movie: { id: '1', title: 'The Matrix' },
        startTime: '2024-12-25T18:00:00Z',
        hall: { name: 'Hall 1' },
        roomNumber: 1,
        capacity: 100,
        seats: []
      }
    }).as('getScreeningDetails');

    cy.visit('/movies/1');
    cy.wait('@getMovie');
    cy.wait('@getScreenings');

    // Click on screening card or button
    cy.contains('Hall 1').closest('.screening-card, .card').find('button, a').contains(/detalii|details|rezervă|book/i).click();
    
    cy.url().should('include', '/screenings/s1');
    cy.wait('@getScreeningDetails');
  });

  it('should show error when movie is not found', () => {
    cy.intercept('GET', '**/api/movies/999**', {
      statusCode: 404,
      body: { message: 'Movie not found' }
    }).as('getMovieError');

    cy.visit('/movies/999');
    cy.wait('@getMovieError');

    // Check error message
    cy.contains(/nu am putut încărca|error|not found/i).should('exist');
  });

  it('should show empty state when no screenings available', () => {
    cy.intercept('GET', '**/api/movies/1**', {
      statusCode: 200,
      body: {
        id: '1',
        title: 'The Matrix',
        description: 'A computer hacker learns about the true nature of reality.',
        duration: 136,
        genre: 'Sci-Fi',
        releaseDate: '1999-03-31',
        rating: 8.7
      }
    }).as('getMovie');

    cy.intercept('GET', '**/api/screenings?movieId=1**', {
      statusCode: 200,
      body: []
    }).as('getEmptyScreenings');

    cy.visit('/movies/1');
    cy.wait('@getMovie');
    cy.wait('@getEmptyScreenings');

    // Check empty state
    cy.contains(/nu există proiecții|no screenings/i).should('exist');
  });
});

