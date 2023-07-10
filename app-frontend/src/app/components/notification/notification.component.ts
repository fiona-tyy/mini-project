import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { NotificationModel } from 'src/app/model';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css'],
})
export class NotificationComponent implements OnInit, OnDestroy {
  showPanel!: boolean;
  notification!: NotificationModel;
  notificationSub!: Subscription;
  notificationTimeout: any;

  constructor(private notificationSvc: NotificationService) {}

  ngOnInit(): void {
    this.notificationSub = this.notificationSvc
      .getNotification()
      .subscribe((n) => {
        this.notification = n!;
        this.showPanel = n !== null;

        this.notificationTimeout = setTimeout(() => {
          this.showPanel = false;
        }, 10000);
      });
  }
  dismissNotification() {
    this.showPanel = false;
  }

  ngOnDestroy(): void {
    clearTimeout(this.notificationTimeout);
    this.notificationSub.unsubscribe();
  }
}
