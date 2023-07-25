import { Component, OnDestroy, OnInit } from '@angular/core';
import { ExpenseService } from '../services/expense.service';
import {
  ExpenseProcessed,
  SettlementData,
  Transaction,
  User,
  UserDTO,
} from '../model';
import {
  Observable,
  Subscription,
  exhaustMap,
  firstValueFrom,
  map,
  tap,
} from 'rxjs';
import { UserService } from '../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import * as htmlToImage from 'html-to-image';
import { PostTransactionService } from '../services/post-transaction.service';

@Component({
  selector: 'app-expense-summary',
  templateUrl: './expense-summary.component.html',
  styleUrls: ['./expense-summary.component.css'],
})
export class ExpenseSummaryComponent implements OnInit, OnDestroy {
  activeUser!: UserDTO | null;
  userSub$!: Subscription;
  summary$!: Observable<Transaction[]>;
  url!: string;
  shareSupported?: boolean;
  transactionId!: string;

  constructor(
    private expenseSvc: ExpenseService,
    private userSvc: UserService,
    private activatedRoute: ActivatedRoute,
    private postTransSvc: PostTransactionService
  ) {}

  ngOnInit(): void {
    this.userSub$ = this.userSvc.user.subscribe(
      (user) => (this.activeUser = user!)
    );
    this.summary$ = this.activatedRoute.params.pipe(
      map((params) => params['transactionId']),
      tap((param) => {
        this.url = 'https://fiona-tyy.com/images/' + param;
        this.transactionId = param;
      }),
      exhaustMap((param) => this.expenseSvc.getTransactionById(param))
    );
    this.shareSupported = !!navigator.canShare;
  }

  delete(transactionId: string) {
    this.postTransSvc.tryDeleteRecord(transactionId);
  }

  share() {
    let node = document.getElementById('page-summary');
    htmlToImage
      .toPng(node!)
      .then((dataUrl) => {
        console.info('dataUrl', dataUrl);
        return dataUrlToFile(dataUrl, 'screenshot.png');
      })
      .then((file) => {
        navigator.share({
          text: "[NinjaSplit] Here's how much you owe:",
          files: [file],
          url:
            'https://ninjasplit.fiona-tyy.com/#/record/' + this.transactionId,
        });
      });
  }

  ngOnDestroy(): void {
    this.userSub$.unsubscribe();
  }
}

export async function dataUrlToFile(
  dataUrl: string,
  fileName: string
): Promise<File> {
  const res: Response = await fetch(dataUrl);
  const blob: Blob = await res.blob();
  return new File([blob], fileName, { type: blob.type });
}
