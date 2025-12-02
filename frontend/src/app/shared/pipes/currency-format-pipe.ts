import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'currencyFormat',
  standalone: true
})
export class CurrencyFormatPipe implements PipeTransform {

  transform(value: number, currency: string = 'Lei'): string {
    if (value == null) return '';
    return `${value.toFixed(2)} ${currency}`;
  }
}
