import { Pipe, PipeTransform } from '@angular/core';

function numberWithThousandSeparator(x: number) {
  return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
}

@Pipe({
  name: 'measurementWithUnit',
  standalone: true,
})
export class MeasurementWithUnitPipe implements PipeTransform {
  transform(
    value: number = 0,
    unit: string = '',
    decimals: number = 1,
    divider: number = 1,
  ): string {
    const multiplier = Math.pow(10, decimals);
    const clampedValue = parseInt((multiplier * value / divider).toFixed(0));

    if (!clampedValue) {
      return '-';
    }

    if (unit === 'min') {
      const hours = Math.floor(clampedValue / 60);
      const minutes = clampedValue - hours * 60;
      return [hours && `${numberWithThousandSeparator(hours)} h`, minutes && `${minutes} min`].filter(Boolean).join(' ');
    }

    return [numberWithThousandSeparator(clampedValue / multiplier), unit].filter(Boolean).join(' ');
  }
}
