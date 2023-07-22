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
import { exhaustMap, take } from 'rxjs';

@Injectable()
export class ExpenseService {
  selectedFriends: Friend[] = [];
  expense!: ExpenseData;
  file!: File | null;

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
  // saveExpense(trans: ExpenseData) {
  //   // console.info('saving transaction: ', trans);
  //   return this.http.post<ExpenseProcessed>('/api/transaction/save', trans);
  // }

  // saveReceipt(transId: string, file: File) {
  //   const data = new FormData();
  //   data.set('transactionId', transId);
  //   data.set('file', file);
  //   return this.http.post('/api/transaction/save-receipt', data);
  // }
  recordPayment(settlement: string, file: File | null) {
    const data = new FormData();
    data.set('settlement', settlement);
    if (!!file) {
      data.set('file', file);
    }
    return this.http.post<SettlementData>('/api/transaction/settlement', data);
  }

  // getOutstandingWithFriends(userId: string) {
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

  // getTransactionsWithFriend(userId: string, friendId: string) {
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

  // payNow(amount: number) {
  //   const form = new HttpParams().set('amount', amount * 100);

  //   const headers = new HttpHeaders().set(
  //     'Content-Type',
  //     'application/x-www-form-urlencoded'
  //   );
  //   return this.http.post('/api/payment/payment-intent', form.toString(), {
  //     headers,
  //   });
  // }
}
