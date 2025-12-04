import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SeatComponent } from '../../../../halls/presentation/components/seat/seat';

@Component({
  selector: 'app-seat-grid',
  templateUrl: './seat-grid.html',
  styleUrls: ['./seat-grid.css'],
  standalone: true,
  imports: [CommonModule, SeatComponent]
})
export class SeatGridComponent {
  @Input() seats: any[] = [];
  @Output() seatSelected = new EventEmitter<any>();

  toggleSeat(seat: any) {
    // Check if seat is available (handle both status and isAvailable)
    const isAvailable = seat?.status === 'AVAILABLE' || 
                       (seat?.isAvailable === true || seat?.isAvailable === 'true') ||
                       (seat?.is_available === true || seat?.is_available === 'true');
    
    if (!isAvailable) return;
    
    seat.isSelected = !seat.isSelected;
    this.seatSelected.emit(seat);
  }

  getSeatsByRows(): any[] {
    const rowsMap = new Map<string, any[]>();
    
    // Group seats by row (row can be string or number)
    this.seats.forEach(seat => {
      const row = String(seat.row || seat.rowNumber || '0');
      if (!rowsMap.has(row)) {
        rowsMap.set(row, []);
      }
      rowsMap.get(row)!.push(seat);
    });

    // Convert to array and sort by row number, then by seat number
    return Array.from(rowsMap.entries())
      .map(([row, seats]) => ({
        row: row,
        seats: seats.sort((a, b) => (a.number || 0) - (b.number || 0))
      }))
      .sort((a, b) => {
        // Sort rows numerically if possible, otherwise alphabetically
        const aNum = parseInt(a.row);
        const bNum = parseInt(b.row);
        if (!isNaN(aNum) && !isNaN(bNum)) {
          return aNum - bNum;
        }
        return a.row.localeCompare(b.row);
      });
  }
}