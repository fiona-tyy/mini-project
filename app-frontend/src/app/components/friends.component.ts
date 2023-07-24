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
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrls: ['./friends.component.css'],
})
export class FriendsComponent implements OnInit {
  // activeUser!: UserDTO | null;

  userSub$!: Subscription;
  // friendSub$!: Subscription;
  friends$!: Observable<Friend[]>;

  constructor(private router: Router, private userSvc: UserService) {}

  ngOnInit(): void {
    this.friends$ = this.userSvc.getFriendsOfActiveUser();
  }

  addFriend(email: string) {
    firstValueFrom(this.userSvc.addFriend(email))
      .then(() => {
        alert('Friend added');
        this.router.navigate(['/home']);
      })
      .catch((err) => {
        alert(err.error.message);
      });
  }
}
