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
import { NetworkService } from 'src/app/services/network.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit, OnDestroy {
  isAuthenticated = false;
  isOnline = true;
  activeUser!: UserDTO | null;
  userSub$!: Subscription;
  networkSub$!: Subscription;
  recent$!: Observable<Transaction[]>;

  constructor(
    private userSvc: UserService,
    private router: Router,
    private expenseSvc: ExpenseService,
    private networkSvc: NetworkService
  ) {}

  ngOnInit(): void {
    this.userSub$ = this.userSvc.user.subscribe((user) => {
      this.activeUser = user;
      this.isAuthenticated = !!user;
      if (this.isAuthenticated) {
        this.recent$ = this.expenseSvc.getRecentTransactions();
      }
    });

    this.networkSvc.connectionChanged.subscribe((online) => {
      console.log('status', online);
      this.isOnline = online;
    });
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
    this.networkSub$.unsubscribe();
  }
}
