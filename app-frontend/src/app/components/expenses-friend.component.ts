import { Component, OnDestroy, OnInit } from '@angular/core';
import { Friend, Transaction, UserDTO } from '../model';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subscription, exhaustMap, filter, map, tap } from 'rxjs';
import { ExpenseService } from '../services/expense.service';
import { UserService } from '../services/user.service';
import { Title } from '@angular/platform-browser';
import { MatDialog } from '@angular/material/dialog';
import { AddSettlementComponent } from './add-settlement.component';

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

  constructor(
    private activatedRoute: ActivatedRoute,
    private userSvc: UserService,
    private expenseSvc: ExpenseService,
    private router: Router,
    private title: Title,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    const friendEmail = this.activatedRoute.snapshot.queryParams['friendEmail'];
    this.userSub$ = this.userSvc.user.subscribe(
      (user) => (this.activeUser = user!)
    );
    this.records$ = this.expenseSvc.getOutstandingWithFriends().pipe(
      map((frnds) => {
        return frnds.find((fr) => fr.email == friendEmail);
      }),
      tap((frd) => {
        (this.friend = frd!),
          this.title.setTitle('Friend: ' + frd!.name.toUpperCase());
      }),
      exhaustMap((frd) =>
        this.expenseSvc.getTransactionsWithFriend(frd!.email)
      ),
      tap((results) => console.info('what ', results))
    );
  }

  getTransaction(transactionId: string) {
    console.info('transaction id: ', transactionId);
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

  ngOnDestroy(): void {
    this.userSub$.unsubscribe();
  }
}
