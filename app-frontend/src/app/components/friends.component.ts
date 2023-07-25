import { Component, OnInit } from '@angular/core';
import { Friend, UserDTO } from '../model';
import {
  Observable,
  Subject,
  Subscription,
  debounceTime,
  filter,
  firstValueFrom,
  map,
  merge,
  mergeMap,
  tap,
} from 'rxjs';
import { Router } from '@angular/router';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrls: ['./friends.component.css'],
})
export class FriendsComponent implements OnInit {
  friends$!: Observable<Friend[]>;

  constructor(private router: Router, private userSvc: UserService) {}

  ngOnInit(): void {
    this.friends$ = this.userSvc.getFriendsOfActiveUser();
  }

  addFriend(email: string) {
    firstValueFrom(this.userSvc.addFriend(email))
      .then(() => {
        alert('Friend added');
        location.reload();
      })
      .catch((err) => {
        alert(err.error.message);
      });
  }
}
