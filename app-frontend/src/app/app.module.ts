import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ServiceWorkerModule } from '@angular/service-worker';
import {
  GoogleLoginProvider,
  GoogleSigninButtonModule,
  SocialAuthServiceConfig,
  SocialLoginModule,
} from '@abacritt/angularx-social-login';
import { AngularFireModule } from '@angular/fire/compat';
import { AngularFireMessagingModule } from '@angular/fire/compat/messaging';

import { AppComponent } from './app.component';
import { MaterialModule } from './material.module';
import { LoginComponent } from './components/login.component';
import { HomeComponent } from './components/home.component';
import { AddExpenseComponent } from './components/add-expense.component';
import { AddSharingWithComponent } from './components/add-sharing-with.component';
import { AddSettlementComponent } from './components/add-settlement.component';
import { ExpenseSummaryComponent } from './components/expense-summary.component';
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner.component';
import { ExpensesFriendComponent } from './components/expenses-friend.component';
import { FriendsComponent } from './components/friends.component';
import { ExpenseService } from './services/expense.service';
import { UserService } from './services/user.service';
import { AuthGuard } from './components/auth.guard';
import { HeaderComponent } from './components/header/header.component';
import { environment } from 'src/environment/environment';
import { NotificationService } from './services/notification.service';
import { NotificationComponent } from './components/notification/notification.component';
import { RequestCodeComponent } from './components/user/request-code.component';
import { ResetPasswordComponent } from './components/user/reset-password.component';
import { EmailService } from './services/email.service';

const appRoutes: Routes = [
  { path: '', component: LoginComponent, title: 'Login' },
  {
    path: 'home',
    component: HomeComponent,
    title: 'Home',
    canActivate: [AuthGuard()],
  },
  { path: 'records', component: ExpensesFriendComponent },
  {
    path: 'record/new/sharing',
    component: AddSharingWithComponent,
    title: 'New Expense',
    canActivate: [AuthGuard()],
  },
  {
    path: 'record/new/expense',
    component: AddExpenseComponent,
    title: 'New Expense',
    canActivate: [AuthGuard()],
  },
  {
    path: 'record/:transactionId',
    component: ExpenseSummaryComponent,
    title: 'Summary',
    canActivate: [AuthGuard()],
  },
  {
    path: 'record/new/settlement/:friendId',
    component: AddSettlementComponent,
    title: 'Record Repayment',
    canActivate: [AuthGuard()],
  },
  {
    path: 'friends',
    component: FriendsComponent,
    title: 'Friends',
    canActivate: [AuthGuard()],
  },
  {
    path: 'request-code',
    component: RequestCodeComponent,
    title: 'Reset Password',
  },
  {
    path: 'reset-password',
    component: ResetPasswordComponent,
    title: 'Reset Password',
  },
  { path: '**', redirectTo: '/', pathMatch: 'full' },
];

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    LoginComponent,
    HomeComponent,
    AddExpenseComponent,
    AddSharingWithComponent,
    AddSettlementComponent,
    ExpenseSummaryComponent,
    ExpensesFriendComponent,
    LoadingSpinnerComponent,
    FriendsComponent,
    NotificationComponent,
    RequestCodeComponent,
    ResetPasswordComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterModule.forRoot(appRoutes, { useHash: true }),
    BrowserAnimationsModule,
    MaterialModule,
    SocialLoginModule,
    GoogleSigninButtonModule,
    ServiceWorkerModule.register('main-sw.js', {
      enabled: !isDevMode(),
      // Register the ServiceWorker as soon as the application is stable
      // or after 30 seconds (whichever comes first).
      registrationStrategy: 'registerWhenStable:30000',
    }),
    AngularFireModule.initializeApp(environment.FirebaseConfig),
    AngularFireMessagingModule,
  ],
  providers: [
    ExpenseService,
    UserService,
    NotificationService,
    EmailService,
    {
      provide: 'SocialAuthServiceConfig',
      useValue: {
        autoLogin: false,
        providers: [
          {
            id: GoogleLoginProvider.PROVIDER_ID,
            provider: new GoogleLoginProvider(
              '623486988411-7p4a0idjc3f8p8u36ob2bl4qd7qnboeh.apps.googleusercontent.com'
            ),
          },
        ],
      } as SocialAuthServiceConfig,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
