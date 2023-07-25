import {
  Component,
  ElementRef,
  Inject,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { SettlementData, UserDTO } from '../model';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../services/user.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ExpenseService } from '../services/expense.service';
import { Subscription, firstValueFrom } from 'rxjs';
import { PostTransactionService } from '../services/post-transaction.service';

@Component({
  selector: 'app-add-settlement',
  templateUrl: './add-settlement.component.html',
  styleUrls: ['./add-settlement.component.css'],
})
export class AddSettlementComponent implements OnInit, OnDestroy {
  activeUser!: UserDTO | null;
  friendId!: string;
  transactionId!: string;
  userSub$!: Subscription;
  @ViewChild('fileUpload') fileInput!: ElementRef;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private userSvc: UserService,
    private expenseSvc: ExpenseService,
    private postTransSvc: PostTransactionService,
    private dialogRef: MatDialogRef<AddSettlementComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      whoPaid: { name: string; email: string };
      whoReceived: { name: string; email: string };
      amount: number;
      description: string;
    }
  ) {}

  ngOnInit(): void {
    this.userSub$ = this.userSvc.user.subscribe(
      (user) => (this.activeUser = user!)
    );
  }

  recordPayment() {
    let settlement: SettlementData = {
      transaction_type: 'settlement',
      description: this.data.description,
      date: new Date().getTime(),
      recorded_by: {
        name: this.activeUser!.name,
        email: this.activeUser!.email,
      },
      recorded_date: new Date().getTime(),
      repayment_amount: this.data.amount,
      who_paid: this.data.whoPaid,
      who_received: this.data.whoReceived,
      attachment: !!this.fileInput.nativeElement.files[0] ? 'Y' : 'N',
    };
    this.postTransSvc.tryPostTransaction(
      'settlement',
      JSON.stringify(settlement),
      !!this.fileInput.nativeElement.files[0]
        ? this.fileInput.nativeElement.files[0]
        : null
    );
  }

  onFileSelected(event: any) {}

  ngOnDestroy(): void {
    this.userSub$.unsubscribe();
  }
}
