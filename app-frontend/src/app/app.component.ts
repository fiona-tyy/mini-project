import { Component, OnDestroy, OnInit } from '@angular/core';
import { Transaction, User, UserDTO } from './model';
import { UserService } from './services/user.service';
import { Observable, Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { ExpenseService } from './services/expense.service';

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

  constructor(private userSvc: UserService) // private router: Router,
  // private expenseSvc: ExpenseService
  {}

  ngOnInit(): void {
    this.userSvc.autoLogin();
    // this.userSub$ = this.userSvc.user.subscribe((user) => {
    //   this.activeUser = user;
    //   this.isAuthenticated = !!user;
    // });
    // this.recent$ = this.expenseSvc.getRecentTransactions();
  }

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
