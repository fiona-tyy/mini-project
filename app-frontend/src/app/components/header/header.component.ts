import {
  Component,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { Router } from '@angular/router';
import { Observable, Subscription, exhaustMap, tap } from 'rxjs';
import { Transaction, UserDTO } from 'src/app/model';
import { ExpenseService } from 'src/app/services/expense.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit, OnDestroy {
  isAuthenticated = false;
  activeUser!: UserDTO | null;
  userSub$!: Subscription;
  recent$!: Observable<Transaction[]>;

  constructor(
    private userSvc: UserService,
    private router: Router,
    private expenseSvc: ExpenseService
  ) {}

  ngOnInit(): void {
    this.userSub$ = this.userSvc.user.subscribe((user) => {
      this.activeUser = user;
      this.isAuthenticated = !!user;
    });
    this.recent$ = this.expenseSvc.getRecentTransactions();
  }

  getTransaction(transactionId: string) {
    this.router.navigate(['/record', transactionId]);
  }

  logout() {
    this.userSvc.logout();
    this.router.navigate(['/']);
  }

  ngOnDestroy(): void {
    this.userSub$.unsubscribe();
  }
}
