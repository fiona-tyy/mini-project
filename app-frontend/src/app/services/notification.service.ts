import { Injectable } from '@angular/core';
import { BehaviorSubject, exhaustMap, filter, map, take, tap } from 'rxjs';
import { NotificationModel } from '../model';
import { HttpClient } from '@angular/common/http';
import { UserService } from './user.service';

@Injectable()
export class NotificationService {
  notification$: BehaviorSubject<NotificationModel | null> =
    new BehaviorSubject<NotificationModel | null>(null);

  constructor(private http: HttpClient, private userSvc: UserService) {}

  setNotification(notification: NotificationModel) {
    this.notification$.next(notification);
  }

  getNotification() {
    return this.notification$.asObservable();
  }

  subscribeNotification(token: any) {
    return this.userSvc.user.pipe(
      filter((user) => !!user),
      map((user) => user!.email.replace('@', '-')),
      exhaustMap((topic) => {
        return this.http.post('/api/notification/subscribe', {
          token: token,
          topic: topic,
        });
      })
    );
  }
}
