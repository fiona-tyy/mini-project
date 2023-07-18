import {
  AfterContentInit,
  Component,
  ElementRef,
  OnInit,
  QueryList,
  ViewChild,
} from '@angular/core';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import {
  Friend,
  LineItem,
  ReceiptResponseData,
  ShareSplit,
  ExpenseData,
  User,
  UserDTO,
} from '../model';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import {
  Observable,
  Subscription,
  firstValueFrom,
  map,
  startWith,
  tap,
} from 'rxjs';
import { ExpenseService } from '../services/expense.service';
import { Router } from '@angular/router';
import { UserService } from '../services/user.service';
import { MatCheckbox } from '@angular/material/checkbox';

@Component({
  selector: 'app-add-expense',
  templateUrl: './add-expense.component.html',
  styleUrls: ['./add-expense.component.css'],
})
export class AddExpenseComponent implements OnInit {
  form!: FormGroup;
  itemArr!: FormArray;

  selectedFriends!: Friend[];
  // userSub$!: Subscription;
  activeUser!: UserDTO | null;
  transactionId!: string;
  totalCost!: number;
  isProcessing = true;
  selectAll = false;

  constructor(
    private fb: FormBuilder,
    private expenseSvc: ExpenseService,
    private router: Router,
    private userSvc: UserService
  ) {}

  ngOnInit(): void {
    this.selectedFriends = this.expenseSvc.selectedFriends;
    this.activeUser = this.userSvc.activeUser;
    // this.userSub$ = this.userSvc.user.subscribe(
    //   (user) => (this.activeUser = user!)
    // );
    this.form = this.createForm(null);
    if (!this.expenseSvc.file) {
      this.isProcessing = false;
    }
    if (!!this.expenseSvc.file) {
      firstValueFrom(this.expenseSvc.getLineItems(this.expenseSvc.file)).then(
        (result) => {
          console.info(result);
          this.isProcessing = false;
          this.form = this.createForm(result);
          this.calculateTotalCost();
        }
      );
    }
  }

  addLineItem() {
    this.itemArr.push(this.createItem(null));
  }

  deleteItem(index: number) {
    this.itemArr.removeAt(index);
    this.calculateTotalCost();
  }

  invalidForm() {
    return this.form.invalid || this.itemArr.length <= 0;
  }

  onCheckboxChange(event: any, i: number) {
    // console.info('what is this ', event);
    const s = this.itemArr.at(i).get('split_with') as FormArray;

    if (event.checked) {
      s.push(this.fb.control<string>(event.source.value));
    } else {
      let x = 0;
      s.controls.forEach((ctrl) => {
        if (ctrl.value == event.source.value) {
          s.removeAt(x);
          return;
        }
        x++;
      });
    }
    // console.info('who is sharing', s);
  }

  calculateTotalCost() {
    let totalCost = 0;
    for (let control of this.itemArr.controls) {
      totalCost += control.value['amount'];
    }
    totalCost += this.form.value['gst'] + this.form.value['service_charge'];
    this.totalCost = totalCost;
  }

  processForm() {
    console.info('>>form value:', this.form.value);
    const expense = this.form.value as ExpenseData;
    expense.date = new Date(this.form.value['date']).getTime();
    let user: User = {
      email: this.activeUser!.email,
      name: this.activeUser!.name,
    };
    expense.recorded_by = user;
    expense.recorded_date = new Date().getTime();
    if (!!this.expenseSvc.file) {
      expense.attachment = 'Y';
    } else {
      expense.attachment = 'N';
    }

    // firstValueFrom(this.expenseSvc.saveExpense(expense)).then((resp) => {
    //   this.transactionId = resp.transaction_id;
    //   this.router.navigate(['/record', this.transactionId]);
    // });
    firstValueFrom(this.expenseSvc.saveExpense(expense))
      .then((resp) => {
        this.transactionId = resp.transaction_id;
        // console.info('file? ', !!this.expenseSvc.file);
        if (!!this.expenseSvc.file) {
          firstValueFrom(
            this.expenseSvc.saveReceipt(
              this.transactionId,
              this.expenseSvc.file
            )
          );
        }
      })
      .then(() => {
        this.expenseSvc.file = null;
        this.router.navigate(['/record', this.transactionId]);
      });
  }

  private createForm(r: ReceiptResponseData | null): FormGroup {
    //TODO create lineitem[] with empty control
    this.itemArr = this.createItemsList(!!r ? r.line_items : []);
    return this.fb.group({
      description: this.fb.control<string>(!!r ? r.description : '', [
        Validators.required,
      ]),
      date: this.fb.control<Date>(
        // !!r ? new Date(Date.parse(r.date)) : new Date()
        !!r ? new Date(r.date) : new Date()
      ),
      who_paid: this.fb.control<string>('', [Validators.required]),
      service_charge: this.fb.control<number>(!!r ? r.service_charge : 0),
      gst: this.fb.control<number>(!!r ? r.gst : 0),
      line_items: this.itemArr,
    });
  }

  private createItemsList(lineItems: LineItem[]): FormArray {
    return this.fb.array(
      lineItems.map((it) => this.createItem(it)),
      [Validators.minLength(1)]
    );
  }

  private createItem(item: LineItem | null): FormGroup {
    return this.fb.group({
      item: this.fb.control<string>(!!item ? item.item : '', [
        Validators.required,
      ]),
      amount: this.fb.control<number>(!!item ? item.amount : 0, [
        Validators.required,
        Validators.min(0.01),
      ]),
      split_with: this.fb.array(
        [],
        [Validators.required, Validators.minLength(1)]
      ),
    });
  }
}
