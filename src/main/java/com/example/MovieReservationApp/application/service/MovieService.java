package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.MovieDTO;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.infrastructure.persistence.repository.MovieRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ScreeningRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.SeatRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;

    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MovieDTO getMovieById(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
        return toDTO(movie);
    }

    public MovieDTO createMovie(MovieDTO dto) {
        Movie movie = toEntity(dto);
        movie.setId(null); // Asigură că e nou
        movie = movieRepository.save(movie);
        return toDTO(movie);
    }

    public MovieDTO updateMovie(UUID id, MovieDTO dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setDuration(dto.getDuration());
        movie.setGenre(dto.getGenre());
        movie.setReleaseDate(dto.getReleaseDate());

        movie = movieRepository.save(movie);
        return toDTO(movie);
    }

    public MovieDTO patchMovie(UUID id, MovieDTO dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        if (dto.getTitle() != null) {
            movie.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            movie.setDescription(dto.getDescription());
        }
        if (dto.getGenre() != null) {
            movie.setGenre(dto.getGenre());
        }
        if (dto.getDuration() != null) {
            movie.setDuration(dto.getDuration());
        }
        if (dto.getReleaseDate() != null) {
            movie.setReleaseDate(dto.getReleaseDate());
        }

        movie = movieRepository.save(movie);
        return toDTO(movie);
    }

    @Transactional
    public void deleteMovie(UUID id) {
        System.out.println("🔍 [MOVIE SERVICE] Starting deletion of movie: " + id);
        
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        System.out.println("🔍 [MOVIE SERVICE] Movie found: " + movie.getTitle());
        
        // Get all screenings for this movie
        List<com.example.MovieReservationApp.domain.model.screening.Screening> screenings = 
            screeningRepository.findByMovieId(id);
        
        System.out.println("🔍 [MOVIE SERVICE] Found " + screenings.size() + " screenings for this movie");
        
        // For each screening, delete reservations and tickets
        for (var screening : screenings) {
            System.out.println("🔍 [MOVIE SERVICE] Processing screening: " + screening.getId());
            
            // Step 1: Delete all reservations for this screening (this will cascade delete tickets)
            var reservations = reservationRepository.findByScreening_Id(screening.getId());
            System.out.println("🔍 [MOVIE SERVICE] Found " + reservations.size() + " reservations for screening " + screening.getId());
            
            for (var reservation : reservations) {
                System.out.println("🔍 [MOVIE SERVICE] Deleting reservation: " + reservation.getId());
                reservationRepository.delete(reservation); // This will cascade delete tickets
            }
            
            // Step 2: Get all seats for this screening
            var seats = seatRepository.findByScreeningId(screening.getId());
            System.out.println("🔍 [MOVIE SERVICE] Found " + seats.size() + " seats for screening " + screening.getId());
            
            // Step 3: Delete any remaining tickets for these seats (shouldn't exist, but just in case)
            for (var seat : seats) {
                var tickets = ticketRepository.findBySeatId(seat.getId());
                if (!tickets.isEmpty()) {
                    System.out.println("🔍 [MOVIE SERVICE] Found " + tickets.size() + " remaining tickets for seat " + seat.getId());
                    for (var ticket : tickets) {
                        System.out.println("🔍 [MOVIE SERVICE] Deleting remaining ticket: " + ticket.getId());
                        ticketRepository.delete(ticket);
                    }
                }
            }
        }
        
        System.out.println("🔍 [MOVIE SERVICE] All reservations and tickets deleted. Now deleting movie...");
        // Now delete the movie (this will cascade delete screenings and seats)
        movieRepository.delete(movie);
        System.out.println("✅ [MOVIE SERVICE] Movie deleted successfully");
    }

    // Metode helper pentru conversie
    private MovieDTO toDTO(Movie movie) {
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());
        dto.setGenre(movie.getGenre());
        dto.setReleaseDate(movie.getReleaseDate());
        return dto;
    }

    private Movie toEntity(MovieDTO dto) {
        Movie movie = new Movie();
        movie.setId(dto.getId());
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setDuration(dto.getDuration());
        movie.setGenre(dto.getGenre());
        movie.setReleaseDate(dto.getReleaseDate());
        return movie;
    }
}