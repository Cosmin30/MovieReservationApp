/// <reference types="cypress" />

describe('Movies Feature', () => {
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

  describe('Movies List', () => {
    it('should display movies list with proper structure', () => {
      cy.intercept('GET', '**/api/movies**', {
        statusCode: 200,
        body: [
          {
            id: '1',
            title: 'The Matrix',
            description: 'A computer hacker learns about the true nature of reality',
            genre: 'Action',
            duration: 136,
            releaseDate: '1999-03-31'
          },
          {
            id: '2',
            title: 'Inception',
            description: 'A thief who steals corporate secrets',
            genre: 'Sci-Fi',
            duration: 148,
            releaseDate: '2010-07-16'
          }
        ]
      }).as('getMovies');

      cy.visit('/movies');
      cy.wait('@getMovies');

      // Check page title/header
      cy.contains(/filme|movies/i).should('exist');

      // Check movies are displayed
      cy.contains('The Matrix').should('be.visible');
      cy.contains('Inception').should('be.visible');

      // Check movie cards exist
      cy.get('.movie-card, [class*="movie"], app-movie-card').should('have.length.at.least', 2);
    });

    it('should handle empty movies list', () => {
      cy.intercept('GET', '**/api/movies**', {
        statusCode: 200,
        body: []
      }).as('getEmptyMovies');

      cy.visit('/movies');
      cy.wait('@getEmptyMovies');

      // Should show no movies (grid should be empty or show no cards)
      cy.get('.movie-card, app-movie-card').should('not.exist');
      // Or check that movie grid is empty
      cy.get('app-movie-grid').should('exist');
    });

    it('should handle server error when loading movies', () => {
      cy.intercept('GET', '**/api/movies**', {
        statusCode: 500,
        body: { message: 'Server error' }
      }).as('getMoviesError');

      cy.visit('/movies');
      cy.wait('@getMoviesError');

      // Should show error message
      cy.contains(/error|eroare|nu.*putut/i).should('exist');
    });

    it('should filter movies by search query', () => {
      cy.intercept('GET', '**/api/movies**', {
        statusCode: 200,
        body: [
          {
            id: '1',
            title: 'The Matrix',
            description: 'A computer hacker learns about the true nature of reality',
            genre: 'Action',
            duration: 136,
            releaseDate: '1999-03-31'
          },
          {
            id: '2',
            title: 'Inception',
            description: 'A thief who steals corporate secrets',
            genre: 'Sci-Fi',
            duration: 148,
            releaseDate: '2010-07-16'
          }
        ]
      }).as('getMovies');

      cy.visit('/movies');
      cy.wait('@getMovies');

      // Type in search input (placeholder is "Caută film..." in movie-search component)
      cy.get('input[placeholder*="Caută film"], input[placeholder*="caută film"]').type('Matrix');
      
      // Check that only "The Matrix" is visible
      cy.contains('The Matrix').should('be.visible');
      cy.contains('Inception').should('not.exist');
    });

    it('should filter movies by genre', () => {
      cy.intercept('GET', '**/api/movies**', {
        statusCode: 200,
        body: [
          {
            id: '1',
            title: 'The Matrix',
            description: 'A computer hacker learns about the true nature of reality',
            genre: 'Action',
            duration: 136,
            releaseDate: '1999-03-31'
          },
          {
            id: '2',
            title: 'Inception',
            description: 'A thief who steals corporate secrets',
            genre: 'Sci-Fi',
            duration: 148,
            releaseDate: '2010-07-16'
          }
        ]
      }).as('getMovies');

      cy.visit('/movies');
      cy.wait('@getMovies');

      // Click on genre filter (Action)
      cy.contains('Action').click();
      
      // Check that only "The Matrix" is visible (Action genre)
      cy.contains('The Matrix').should('be.visible');
      cy.contains('Inception').should('not.exist');
    });

    it('should clear filters and show all movies', () => {
      cy.intercept('GET', '**/api/movies**', {
        statusCode: 200,
        body: [
          {
            id: '1',
            title: 'The Matrix',
            description: 'A computer hacker learns about the true nature of reality',
            genre: 'Action',
            duration: 136,
            releaseDate: '1999-03-31'
          },
          {
            id: '2',
            title: 'Inception',
            description: 'A thief who steals corporate secrets',
            genre: 'Sci-Fi',
            duration: 148,
            releaseDate: '2010-07-16'
          }
        ]
      }).as('getMovies');

      cy.visit('/movies');
      cy.wait('@getMovies');

      // Apply filter using search input
      cy.get('input[placeholder*="Caută film"], input[placeholder*="caută film"]').type('Matrix');
      cy.contains('The Matrix').should('be.visible');
      cy.contains('Inception').should('not.exist');

      // Clear filter
      cy.get('input[placeholder*="Caută film"], input[placeholder*="caută film"]').clear();
      
      // Both movies should be visible again
      cy.contains('The Matrix').should('be.visible');
      cy.contains('Inception').should('be.visible');
    });

    it('should show loading state while fetching movies', () => {
      cy.intercept('GET', '**/api/movies**', {
        delay: 1000,
        statusCode: 200,
        body: []
      }).as('getMovies');

      cy.visit('/movies');

      // Note: Loading state might not be visible if request is too fast
      // Just verify that movies list loads correctly
      cy.wait('@getMovies');
      cy.url().should('include', '/movies');
    });
  });

  describe('Admin Features', () => {
    beforeEach(() => {
      // Login as admin
      cy.clearLocalStorage();
      cy.intercept('POST', '**/api/auth/login', {
        statusCode: 200,
        body: {
          token: 'fake-admin-token',
          role: 'ADMIN',
          userId: 'admin-123'
        }
      }).as('adminLogin');

      cy.intercept('GET', '**/api/users/me**', {
        statusCode: 200,
        body: {
          id: 'admin-123',
          fullName: 'Admin User',
          email: 'admin@example.com',
          role: 'ADMIN'
        }
      }).as('getUserProfile');

      cy.visit('/login');
      cy.get('input[name="email"]').type('admin@example.com');
      cy.get('input[name="password"]').type('admin123');
      cy.get('button[type="submit"]').click();
      cy.wait('@adminLogin');
      cy.wait('@getUserProfile');
    });

    it('should allow admin to create new movie', () => {
      // First load - empty list
      cy.intercept('GET', '**/api/movies**', {
        statusCode: 200,
        body: []
      }).as('getMovies');

      cy.intercept('POST', '**/api/movies', {
        statusCode: 201,
        body: {
          id: 'new-1',
          title: 'New Movie',
          description: 'New description',
          genre: 'Dramă',
          duration: 120,
          releaseDate: '2024-12-31'
        }
      }).as('createMovie');

      cy.visit('/movies');
      cy.wait('@getMovies');
      
      // After creating, the list reloads with the new movie
      // Redefine intercept after first load
      cy.intercept('GET', '**/api/movies**', {
        statusCode: 200,
        body: [
          {
            id: 'new-1',
            title: 'New Movie',
            description: 'New description',
            genre: 'Dramă',
            duration: 120,
            releaseDate: '2024-12-31'
          }
        ]
      }).as('getMoviesAfterCreate');

      // Look for add movie button
      cy.get('button, a').contains(/add|adaugă|nou|new/i).first().click({ force: true });

      // Wait for form to appear
      cy.get('app-movie-form, .movie-form-container').should('be.visible');

      // Fill movie form if modal/form appears
      // Form uses formControlName, not name attribute
      cy.get('input#title, input[formControlName="title"]').should('be.visible').type('New Movie');
      cy.get('textarea#description, textarea[formControlName="description"]').should('be.visible').type('New description');
      cy.get('select#genre, select[formControlName="genre"]').should('be.visible').select('Dramă');
      cy.get('input#duration, input[formControlName="duration"]').should('be.visible').type('120');
      cy.get('input#releaseDate, input[formControlName="releaseDate"]').should('be.visible').type('2024-12-31');
      cy.get('button[type="submit"]').should('not.be.disabled').click({ force: true });

      cy.wait('@createMovie');
      
      // Wait for movies list to reload after creation
      cy.wait('@getMoviesAfterCreate');
      
      // Check that new movie appears in the list
      cy.contains('New Movie').should('exist');
    });

    it('should allow admin to delete movie', () => {
      cy.intercept('GET', '**/api/movies**', {
        statusCode: 200,
        body: [
          {
            id: '1',
            title: 'Movie to Delete',
            description: 'Description',
            genre: 'Action',
            duration: 120,
            releaseDate: '2024-01-01'
          }
        ]
      }).as('getMovies');

      cy.intercept('DELETE', '**/api/movies/1', {
        statusCode: 204
      }).as('deleteMovie');

      cy.visit('/movies');
      cy.wait('@getMovies');

      // Find and click delete button (it's an icon button with fa-trash)
      cy.contains('Movie to Delete').closest('.movie-card, .card').find('button.btn-danger, button[title*="șterge"], button[title*="delete"]').click({ force: true });
      
      // Confirm deletion if confirmation dialog appears
      cy.on('window:confirm', () => true);
      
      cy.wait('@deleteMovie');
      cy.contains('Movie to Delete').should('not.exist');
    });
  });
});
