import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { RouterTokens } from '../app.routes';

export function hasRole(role: string) {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.hasRole(role).pipe(
    tap((authorized) => {
      if (!authorized) {
        router.navigate([RouterTokens.SIGNIN]);
      }

      return authorized;
    })
  );
}
