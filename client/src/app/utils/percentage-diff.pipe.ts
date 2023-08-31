import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'percentageDiff',
  standalone: true,
})
export class PercentageDiffPipe implements PipeTransform {
  transform(value: number = 0): string {
    const clampedValue = parseInt((1000 * value).toFixed(0));

    if (clampedValue > 0) {
      return `↑ ${clampedValue / 10} %`;
    } else if (clampedValue < 0) {
      return `↓ ${-clampedValue / 10} %`;
    }

    return '-';
  }
}
