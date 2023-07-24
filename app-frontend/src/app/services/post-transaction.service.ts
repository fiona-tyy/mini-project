import { Injectable } from '@angular/core';
import { NetworkService } from './network.service';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import Dexie from 'dexie';
import { ExpenseService } from './expense.service';

@Injectable({
  providedIn: 'root',
})
export class PostTransactionService {
  private db!: any;

  constructor(
    private networkSvc: NetworkService,
    private expenseSvc: ExpenseService,
    private router: Router
  ) {
    this.createIndexedDb();
    this.registerToNetworkService(networkSvc);
  }

  tryPostTransaction(type: string, transaction: string, file: File | null) {
    if (this.networkSvc.isOnline) {
      if (type == 'expense') {
        this.expenseSvc.saveExpense(transaction, file).subscribe({
          next: (data) => {
            this.expenseSvc.file = null;
            this.router.navigate(['/record', data.transaction_id]);
          },
        });
      }
      if (type == 'settlement') {
        this.expenseSvc.recordPayment(transaction, file).subscribe({
          next: (resp) => {
            this.router.navigate(['/record', resp.transaction_id]);
          },
        });
      }
    } else {
      this.saveItemToIndexedDb(type, transaction, file).then(() => {
        alert(
          'Network offline - transaction will be posted when network returns online'
        );
        this.router.navigate(['/home']);
      });
    }
  }

  tryDeleteRecord(transactionId: string) {
    if (this.networkSvc.isOnline) {
      this.expenseSvc.deleteTransactionById(transactionId).subscribe({
        next: () => {
          this.router.navigate(['/home']);
        },
      });
    } else {
      this.saveItemToIndexedDb('delete', transactionId, null).then(() => {
        this.router.navigate(['/home']);
      });
    }
  }

  private createIndexedDb() {
    this.db = new Dexie('OfflineDB');
    this.db.version(1).stores({
      queued: '++id',
    });
    this.db.open();
  }

  private registerToNetworkService(networkSvc: NetworkService) {
    networkSvc.connectionChanged.subscribe((online) => {
      if (online) {
        console.log('went online');
        this.sendItemsFromIndexedDb();
      } else {
        console.log('went offline');
      }
    });
  }

  // add to indexedDB when in offline mode
  private saveItemToIndexedDb(
    type: string,
    transaction: string,
    file: File | null
  ) {
    return this.db.queued.add({
      type: type,
      transaction: transaction,
      file: file,
    });
  }

  // post items from indexedDB when in online mode
  private async sendItemsFromIndexedDb() {
    console.log('sending items from db');
    const queuedItems: queuedTask[] = await this.db.queued.toArray();
    //iterate through queuedItems and post
    for (let item of queuedItems) {
      if (item.type == 'expense') {
        this.expenseSvc
          .saveExpense(item.transaction, item.file ? item.file : null)
          .subscribe();
      }
      if (item.type == 'settlement') {
        this.expenseSvc
          .recordPayment(item.transaction, !!item.file ? item.file : null)
          .subscribe();
      }
      if (item.type == 'delete') {
        this.expenseSvc.deleteTransactionById(item.transaction).subscribe();
      }
    }
    this.db.queued.clear();
  }
}

export interface queuedTask {
  id: number;
  type: string;
  transaction: string;
  file?: File;
}
