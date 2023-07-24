import { Injectable } from '@angular/core';
import {
  Friend,
  ReceiptResponseData,
  ExpenseData,
  User,
  ExpenseProcessed,
  Transaction,
  SettlementData,
} from '../model';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { UserService } from './user.service';
import { Subject, exhaustMap, take, tap } from 'rxjs';

@Injectable()
export class ExpenseService {
  selectedFriends: Friend[] = [];
  expense!: ExpenseData;
  file!: File | null;
  private _refreshRequired = new Subject<void>();

  get refreshRequired() {
    return this._refreshRequired;
  }

  constructor(private http: HttpClient, private userSvc: UserService) {}

  getLineItems(file: File) {
    // console.info('file selected: ', file);
    const data = new FormData();
    data.set('file', file);
    return this.http.post<ReceiptResponseData>('/api/transaction/new', data);
  }

  saveExpense(expense: string, file: File | null) {
    const data = new FormData();
    data.set('expense', expense);
    if (!!file) {
      data.set('file', file);
    }
    return this.http.post<ExpenseProcessed>('api/transaction/expense', data);
  }

  recordPayment(settlement: string, file: File | null) {
    const data = new FormData();
    data.set('settlement', settlement);
    if (!!file) {
      data.set('file', file);
    }
    return this.http.post<SettlementData>('/api/transaction/settlement', data);
  }

  getOutstandingWithFriends() {
    return this.userSvc.user.pipe(
      take(1),
      exhaustMap((user) => {
        return this.http.get<Friend[]>(
          '/api/transaction/outstanding/' + user!.email
        );
      })
    );
  }

  getTransactionsWithFriend(friendId: string) {
    const params = new HttpParams().set('friendId', friendId);
    return this.userSvc.user.pipe(
      take(1),
      exhaustMap((user) => {
        return this.http.get<Transaction[]>(
          '/api/transaction/records/' + user!.email,
          { params }
        );
      })
    );
  }

  getTransactionById(transactionId: string) {
    return this.http.get<Transaction[]>(
      'api/transaction/record/' + transactionId
    );
  }

  deleteTransactionById(transactionId: string) {
    return this.http.delete<any>('api/transaction/record/' + transactionId);
  }
  getRecentTransactions() {
    return this.userSvc.user.pipe(
      take(1),
      exhaustMap((user) => {
        return this.http.get<Transaction[]>(
          '/api/transaction/recent/' + user!.email
        );
      })
    );
  }
}
