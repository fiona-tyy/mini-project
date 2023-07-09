import {
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { FormBuilder, FormControl } from '@angular/forms';
import { Friend, LineItem, User } from '../model';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import {
  Observable,
  Subscription,
  exhaustMap,
  firstValueFrom,
  map,
  startWith,
  tap,
} from 'rxjs';
import { Router } from '@angular/router';
import { ExpenseService } from '../services/expense.service';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-add-sharing-with',
  templateUrl: './add-sharing-with.component.html',
  styleUrls: ['./add-sharing-with.component.css'],
})
export class AddSharingWithComponent implements OnInit, OnDestroy {
  separatorKeysCodes: number[] = [ENTER, COMMA];
  friendCtrl = new FormControl('');
  friendsInput$!: Observable<Friend[]>;
  selectedFriends: Friend[] = [];
  // activeUser!: User | null;
  // friends$!: Observable<Friend[]>;
  // userSub$!: Subscription;
  friendsSub$!: Subscription;

  friends!: Friend[] | null;

  @ViewChild('friendInput') friendInput!: ElementRef<HTMLInputElement>;
  @ViewChild('fileUpload') fileInput!: ElementRef;

  constructor(
    private router: Router,
    private expenseSvc: ExpenseService,
    private userSvc: UserService
  ) {
    // this.friendsInput$ = this.friendCtrl.valueChanges.pipe(
    //   startWith(null),
    //   map((friendName: string | null) =>
    //     // friendName ? this._filter(friendName) : this.activeUser.friends!.slice()
    //     friendName ? this._filter(friendName) : this.friends.slice()
    //   )
    // );
  }

  ngOnInit(): void {
    // this.activeUser = this.userSvc.activeUser;
    this.friendsInput$ = this.userSvc.getFriendsOfActiveUser().pipe(
      tap((result) => {
        this.friends = result;
      }),
      exhaustMap((result) =>
        this.friendCtrl.valueChanges.pipe(
          startWith(null),
          map((friendName: string | null) =>
            friendName ? this._filter(friendName) : this.friends!.slice()
          )
        )
      )
    );

    // this.friendsInput$ = this.friendCtrl.valueChanges.pipe(
    //   startWith(null),
    //   map((friendName: string | null) =>
    //     // friendName ? this._filter(friendName) : this.activeUser.friends!.slice()
    //     friendName ? this._filter(friendName) : this.friends!.slice()
    //   )
    // );
  }

  remove(friend: Friend) {
    console.info('removed: ', friend);
    const index = this.selectedFriends.findIndex(
      (fr) => fr.email == friend.email
    )!;
    if (index >= 0) {
      this.selectedFriends.splice(index, 1);
    }
    console.info('selectedfriends after remove: ', this.selectedFriends);
  }
  add(event: MatChipInputEvent): void {
    // console.info('chipinput event>> ', event);
    const value = (event.value || '').trim();
    // Add friend
    if (value) {
      // const friend: Friend = this.activeUser.friends!.find(
      const friend: Friend = this.friends!.find(
        (friend) => friend.email == value
      )!;
      this.selectedFriends.push(friend);
    }
    // Clear the input value
    event.chipInput!.clear();
    this.friendCtrl.setValue(null);
  }

  selected(event: MatAutocompleteSelectedEvent) {
    // console.info('mat autocomplete event>> ', event);
    const selectedFriendEmail = event.option.value;
    // console.info('>> selected friend:  ', selectedFriendId);
    // const friend: Friend = this.activeUser.friends!.find(
    const friend: Friend = this.friends!.find(
      (fr) => fr.email == selectedFriendEmail
    )!;

    this.selectedFriends.push(friend);
    // console.info('>>selectedfriends: ', this.selectedFriends);

    this.friendInput.nativeElement.value = '';
    this.friendCtrl.setValue(null);
  }

  onFileSelected(event: any) {}

  onNext() {
    this.expenseSvc.file = this.fileInput.nativeElement.files[0];
    this.expenseSvc.selectedFriends = this.selectedFriends;

    this.router.navigate(['/record', 'new', 'expense']);
  }

  ngOnDestroy(): void {
    // this.userSub$.unsubscribe();
  }

  private _filter(friendName: string): Friend[] {
    // return this.activeUser.friends!.filter((friend) =>
    return this.friends!.filter((friend) =>
      friend.name.toLowerCase().includes(friendName.toLowerCase())
    );
  }
}
