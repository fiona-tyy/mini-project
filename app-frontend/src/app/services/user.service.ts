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
  firstValueFrom,
  tap,
  throwError,
} from 'rxjs';
import { Friend, User, UserDTO } from '../model';

@Injectable()
export class UserService {
  user = new BehaviorSubject<UserDTO | null>(null);
  friendsOfUser = new Subject<Friend[]>();
  activeUser!: UserDTO | null;
  friends!: Friend[];
  friendsOutstanding!: Friend[];
  constructor(private http: HttpClient) {}

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
        })
      );
  }

  // getActiveUser(email: string) {
  //   const form = new HttpParams().set('email', email);

  //   const headers = new HttpHeaders().set(
  //     'Content-Type',
  //     'application/x-www-form-urlencoded'
  //   );

  //   return firstValueFrom(
  //     this.http.post<User>('/api/user', form.toString(), { headers })
  //   );
  // }

  //friends-1
  // getFriendsOfActiveUser(userId: String) {
  //   return this.http.get<Friend[]>('/api/user/' + userId + '/friends');
  // }

  getFriendsOfActiveUser(userId: String) {
    return this.http.get<Friend[]>('/api/user/' + userId + '/friends');
  }

  addFriend(userId: string, email: string) {
    const params = new HttpParams().set('email', email);
    return this.http.get('/api/user/' + userId + '/add-friend', { params });
  }

  logout() {
    this.user.next(null);
    this.activeUser = null;
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
        case 'INVALID_PASSWORD':
          errorMessage = 'Email and/or password is incorrect.';
          break;
        case 'TOO_MANY_ATTEMPTS_TRY_LATER':
          errorMessage =
            'Too many unsuccessful login attempts. Try again later.';
          break;
      }
    }
    return throwError(() => new Error(errorMessage));
  }
}
