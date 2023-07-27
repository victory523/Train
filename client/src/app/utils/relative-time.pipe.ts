import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'relativeTime',
})
export class RelativeTimePipe implements PipeTransform {
  transform(date: Date): string {
    const timeMs = date.getTime();
    const deltaSeconds = Math.round((Date.now() - timeMs) / 1000);
    const cutoffs = [
      60,
      3600,
      86400,
      86400 * 7,
      86400 * 30,
      86400 * 365,
      Infinity,
    ];
    const units: Intl.RelativeTimeFormatUnit[] = [
      'second',
      'minute',
      'hour',
      'day',
      'week',
      'month',
      'year',
    ];
    const unitIndex = cutoffs.findIndex(
      (cutoff) => cutoff > Math.abs(deltaSeconds)
    );
    const divisor = unitIndex ? cutoffs[unitIndex - 1] : 1;
    const rtf = new Intl.RelativeTimeFormat('en-US', { numeric: 'auto' });
    return rtf.format(-Math.floor(deltaSeconds / divisor), units[unitIndex]);
  }
}
