import { Observable, catchError, map, of, startWith } from 'rxjs';
import { HttpRequestState } from './types';

export const initialHttpRequestState: HttpRequestState<any> = {
  isLoading: false,
  isReady: false,
  hasFailed: false,
};

export function subscribeToRequestState<T>(
  $target: Observable<T>,
  setState: (newState: HttpRequestState<T>) => void
): void {
  $target
    .pipe(
      map((value) => ({
        isLoading: false,
        isReady: true,
        hasFailed: false,
        value,
      })),
      catchError((error) => {
        console.log(error);
        return of({ isLoading: false, isReady: false, hasFailed: true, error });
      }),
      startWith({ isLoading: true, isReady: false, hasFailed: false })
    )
    .subscribe(setState);
}
