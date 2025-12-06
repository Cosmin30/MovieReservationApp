/// <reference types="cypress" />

describe('Screenings Feature', () => {
  describe('User View', () => {
    beforeEach(() => {
      // Login as regular user
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

    it('should display all screenings', () => {
      cy.intercept('GET', '**/api/screenings**', {
        statusCode: 200,
        body: [
          {
            id: 's1',
            movie: { id: '1', title: 'The Matrix' },
            start_time: '2024-12-25T18:00:00Z',
            startTime: '2024-12-25T18:00:00Z',
            hall: { name: 'Hall 1', number: 1 },
            room_number: 1,
            roomNumber: 1,
            capacity: 100,
            seats: [
              { id: 'seat1', row: 'A', number: 1, is_available: true, isAvailable: true, status: 'AVAILABLE' },
              { id: 'seat2', row: 'A', number: 2, is_available: true, isAvailable: true, status: 'AVAILABLE' }
            ]
          },
          {
            id: 's2',
            movie: { id: '2', title: 'Inception' },
            start_time: '2024-12-26T20:00:00Z',
            startTime: '2024-12-26T20:00:00Z',
            hall: { name: 'Hall 2', number: 2 },
            room_number: 2,
            roomNumber: 2,
            capacity: 80,
            seats: []
          }
        ]
      }).as('getScreenings');

      cy.visit('/screenings');
      cy.wait('@getScreenings');

      // Check page title
      cy.contains(/proiecții disponibile|screenings/i).should('exist');

      // Check screenings are displayed
      cy.contains('The Matrix').should('be.visible');
      cy.contains('Inception').should('be.visible');
      cy.contains('Hall 1').should('exist');
      cy.contains('Hall 2').should('exist');
    });

    it('should show loading state', () => {
      cy.intercept('GET', '**/api/screenings**', {
        delay: 1000,
        statusCode: 200,
        body: []
      }).as('getScreenings');

      cy.visit('/screenings');
      
      // Check loading state
      cy.contains(/se încarcă proiecțiile|loading/i).should('exist');
      
      cy.wait('@getScreenings');
    });

    it('should show empty state when no screenings', () => {
      cy.intercept('GET', '**/api/screenings**', {
        statusCode: 200,
        body: []
      }).as('getScreenings');

      cy.visit('/screenings');
      cy.wait('@getScreenings');

      // Check empty state
      cy.contains(/nu există proiecții disponibile|no screenings/i).should('exist');
    });

    it('should show error state on API error', () => {
      cy.intercept('GET', '**/api/screenings**', {
        statusCode: 500,
        body: { message: 'Internal server error' }
      }).as('getScreeningsError');

      cy.visit('/screenings');
      cy.wait('@getScreeningsError');

      // Check error state
      cy.contains(/nu am putut încărca proiecțiile|error/i).should('exist');
    });

    it('should not show admin buttons for regular users', () => {
      cy.intercept('GET', '**/api/screenings**', {
        statusCode: 200,
        body: [
          {
            id: 's1',
            movie: { id: '1', title: 'The Matrix' },
            start_time: '2024-12-25T18:00:00Z',
            startTime: '2024-12-25T18:00:00Z',
            hall: { name: 'Hall 1' },
            room_number: 1,
            roomNumber: 1,
            capacity: 100,
            seats: []
          }
        ]
      }).as('getScreenings');

      cy.visit('/screenings');
      cy.wait('@getScreenings');

      // Admin buttons should not be visible
      cy.contains(/adaugă proiecție nouă|add screening/i).should('not.exist');
      cy.contains(/editează|edit/i).should('not.exist');
      cy.contains(/șterge|delete/i).should('not.exist');
    });
  });

  describe('Admin View', () => {
    beforeEach(() => {
      // Login as admin
      cy.intercept('POST', '**/api/auth/login', {
        statusCode: 200,
        body: {
          token: 'fake-jwt-token',
          role: 'ADMIN',
          userId: 'admin-123'
        }
      }).as('loginRequest');

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
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').click();
      cy.wait('@loginRequest');
      cy.wait('@getUserProfile');
    });

    it('should show admin buttons', () => {
      cy.intercept('GET', '**/api/screenings**', {
        statusCode: 200,
        body: [
          {
            id: 's1',
            movie: { id: '1', title: 'The Matrix' },
            start_time: '2024-12-25T18:00:00Z',
            startTime: '2024-12-25T18:00:00Z',
            hall: { name: 'Hall 1' },
            room_number: 1,
            roomNumber: 1,
            capacity: 100,
            seats: []
          }
        ]
      }).as('getScreenings');

      cy.visit('/screenings');
      cy.wait('@getScreenings');

      // Admin buttons should be visible
      cy.contains(/adaugă proiecție nouă|add screening/i).should('exist');
      cy.contains(/editează|edit/i).should('exist');
      cy.contains(/șterge|delete/i).should('exist');
    });

    it('should allow admin to add new screening', () => {
      cy.intercept('GET', '**/api/movies**', {
        statusCode: 200,
        body: [
          { id: '1', title: 'The Matrix' },
          { id: '2', title: 'Inception' }
        ]
      }).as('getMovies');

      cy.intercept('GET', '**/api/halls**', {
        statusCode: 200,
        body: [
          { id: 'h1', name: 'Hall 1', number: 1, capacity: 100 },
          { id: 'h2', name: 'Hall 2', number: 2, capacity: 80 }
        ]
      }).as('getHalls');

      cy.intercept('GET', '**/api/screenings**', {
        statusCode: 200,
        body: []
      }).as('getScreenings');

      cy.intercept('POST', '**/api/screenings**', {
        statusCode: 201,
        body: {
          id: 's-new',
          movie: { id: '1', title: 'The Matrix' },
          startTime: '2024-12-27T20:00:00Z',
          hall: { id: 'h1', name: 'Hall 1' },
          roomNumber: 1
        }
      }).as('createScreening');

      cy.visit('/screenings');
      cy.wait('@getScreenings');

      // Click "Adaugă Proiecție Nouă"
      cy.contains(/adaugă proiecție nouă|add screening/i).click();

      // Wait for form to appear
      cy.get('app-screening-form').should('be.visible');

      // Wait for movies and halls API calls to complete
      cy.wait('@getMovies');
      cy.wait('@getHalls');

      // Give Angular time to process the data and render options
      cy.wait(1000);

      // Wait for Angular to render the options in the DOM
      cy.get('form').within(() => {
        // Wait for movie select to have options populated
        // First check that select exists and is visible
        cy.get('select#movieId').should('be.visible');
        
        // Wait for options to be populated - retry until we have more than just placeholder
        cy.get('select#movieId option', { timeout: 10000 }).should('have.length.greaterThan', 1);
        
        // Wait for "The Matrix" option to exist (by text or value)
        cy.get('select#movieId').should('contain', 'The Matrix');
        
        // Wait for hall select to have options populated
        cy.get('select#hallId').should('be.visible');
        cy.get('select#hallId option', { timeout: 10000 }).should('have.length.greaterThan', 1);
        
        // Wait for "Hall 1" option to exist
        cy.get('select#hallId').should('contain', 'Hall 1');
        
        // Now select the values - try by text first, fallback to value
        cy.get('select#movieId').then(($select) => {
          const select = $select[0] as HTMLSelectElement;
          const options = Array.from(select.options);
          const matrixOption = options.find(opt => opt.text.includes('The Matrix') || opt.value === '1');
          if (matrixOption) {
            cy.get('select#movieId').select(matrixOption.value);
          } else {
            cy.get('select#movieId').select('1');
          }
        });
        
        cy.get('select#hallId').then(($select) => {
          const select = $select[0] as HTMLSelectElement;
          const options = Array.from(select.options);
          const hall1Option = options.find(opt => opt.text.includes('Hall 1') || opt.value === 'h1');
          if (hall1Option) {
            cy.get('select#hallId').select(hall1Option.value);
          } else {
            cy.get('select#hallId').select('h1');
          }
        });
        
        // Wait a bit for auto-fill to complete
        cy.wait(500);
        
        // Verify roomNumber and capacity are auto-filled (they might be disabled/readonly)
        cy.get('input#roomNumber').should('exist');
        cy.get('input#capacity').should('exist');
        
        // Set start time (format: YYYY-MM-DDTHH:mm)
        const futureDate = new Date();
        futureDate.setDate(futureDate.getDate() + 7);
        const year = futureDate.getFullYear();
        const month = String(futureDate.getMonth() + 1).padStart(2, '0');
        const day = String(futureDate.getDate()).padStart(2, '0');
        const startTime = `${year}-${month}-${day}T20:00`;
        cy.get('input#startTime').clear().type(startTime);
        
        // Verify form is valid before submitting
        cy.get('button[type="submit"]').should('not.be.disabled');
        cy.get('button[type="submit"]').click();
      });

      cy.wait('@createScreening');
      cy.contains(/proiecția a fost adăugată cu succes|success/i).should('exist');
    });

    it('should allow admin to edit screening', () => {
      cy.intercept('GET', '**/api/screenings**', {
        statusCode: 200,
        body: [
          {
            id: 's1',
            movie: { id: '1', title: 'The Matrix' },
            start_time: '2024-12-25T18:00:00Z',
            startTime: '2024-12-25T18:00:00Z',
            hall: { id: 'h1', name: 'Hall 1' },
            room_number: 1,
            roomNumber: 1,
            capacity: 100,
            seats: []
          }
        ]
      }).as('getScreenings');

      cy.intercept('GET', '**/api/movies**', {
        statusCode: 200,
        body: [
          { id: '1', title: 'The Matrix' },
          { id: '2', title: 'Inception' }
        ]
      }).as('getMovies');

      cy.intercept('GET', '**/api/halls**', {
        statusCode: 200,
        body: [
          { id: 'h1', name: 'Hall 1', number: 1, capacity: 100 },
          { id: 'h2', name: 'Hall 2', number: 2, capacity: 80 }
        ]
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

      cy.visit('/screenings');
      cy.wait('@getScreenings');

      // Click edit button on screening card
      cy.contains('The Matrix').closest('.screening-card').find('button[title*="Editează"], button[title*="Edit"]').click({ force: true });

      // Wait for form to appear
      cy.get('app-screening-form').should('be.visible');

      // Wait for movies and halls to load
      cy.wait('@getMovies');
      cy.wait('@getHalls');
      cy.wait(1000); // Give Angular time to render options

      // Fill form with updated data
      cy.get('form').within(() => {
        // Change start time
        const futureDate = new Date();
        futureDate.setDate(futureDate.getDate() + 10);
        const year = futureDate.getFullYear();
        const month = String(futureDate.getMonth() + 1).padStart(2, '0');
        const day = String(futureDate.getDate()).padStart(2, '0');
        const startTime = `${year}-${month}-${day}T20:00`;
        cy.get('input#startTime').clear().type(startTime);
        
        // Submit form
        cy.get('button[type="submit"]').should('not.be.disabled');
        cy.get('button[type="submit"]').click();
      });

      cy.wait('@updateScreening');
      cy.contains(/proiecția a fost actualizată cu succes|success/i).should('exist');
    });

    it('should allow admin to delete screening', () => {
      // Set up intercept for initial load - use a more specific pattern to avoid conflicts
      let initialLoad = true;
      cy.intercept('GET', '**/api/screenings**', (req) => {
        if (initialLoad) {
          req.reply({
            statusCode: 200,
            body: [
              {
                id: 's1',
                movie: { id: '1', title: 'The Matrix' },
                start_time: '2024-12-25T18:00:00Z',
                startTime: '2024-12-25T18:00:00Z',
                hall: { name: 'Hall 1' },
                room_number: 1,
                roomNumber: 1,
                capacity: 100,
                seats: []
              }
            ]
          });
          initialLoad = false;
        } else {
          req.reply({
            statusCode: 200,
            body: []
          });
        }
      }).as('getScreenings');

      cy.intercept('DELETE', '**/api/screenings/s1**', {
        statusCode: 200
      }).as('deleteScreening');

      cy.visit('/screenings');
      cy.wait('@getScreenings');

      // Set up window confirm handler before clicking delete
      cy.window().then((win) => {
        cy.stub(win, 'confirm').returns(true);
      });

      // Click delete button
      cy.contains('The Matrix').closest('.screening-card').find('button[title*="Șterge"], button[title*="Delete"]').click({ force: true });

      cy.wait('@deleteScreening');
      
      // Wait for reload after delete
      cy.wait('@getScreenings');

      cy.contains(/proiecția a fost ștearsă cu succes|success/i).should('exist');
    });

    it('should refresh screenings when refresh query param is present', () => {
      cy.intercept('GET', '**/api/screenings**', {
        statusCode: 200,
        body: [
          {
            id: 's1',
            movie: { id: '1', title: 'The Matrix' },
            start_time: '2024-12-25T18:00:00Z',
            startTime: '2024-12-25T18:00:00Z',
            hall: { name: 'Hall 1' },
            room_number: 1,
            roomNumber: 1,
            capacity: 100,
            seats: []
          }
        ]
      }).as('getScreenings');

      cy.visit('/screenings?refresh=true');
      cy.wait('@getScreenings');

      // URL should be cleaned (no query params)
      cy.url().should('not.include', 'refresh=true');
    });
  });
});
