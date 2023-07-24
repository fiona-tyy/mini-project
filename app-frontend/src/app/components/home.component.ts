import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ExpenseService } from '../services/expense.service';
import { UserService } from '../services/user.service';
import { Friend, User, UserDTO } from '../model';
import {
  Observable,
  Subscription,
  exhaustMap,
  firstValueFrom,
  tap,
} from 'rxjs';
import { SwPush } from '@angular/service-worker';
import { AngularFireMessaging } from '@angular/fire/compat/messaging';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit, OnDestroy {
  activeUser!: UserDTO | null;
  amountOwed = 0;
  amountBorrowed = 0;

  userSub$!: Subscription;
  friendSub$!: Subscription;
  friendsOutstand$!: Observable<Friend[]>;
  token$!: Subscription;
  // outstanding$!: Subscription;

  constructor(
    private router: Router,
    private expenseSvc: ExpenseService,
    private userSvc: UserService,
    private afMessaging: AngularFireMessaging,
    private notificationSvc: NotificationService
  ) {}

  ngOnInit(): void {
    this.userSub$ = this.userSvc.user.subscribe(
      (user) => (this.activeUser = user!)
    );

    if (!!this.activeUser) {
      console.log('from homepage- activeuser: ', this.activeUser);

      this.friendsOutstand$ = this.expenseSvc.getOutstandingWithFriends();
      this.friendSub$ = this.friendsOutstand$
        .pipe(
          tap((result) => {
            result.forEach((f) => {
              if (f.amount_outstanding > 0) {
                this.amountOwed += f.amount_outstanding;
              } else if (f.amount_outstanding < 0) {
                this.amountBorrowed += -f.amount_outstanding;
              }
            });
          })
        )
        .subscribe();
    }
    this.token$ = this.afMessaging.requestToken
      .pipe(
        tap((token) => {
          console.log('token: ', token);
        }),
        exhaustMap((token) => this.notificationSvc.subscribeNotification(token))
      )
      .subscribe();
  }

  onNew() {
    this.router.navigate(['/record', 'new', 'sharing']);
  }

  getFriendExpenses(friendEmail: string, friendName: string) {
    const queryParams: Params = {
      friendEmail: friendEmail,
      friendName: friendName,
    };
    this.router.navigate(['/records'], {
      queryParams,
    });
  }

  ngOnDestroy(): void {
    this.userSub$.unsubscribe();
    this.friendSub$.unsubscribe();
    this.token$.unsubscribe();
  }
}
