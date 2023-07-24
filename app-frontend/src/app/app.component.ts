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
  // token!: any;

  constructor(
    private userSvc: UserService,
    private afMessaging: AngularFireMessaging,
    private notificationSvc: NotificationService
  ) {}

  ngOnInit(): void {
    this.userSvc.autoLogin();

    this.afMessaging.messages.subscribe((msg) => {
      console.log('new message:', msg);
      this.notificationSvc.setNotification({
        body: msg.notification!.body!,
        title: msg.notification!.title!,
        isVisible: true,
      });
    });
  }
}
