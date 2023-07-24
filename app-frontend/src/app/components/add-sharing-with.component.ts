import {
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { FormControl } from '@angular/forms';
import { Friend } from '../model';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Observable, exhaustMap, map, startWith, tap } from 'rxjs';
import { Router } from '@angular/router';
import { ExpenseService } from '../services/expense.service';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-add-sharing-with',
  templateUrl: './add-sharing-with.component.html',
  styleUrls: ['./add-sharing-with.component.css'],
})
export class AddSharingWithComponent implements OnInit {
  separatorKeysCodes: number[] = [ENTER, COMMA];
  friendCtrl = new FormControl('');
  friendsInput$!: Observable<Friend[]>;
  selectedFriends: Friend[] = [];
  // activeUser!: User | null;
  // friends$!: Observable<Friend[]>;
  // userSub$!: Subscription;
  // friendsSub$!: Subscription;

  friends!: Friend[] | null;

  @ViewChild('friendInput') friendInput!: ElementRef<HTMLInputElement>;
  @ViewChild('fileUpload') fileInput!: ElementRef;

  constructor(
    private router: Router,
    private expenseSvc: ExpenseService,
    private userSvc: UserService
  ) {}

  ngOnInit(): void {
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
  }

  remove(friend: Friend) {
    const index = this.selectedFriends.findIndex(
      (fr) => fr.email == friend.email
    )!;
    if (index >= 0) {
      this.selectedFriends.splice(index, 1);
    }
  }
  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    if (value) {
      const friend: Friend = this.friends!.find(
        (friend) => friend.email == value
      )!;
      this.selectedFriends.push(friend);
    }

    event.chipInput!.clear();
    this.friendCtrl.setValue(null);
  }

  selected(event: MatAutocompleteSelectedEvent) {
    const selectedFriendEmail = event.option.value;
    const friend: Friend = this.friends!.find(
      (fr) => fr.email == selectedFriendEmail
    )!;

    this.selectedFriends.push(friend);

    this.friendInput.nativeElement.value = '';
    this.friendCtrl.setValue(null);
  }

  onFileSelected(event: any) {}

  onNext() {
    this.expenseSvc.file = this.fileInput.nativeElement.files[0];
    this.expenseSvc.selectedFriends = this.selectedFriends;

    this.router.navigate(['/record', 'new', 'expense']);
  }

  private _filter(friendName: string): Friend[] {
    return this.friends!.filter((friend) =>
      friend.name.toLowerCase().includes(friendName.toLowerCase())
    );
  }
}
