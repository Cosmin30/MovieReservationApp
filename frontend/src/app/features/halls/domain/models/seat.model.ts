export interface SeatModel {
  id: string;
  row: number;
  number: number;
  status: 'AVAILABLE' | 'RESERVED' | 'UNAVAILABLE';
  isSelected?: boolean;
}
