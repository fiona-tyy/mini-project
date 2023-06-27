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
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable()
export class ExpenseService {
  selectedFriends: Friend[] = [];
  expense!: ExpenseData;
  file!: File | null;

  constructor(private http: HttpClient) {}

  getLineItems(file: File) {
    // console.info('file selected: ', file);
    const data = new FormData();
    data.set('file', file);
    return this.http.post<ReceiptResponseData>('/api/transaction/new', data);
  }

  saveExpense(trans: ExpenseData) {
    // console.info('saving transaction: ', trans);
    return this.http.post<ExpenseProcessed>('/api/transaction/save', trans);
  }

  saveReceipt(transId: string, file: File) {
    const data = new FormData();
    data.set('transactionId', transId);
    data.set('file', file);
    return this.http.post('/api/transaction/save-receipt', data);
  }

  getOutstandingWithFriends(userId: string) {
    return this.http.get<Friend[]>('/api/transaction/outstanding/' + userId);
  }

  getTransactionsWithFriend(userId: string, friendId: string) {
    const params = new HttpParams().set('friendId', friendId);
    return this.http.get<Transaction[]>('/api/transaction/records/' + userId, {
      params,
    });
  }

  getTransactionById(transactionId: string) {
    return this.http.get<Transaction[]>(
      'api/transaction/record/' + transactionId
    );
  }

  deleteTransactionById(transactionId: string) {
    return this.http.delete<any>('api/transaction/record/' + transactionId);
  }
}
