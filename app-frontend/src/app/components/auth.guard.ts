import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { UserService } from '../services/user.service';
import { inject } from '@angular/core';
import { map, take } from 'rxjs';

export function AuthGuard(): CanActivateFn {
  return () => {
    const userSvc = inject(UserService);
    const router = inject(Router);

    return userSvc.user.pipe(
      take(1),
      map((user) => {
        const isAuth = !!user;
        if (isAuth) {
          return true;
        } else {
          return router.createUrlTree(['/']);
        }
      })
    );
  };
}
