import { Component, OnDestroy, OnInit } from '@angular/core';
import { Transaction, User, UserDTO } from './model';
import { UserService } from './services/user.service';
import {
  Observable,
  Subscription,
  exhaustMap,
  firstValueFrom,
  tap,
} from 'rxjs';
import { Router } from '@angular/router';
import { ExpenseService } from './services/expense.service';
import { AngularFireMessaging } from '@angular/fire/compat/messaging';
import { NotificationService } from './services/notification.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  // isAuthenticated = false;
  // activeUser!: UserDTO | null;
  // userSub$!: Subscription;
  // recent$!: Observable<Transaction[]>;
  token!: any;

  constructor(
    private userSvc: UserService,
    private afMessaging: AngularFireMessaging,
    private notificationSvc: NotificationService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.userSvc.autoLogin();

    // this.afMessaging.requestToken
    //   .pipe(
    //     tap((token) => {
    //       console.info('token: ', token);
    //       this.token = token;
    //     }),
    //     exhaustMap((token) => this.notificationSvc.subscribeNotification(token))
    //   )
    //   .subscribe();

    this.afMessaging.messages.subscribe((msg) => {
      console.info('new message:', msg);
      this.notificationSvc.setNotification({
        body: msg.notification!.body!,
        title: msg.notification!.title!,
        isVisible: true,
      });
    });
  }

  // sendNotification() {
  //   console.info('token sent ', this.token);
  //   // firstValueFrom(this.notificationSvc.sendNotification()).then((result) =>
  //   //   console.info('email replaced ', result)
  //   // );
  //   firstValueFrom(
  //     this.http.post('/api/notification/send', {
  //       token: this.token,
  //     })
  //   );
  // }

  // getTransaction(transactionId: string) {
  //   this.router.navigate(['/record', transactionId]);
  // }

  // logout() {
  //   this.userSvc.logout();
  //   this.router.navigate(['/']);
  // }

  // ngOnDestroy(): void {
  //   this.userSub$.unsubscribe();
  // }
}
