import { Component, OnDestroy, OnInit } from '@angular/core';
import { Friend, Transaction, UserDTO } from '../model';
import { ActivatedRoute, Router } from '@angular/router';
import {
  Observable,
  Subscription,
  exhaustMap,
  filter,
  firstValueFrom,
  map,
  tap,
} from 'rxjs';
import { ExpenseService } from '../services/expense.service';
import { UserService } from '../services/user.service';
import { Title } from '@angular/platform-browser';
import { MatDialog } from '@angular/material/dialog';
import { AddSettlementComponent } from './add-settlement.component';
import { EmailService } from '../services/email.service';

@Component({
  selector: 'app-expenses-friend',
  templateUrl: './expenses-friend.component.html',
  styleUrls: ['./expenses-friend.component.css'],
})
export class ExpensesFriendComponent implements OnInit, OnDestroy {
  friend!: Friend;
  activeUser!: UserDTO | null;
  friends!: Friend[];
  records$!: Observable<Transaction[]>;
  userSub$!: Subscription;
  friendName!: string;
  friendEmail!: string;

  constructor(
    private activatedRoute: ActivatedRoute,
    private userSvc: UserService,
    private expenseSvc: ExpenseService,
    private emailSvc: EmailService,
    private router: Router,
    private title: Title,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.friendEmail = this.activatedRoute.snapshot.queryParams['friendEmail'];
    this.friendName = this.activatedRoute.snapshot.queryParams['friendName'];
    this.title.setTitle('Friend: ' + this.friendName.toUpperCase());
    this.userSub$ = this.userSvc.user.subscribe(
      (user) => (this.activeUser = user!)
    );

    // TO FIX - if there are no transactions with friend, need to also be able to display
    this.records$ = this.expenseSvc.getOutstandingWithFriends().pipe(
      map((frnds) => {
        return frnds.find((fr) => fr.email == this.friendEmail);
      }),
      filter((frd) => !!frd),
      tap((frd) => {
        this.friend = frd!;
      }),
      exhaustMap((frd) => this.expenseSvc.getTransactionsWithFriend(frd!.email))
    );
  }

  getTransaction(transactionId: string) {
    console.log('transaction id: ', transactionId);
    this.router.navigate(['/record', transactionId]);
  }

  addSettlement() {
    this.router.navigate(['/record/new/settlement', this.friend.email]);
  }

  openDialog() {
    let dialogRef;
    if (this.friend.amount_outstanding < 0) {
      dialogRef = this.dialog.open(AddSettlementComponent, {
        data: {
          whoPaid: {
            name: this.activeUser!.name,
            email: this.activeUser!.email,
          },
          whoReceived: { name: this.friend.name, email: this.friend.email },
          amount: -this.friend.amount_outstanding,
          description: '',
        },
      });
    } else {
      dialogRef = this.dialog.open(AddSettlementComponent, {
        data: {
          whoPaid: { name: this.friend.name, email: this.friend.email },
          whoReceived: {
            name: this.activeUser!.name,
            email: this.activeUser!.email,
          },
          amount: this.friend.amount_outstanding,
          description: '',
        },
      });
    }
  }
  sendReminderEmail() {
    firstValueFrom(
      this.emailSvc.sendReminderEmail(this.friend, this.activeUser!)
    ).then(() =>
      alert(
        'Email reminder sent to ' +
          this.friend.name.slice(0, 1).toUpperCase() +
          this.friend.name.slice(1)
      )
    );
  }

  ngOnDestroy(): void {
    this.userSub$.unsubscribe();
  }
}
