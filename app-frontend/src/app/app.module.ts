import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppComponent } from './app.component';
import { MaterialModule } from './material.module';
import { LoginComponent } from './components/login.component';
import { HomeComponent } from './components/home.component';
import { RecordsComponent } from './components/records.component';
import { AddExpenseComponent } from './components/add-expense.component';
import { AddSharingWithComponent } from './components/add-sharing-with.component';
import { ExpenseSummaryComponent } from './components/expense-summary.component';
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner.component';
import { ExpensesFriendComponent } from './components/expenses-friend.component';
import { FriendsComponent } from './components/friends.component';
import { ExpenseService } from './services/expense.service';
import { UserService } from './services/user.service';

const appRoutes: Routes = [
  { path: '', component: LoginComponent, title: 'Login' },
  { path: 'home', component: HomeComponent, title: 'Home' },
  { path: 'records/:friendId', component: RecordsComponent },
  // { path: 'records', component: FriendComponent },
  { path: 'records', component: ExpensesFriendComponent },
  {
    path: 'record/new/sharing',
    component: AddSharingWithComponent,
    title: 'New Expense',
  },
  {
    path: 'record/new/expense',
    component: AddExpenseComponent,
    title: 'New Expense',
  },
  {
    path: 'record/:transactionId',
    component: ExpenseSummaryComponent,
    title: 'Expense Summary',
  },
  { path: 'friends', component: FriendsComponent, title: 'Friends' },
  { path: '**', redirectTo: '/', pathMatch: 'full' },
];

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    RecordsComponent,
    AddExpenseComponent,
    AddSharingWithComponent,
    ExpenseSummaryComponent,
    ExpensesFriendComponent,
    LoadingSpinnerComponent,
    FriendsComponent,
  ],
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterModule.forRoot(appRoutes, { useHash: true }),
    BrowserAnimationsModule,
    MaterialModule,
  ],
  providers: [ExpenseService, UserService],
  bootstrap: [AppComponent],
})
export class AppModule {}
