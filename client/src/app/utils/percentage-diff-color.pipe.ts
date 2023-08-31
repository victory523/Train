import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'percentageDiffColor',
  standalone: true
})
export class PercentageDiffColorPipe implements PipeTransform {

  transform(value: number = 0, inverse?: 'inverse'): 'green' | 'red' | undefined {
    const clampedValue = parseInt((1000 * value).toFixed(0));

    if (clampedValue > 0) {
      return inverse ? 'red': 'green';
    } else if (clampedValue < 0) {
      return inverse ? 'green': 'red';
    }

    return;
  }

}
