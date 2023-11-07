import {
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest
} from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError } from 'rxjs';

export const oAuthLoginInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const location = inject(Location);
  return next(req).pipe(
    catchError((error) => {
      if (error.error?._links?.oauth2Login?.href) {
        location.assign(error.error._links.oauth2Login.href);
      }

      throw error;
    })
  );
};
