import {
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest
} from '@angular/common/http';

export const timezoneInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  return next(
    req.clone({
      headers: req.headers.set(
        'X-Timezone',
        Intl.DateTimeFormat().resolvedOptions().timeZone
      ),
    })
  );
};
