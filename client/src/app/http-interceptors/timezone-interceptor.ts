import {
  HTTP_INTERCEPTORS,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable, Provider } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class TimezoneInterceptor implements HttpInterceptor {
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(
      req.clone({
        headers: req.headers.set(
          'X-Timezone',
          Intl.DateTimeFormat().resolvedOptions().timeZone
        ),
      })
    );
  }
}

export function provideTimezoneInterceptor(): Provider {
  return {
    provide: HTTP_INTERCEPTORS,
    useClass: TimezoneInterceptor,
    multi: true,
  };
}
