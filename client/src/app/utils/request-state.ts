import { HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, map, of, startWith } from 'rxjs';

export type HttpRequestState<T> =
  | {
      isLoading: false;
      isReady: false;
      hasFailed: false;
    }
  | {
      isLoading: true;
      isReady: false;
      hasFailed: false;
    }
  | {
      isLoading: false;
      isReady: true;
      hasFailed: false;
      value: T;
    }
  | {
      isLoading: false;
      isReady: false;
      hasFailed: true;
      error: HttpErrorResponse | Error;
    };

export const initialHttpRequestState: HttpRequestState<any> = {
  isLoading: false,
  isReady: false,
  hasFailed: false,
};

export function requestState<T>(
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
        return of({ isLoading: false, isReady: false, hasFailed: true, error });
      }),
      startWith({ isLoading: true, isReady: false, hasFailed: false })
    )
    .subscribe((newState) => setState(newState as HttpRequestState<T>));
}
