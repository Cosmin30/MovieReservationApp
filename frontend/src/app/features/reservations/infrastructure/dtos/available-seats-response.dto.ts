export interface AvailableSeatsResponseDTO {
  screenId: string;
  seats: { id: string; row: number; number: number; available: boolean }[];
}
