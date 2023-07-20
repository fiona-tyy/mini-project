import {
  Component,
  ElementRef,
  Inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import { SettlementData, UserDTO } from '../model';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../services/user.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ExpenseService } from '../services/expense.service';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-add-settlement',
  templateUrl: './add-settlement.component.html',
  styleUrls: ['./add-settlement.component.css'],
})
export class AddSettlementComponent implements OnInit {
  activeUser!: UserDTO | null;
  friendId!: string;
  transactionId!: string;
  @ViewChild('fileUpload') fileInput!: ElementRef;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private userSvc: UserService,
    private expenseSvc: ExpenseService,
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
    this.activeUser = this.userSvc.activeUser;
    // this.friendId = this.activatedRoute.snapshot.params['friendId'];
    // console.info('friendId: ', this.friendId);
  }

  recordPayment() {
    console.info('paid: ', this.data.amount);
    console.info('paid: ', this.data.description);

    console.info('file?', !!this.fileInput.nativeElement.files[0]);
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

    this.expenseSvc
      .recordPayment(
        settlement,
        !!this.fileInput.nativeElement.files[0]
          ? this.fileInput.nativeElement.files[0]
          : null
      )
      .subscribe((resp) => {
        this.router.navigate(['/record', resp.transaction_id]);
      });

    // firstValueFrom(this.expenseSvc.recordPayment(settlement))
    //   .then((resp) => {
    //     this.transactionId = resp.transaction_id!;
    //     if (!!this.fileInput.nativeElement.files[0]) {
    //       firstValueFrom(
    //         this.expenseSvc.saveReceipt(
    //           this.transactionId,
    //           this.fileInput.nativeElement.files[0]
    //         )
    //       );
    //     }
    //   })
    //   .then(() => this.router.navigate(['/record', this.transactionId]));
  }

  onFileSelected(event: any) {}
}
