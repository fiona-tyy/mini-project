import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
  HttpParams,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  BehaviorSubject,
  Subject,
  catchError,
  exhaustMap,
  firstValueFrom,
  take,
  tap,
  throwError,
} from 'rxjs';
import { Friend, User, UserDTO } from '../model';
import { Router } from '@angular/router';

@Injectable()
export class UserService {
  user = new BehaviorSubject<UserDTO | null>(null);
  // friendsOfUser = new Subject<Friend[]>();
  activeUser!: UserDTO | null;
  friends!: Friend[];
  friendsOutstanding!: Friend[];
  expirationTimer!: any;
  constructor(private http: HttpClient, private router: Router) {}

  signup(name: string, email: string, password: string) {
    const form = new HttpParams()
      .set('name', name)
      .set('email', email)
      .set('password', password);

    const headers = new HttpHeaders().set(
      'Content-Type',
      'application/x-www-form-urlencoded'
    );

    return this.http
      .post<UserDTO>('/api/user/new', form.toString(), { headers })
      .pipe(
        catchError(this.handleError)
        // tap((result) => {
        //   this.user.next(result);
        // })
      );
  }

  login(email: string, password: string) {
    const form = new HttpParams().set('email', email).set('password', password);

    const headers = new HttpHeaders().set(
      'Content-Type',
      'application/x-www-form-urlencoded'
    );

    return this.http
      .post<UserDTO>('/api/user', form.toString(), { headers })
      .pipe(
        catchError(this.handleError),
        tap((result) => {
          this.user.next(result);
          this.activeUser = result;
          this.autoLogout(result.token_expiration_date - new Date().getTime());
          localStorage.setItem('userData', JSON.stringify(result));
        })
      );
  }

  loginWithGoogle(email: string, googleToken: string) {
    const form = new HttpParams()
      .set('email', email)
      .set('googleToken', googleToken);
    const headers = new HttpHeaders().set(
      'Content-Type',
      'application/x-www-form-urlencoded'
    );
    return this.http
      .post<UserDTO>('/api/user/google-login', form.toString(), { headers })
      .pipe(
        tap((result) => {
          this.user.next(result);
          this.activeUser = result;
          this.autoLogout(result.token_expiration_date - new Date().getTime());
          localStorage.setItem('userData', JSON.stringify(result));
          this.router.navigate(['/home']);
        })
      );
  }

  autoLogin() {
    const userData: {
      id: string;
      name: string;
      email: string;
      token: string;
      token_expiration_date: number;
    } = JSON.parse(localStorage.getItem('userData')!);
    if (!userData) {
      return;
    }
    const loadedUser: UserDTO = {
      name: userData.name,
      email: userData.email,
      token: userData.token,
      token_expiration_date: userData.token_expiration_date,
    };
    if (loadedUser.token_expiration_date > new Date().getTime()) {
      this.user.next(loadedUser);
      this.activeUser = loadedUser;
      this.autoLogout(loadedUser.token_expiration_date - new Date().getTime());
    }
  }

  getFriendsOfActiveUser() {
    return this.user.pipe(
      take(1),
      exhaustMap((user) => {
        return this.http.get<Friend[]>('/api/user/' + user?.email + '/friends');
      })
    );
  }

  addFriend(email: string) {
    const params = new HttpParams().set('email', email);
    return this.user.pipe(
      take(1),
      exhaustMap((user) => {
        return this.http.get('/api/user/' + user?.email + '/add-friend', {
          params,
        });
      })
    );
  }

  requestCode(email: string) {
    const params = new HttpParams().set('email', email);
    return this.http
      .get('/api/user/request-code', { params })
      .pipe(catchError(this.handleError));
  }

  resetPassword(code: string, password: string) {
    const form = new HttpParams()
      .set('oobCode', code)
      .set('newPassword', password);
    const headers = new HttpHeaders().set(
      'Content-Type',
      'application/x-www-form-urlencoded'
    );
    return this.http
      .post<{email:string}>('/api/user/new', form.toString(), { headers })
      .pipe(catchError(this.handleError));
  }

  logout() {
    this.user.next(null);
    this.activeUser = null;
    localStorage.removeItem('userData');
    if (this.expirationTimer) {
      clearTimeout(this.expirationTimer);
    }
    this.expirationTimer = null;
  }
  autoLogout(expirationDuration: number) {
    this.expirationTimer = setTimeout(() => {
      this.logout;
    }, expirationDuration);
  }

  private handleError(errorRes: HttpErrorResponse) {
    console.info('>>what is this error ', errorRes);
    let errorMessage = 'An unknown error occurred!';
    if (!errorRes.error || !errorRes.error.error) {
      return throwError(() => new Error(errorMessage));
    }
    if (errorRes.error.message) {
      errorMessage = errorRes.error.message;
    } else {
      switch (errorRes.error.error.message) {
        case 'EMAIL_EXISTS':
          errorMessage = 'An account with this email already exists.';
          break;
        case 'EMAIL_NOT_FOUND':
          errorMessage = 'Email not found';
          break;
        case 'INVALID_PASSWORD':
          errorMessage = 'Password is incorrect.';
          break;
        case 'TOO_MANY_ATTEMPTS_TRY_LATER':
          errorMessage =
            'Too many unsuccessful login attempts. Try again later.';
          break;
        case 'EXPIRED_OOB_CODE':
          errorMessage = 'The code has expired. Request for a new code.';
          break;
        case 'INVALID_OOB_CODE':
          errorMessage = 'The code is invalid.';
          break;
        case 'USER_DISABLED':
          errorMessage =
            'The user account has been disabled by an administrator.';
          break;
      }
    }
    return throwError(() => new Error(errorMessage));
  }
}
