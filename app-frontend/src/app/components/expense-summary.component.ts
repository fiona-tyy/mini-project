import { Component, OnInit } from '@angular/core';
import { ExpenseService } from '../services/expense.service';
import { ExpenseProcessed, SettlementData, Transaction, User } from '../model';
import { Observable, Subscription, firstValueFrom } from 'rxjs';
import { UserService } from '../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-expense-summary',
  templateUrl: './expense-summary.component.html',
  styleUrls: ['./expense-summary.component.css'],
})
export class ExpenseSummaryComponent implements OnInit {
  activeUser!: User | null;
  userSub$!: Subscription;
  summary$!: Observable<Transaction[]>;
  // payer!: User;
  url!: string;

  constructor(
    private expenseSvc: ExpenseService,
    private userSvc: UserService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.activeUser = this.userSvc.activeUser;
    // this.transaction = this.expenseSvc.transaction;
    const transactionId = this.activatedRoute.snapshot.params['transactionId'];
    this.url = 'https://fiona-tyy.com/images/' + transactionId;
    // post through service
    this.summary$ = this.expenseSvc.getTransactionById(transactionId);
  }

  delete(transactionId: string) {
    firstValueFrom(this.expenseSvc.deleteTransactionById(transactionId))
      .then((result) => this.router.navigate(['/home']))
      .catch((err) => alert(err.message));
  }
}
