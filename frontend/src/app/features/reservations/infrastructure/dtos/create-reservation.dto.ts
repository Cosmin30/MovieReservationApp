export interface CreateReservationDTO {
  userId: string;
  screeningId: string;
  seatIds: string[];
  pricePerSeat: number;
}
