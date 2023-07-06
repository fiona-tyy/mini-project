import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ExpenseService } from '../services/expense.service';
import { UserService } from '../services/user.service';
import { Friend, User, UserDTO } from '../model';
import { Observable, Subscription, firstValueFrom, tap } from 'rxjs';

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
  outstanding$!: Subscription;
  // friends!: Friend[];

  constructor(
    private router: Router,
    private expenseSvc: ExpenseService,
    private userSvc: UserService
  ) {}

  ngOnInit(): void {
    this.userSub$ = this.userSvc.user.subscribe(
      (user) => (this.activeUser = user!)
    );

    // this.activeUser = this.userSvc.activeUser;
    if (!!this.activeUser) {
      console.info('from homepage- activeuser: ', this.activeUser);

      //gets outstanding regardless of whether friends
      this.friendsOutstand$ = this.expenseSvc.getOutstandingWithFriends();
      this.friendSub$ = this.friendsOutstand$
        .pipe(
          tap((result) => {
            // this.userSvc.friendsOutstanding = result;
            // console.info(this.userSvc.friends);
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

      //friend-1
      // firstValueFrom(
      //   this.userSvc.getFriendsOfActiveUser(this.activeUser!.email)
      // ).then((result) => (this.userSvc.friends = result));
    }
  }

  onNew() {
    this.router.navigate(['/record', 'new', 'sharing']);
  }

  getFriendExpenses(friendEmail: string) {
    const queryParams: Params = { friendEmail: friendEmail };
    this.router.navigate(['/records'], {
      queryParams,
    });
  }

  ngOnDestroy(): void {
    this.userSub$.unsubscribe();
    //unsubscribe from friendSub$
    this.friendSub$.unsubscribe();
  }
}
