import { Component, OnInit } from '@angular/core';
import { UserService } from './services/user.service';
import { AngularFireMessaging } from '@angular/fire/compat/messaging';
import { NotificationService } from './services/notification.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
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
