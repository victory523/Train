import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'measurementWithUnit',
  standalone: true,
})
export class MeasurementWithUnitPipe implements PipeTransform {
  transform(value?: number, unit?: string): string {
    if (typeof value !== 'number' || !unit) {
      return '-';
    }

    return [value, unit].join(' ');
  }
}
