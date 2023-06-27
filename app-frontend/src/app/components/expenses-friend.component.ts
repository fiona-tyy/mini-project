import { Component, OnInit } from '@angular/core';
import { Friend, Transaction, UserDTO } from '../model';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { ExpenseService } from '../services/expense.service';
import { UserService } from '../services/user.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-expenses-friend',
  templateUrl: './expenses-friend.component.html',
  styleUrls: ['./expenses-friend.component.css'],
})
export class ExpensesFriendComponent implements OnInit {
  friend!: Friend;
  activeUser!: UserDTO | null;
  friends!: Friend[];
  records$!: Observable<Transaction[]>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private userSvc: UserService,
    private expenseSvc: ExpenseService,
    private router: Router,
    private title: Title
  ) {}

  ngOnInit(): void {
    this.activeUser = this.userSvc.activeUser;
    this.friends = this.userSvc.friendsOutstanding;
    const friendId = this.activatedRoute.snapshot.queryParams['friendId'];
    // this.friend = this.activeUser.friends!.find((fr) => fr.id == friendId)!;
    this.friend = this.friends.find((fr) => fr.id == friendId)!;
    console.info('>expenses w friend: ', this.friend);
    this.records$ = this.expenseSvc.getTransactionsWithFriend(
      this.activeUser!.id,
      this.friend.id
    );
    this.title.setTitle('Friend: ' + this.friend.name.toUpperCase());
    // .pipe(
    //   tap((result) => console.info('friend transactions received:', result))
    // )
    // .subscribe();
  }

  getTransaction(transactionId: string) {
    console.info('transaction id: ', transactionId);
    this.router.navigate(['/record', transactionId]);
  }

  addSettlement() {}
}
