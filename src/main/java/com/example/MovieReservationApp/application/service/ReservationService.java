package com.example.MovieReservationApp.application.service;

import com.example.MovieReservationApp.application.dto.*;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.domain.model.seat.Seat;
import com.example.MovieReservationApp.domain.model.ticket.Ticket;
import com.example.MovieReservationApp.domain.model.user.User;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.movie.Movie;
import com.example.MovieReservationApp.domain.model.hall.Hall;
import com.example.MovieReservationApp.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;

    // -------------------------------
    // CREATE RESERVATION
    // -------------------------------
    public ReservationDTO createReservation(UUID userId, UUID screeningId, List<UUID> seatIds, BigDecimal pricePerSeat) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Screening not found"));

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setScreening(screening);
        reservation.setCreatedAt(OffsetDateTime.now());
        reservation.setStatus("CREATED");
        reservation.setTotalPrice(pricePerSeat.multiply(BigDecimal.valueOf(seatIds.size())));
        reservationRepository.save(reservation);

        List<TicketDTO> tickets = seatIds.stream().map(seatId -> {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));
            if (!seat.getIsAvailable()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat " + seat.getNumber() + " in row " + seat.getRow() + " is already taken");
            }
            seat.setIsAvailable(false);

            Ticket ticket = new Ticket();
            ticket.setReservation(reservation);
            ticket.setSeat(seat);
            ticket.setPrice(pricePerSeat);
            ticketRepository.save(ticket);

            TicketDTO ticketDTO = new TicketDTO();
            ticketDTO.setId(ticket.getId());
            ticketDTO.setPrice(ticket.getPrice());
            ticketDTO.setSeatId(seat.getId());
            ticketDTO.setReservationId(reservation.getId());
            // Include seat information
            SeatDTO seatDTO = new SeatDTO();
            seatDTO.setId(seat.getId());
            seatDTO.setNumber(seat.getNumber());
            seatDTO.setRow(seat.getRow());
            seatDTO.setIsAvailable(seat.getIsAvailable());
            ticketDTO.setSeat(seatDTO);
            return ticketDTO;
        }).collect(Collectors.toList());

        return mapReservationToDTO(reservation, tickets);
    }

    // -------------------------------
    // GET ALL RESERVATIONS
    // -------------------------------
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapReservationToDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------
    // GET RESERVATION BY ID
    // -------------------------------
    public ReservationDTO getReservationById(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
        return mapReservationToDTO(reservation);
    }

    // -------------------------------
    // GET RESERVATIONS BY USER
    // -------------------------------
    public List<ReservationDTO> getReservationsByUser(UUID userId) {
        return reservationRepository.findByUser_Id(userId).stream()
                .map(this::mapReservationToDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------
    // UPDATE COMPLET
    // -------------------------------
    public ReservationDTO updateReservation(UUID id, ReservationDTO dto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        reservation.setStatus(dto.getStatus());
        reservation.setTotalPrice(dto.getTotalPrice());
        reservationRepository.save(reservation);

        return mapReservationToDTO(reservation);
    }

    // -------------------------------
    // PATCH (UPDATE PARȚIAL)
    // -------------------------------
    public ReservationDTO patchReservation(UUID id, ReservationDTO dto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        if (dto.getStatus() != null) reservation.setStatus(dto.getStatus());
        if (dto.getTotalPrice() != null) reservation.setTotalPrice(dto.getTotalPrice());

        reservationRepository.save(reservation);
        return mapReservationToDTO(reservation);
    }

    // -------------------------------
    // DELETE
    // -------------------------------
    public void deleteReservation(UUID id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found");
        }
        reservationRepository.deleteById(id);
    }

    // -------------------------------
    // HELPER METHODS: DTO MAPPING
    // -------------------------------
    private ReservationDTO mapReservationToDTO(Reservation reservation) {
        List<TicketDTO> tickets = reservation.getTickets().stream()
                .map(ticket -> {
                    TicketDTO tDto = new TicketDTO();
                    tDto.setId(ticket.getId());
                    tDto.setSeatId(ticket.getSeat().getId());
                    tDto.setReservationId(reservation.getId());
                    tDto.setPrice(ticket.getPrice());
                    // Include seat information
                    SeatDTO seatDTO = new SeatDTO();
                    seatDTO.setId(ticket.getSeat().getId());
                    seatDTO.setNumber(ticket.getSeat().getNumber());
                    seatDTO.setRow(ticket.getSeat().getRow());
                    seatDTO.setIsAvailable(ticket.getSeat().getIsAvailable());
                    tDto.setSeat(seatDTO);
                    return tDto;
                }).collect(Collectors.toList());
        return mapReservationToDTO(reservation, tickets);
    }

    private ReservationDTO mapReservationToDTO(Reservation reservation, List<TicketDTO> tickets) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(reservation.getUser().getId());
        userDTO.setEmail(reservation.getUser().getEmail());
        userDTO.setFullName(reservation.getUser().getFullName());
        userDTO.setCreatedAt(reservation.getUser().getCreatedAt());

        ScreeningDTO screeningDTO = mapScreeningToDTO(reservation.getScreening());

        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setUser(userDTO);
        dto.setScreening(screeningDTO);
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setStatus(reservation.getStatus());
        dto.setTotalPrice(reservation.getTotalPrice());
        dto.setTickets(tickets);
        return dto;
    }

    private ScreeningDTO mapScreeningToDTO(Screening screening) {
        ScreeningDTO dto = new ScreeningDTO();
        dto.setId(screening.getId());
        dto.setStartTime(screening.getStartTime());
        dto.setRoomNumber(screening.getRoomNumber());
        dto.setCapacity(screening.getCapacity());
        dto.setMovie(mapMovieToDTO(screening.getMovie()));
        dto.setHall(mapHallToDTO(screening.getHall()));
        dto.setSeats(screening.getSeats().stream().map(seat -> {
            SeatDTO seatDTO = new SeatDTO();
            seatDTO.setId(seat.getId());
            seatDTO.setNumber(seat.getNumber());
            seatDTO.setRow(seat.getRow());
            seatDTO.setIsAvailable(seat.getIsAvailable());
            seatDTO.setScreeningId(screening.getId());
            return seatDTO;
        }).collect(Collectors.toList()));
        return dto;
    }

    private MovieDTO mapMovieToDTO(Movie movie) {
        if (movie == null) return null;
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());
        dto.setGenre(movie.getGenre());
        dto.setReleaseDate(movie.getReleaseDate());
        return dto;
    }

    private HallDTO mapHallToDTO(Hall hall) {
        if (hall == null) return null;
        HallDTO dto = new HallDTO();
        dto.setId(hall.getId());
        dto.setName(hall.getName());
        dto.setNumber(hall.getNumber());
        dto.setCapacity(hall.getCapacity());
        return dto;
    }
}
