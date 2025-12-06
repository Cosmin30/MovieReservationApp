export interface SeatDTO {
  id?: string;
  row?: number | string; // Backend can send string or number
  number?: number;
  status?: string;
  is_available?: boolean; // Backend sends snake_case
  isAvailable?: boolean; // camelCase variant
  available?: boolean; // Alternative format
}
