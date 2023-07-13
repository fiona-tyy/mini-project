import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ExpenseService } from '../services/expense.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../services/user.service';
import { User, UserDTO } from '../model';
import {
  GoogleLoginProvider,
  SocialAuthService,
  SocialUser,
} from '@abacritt/angularx-social-login';
import { Subscription, exhaustMap, tap } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit, OnDestroy {
  constructor(
    private router: Router,
    private expenseSvc: ExpenseService,
    private userSvc: UserService,
    private fb: FormBuilder,
    private authService: SocialAuthService
  ) {}

  hide1 = true;
  hide2 = true;
  isLoginMode = true;
  isLoading = false;

  activeUser!: UserDTO | null;
  userSub$!: Subscription;
  loginForm!: FormGroup;
  signupForm!: FormGroup;
  socialUser!: SocialUser;
  // isLoggedin?: boolean;

  error: string | null = null;

  ngOnInit(): void {
    this.userSub$ = this.userSvc.user
      .pipe(
        tap((user) => {
          let isLoggedin = !!user;
          if (isLoggedin) {
            this.router.navigate(['/home']);
          }
        })
      )
      .subscribe();
    this.loginForm = this.createLoginForm();
    this.signupForm = this.createSignupForm();

    let user$ = this.authService.authState
      .pipe(
        tap((user) => {
          this.socialUser = user;
          console.log(this.socialUser);
        }),
        exhaustMap((user) =>
          this.userSvc.loginWithGoogle(user.email, user.idToken)
        )
      )
      .subscribe();
  }

  login() {
    if (this.loginForm.invalid) {
      return;
    }
    this.isLoading = true;
    const email = this.loginForm.value['email'];
    const password = this.loginForm.value['password'];

    let user$ = this.userSvc.login(email, password);
    user$.subscribe({
      next: (data) => {
        this.isLoading = false;
        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.info(error);
        alert(error);
        this.isLoading = false;
      },
    });
  }

  signup() {
    if (this.signupForm.invalid) {
      return;
    }
    this.isLoading = true;
    const name = this.signupForm.value['name'];
    const email = this.signupForm.value['email'];
    const password = this.signupForm.value['password'];
    let user$ = this.userSvc.signup(name, email, password);
    user$.subscribe({
      next: (data) => {
        this.isLoading = false;
        alert('Account successfully created! Please login to proceed.');
        this.signupForm.reset();
        this.loginForm.reset();
        this.isLoginMode = true;
      },
      error: (error) => {
        console.info(error);
        alert(error);
        this.isLoading = false;
      },
    });
  }

  switchMode() {
    this.isLoginMode = !this.isLoginMode;
  }

  loginWithGoogle() {
    // let user$ = this.authService.authState.pipe(
    //   tap((user) => {
    //     this.socialUser = user;
    //     console.log(this.socialUser);
    //   }),
    //   exhaustMap((user) =>
    //     this.userSvc.loginWithGoogle(user.email, user.idToken)
    //   )
    // );
    // this.authService.authState.subscribe((user) => {
    //   this.socialUser = user;
    //   this.isLoggedin = user != null;
    //   console.log(this.socialUser);
    // });
    // // let user$ = this.userSvc.loginWithGoogle(
    // //   this.socialUser.email,
    // //   this.socialUser.idToken
    // // );
    // user$.subscribe({
    //   next: (data) => {
    //     this.isLoading = false;
    //     this.router.navigate(['/home']);
    //   },
    //   error: (error) => {
    //     console.info(error);
    //     alert(error);
    //     this.isLoading = false;
    //   },
    // });
  }

  invalidSignupForm() {
    return (
      this.signupForm.invalid ||
      this.signupForm.controls['password'].value !=
        this.signupForm.controls['password2'].value
    );
  }

  ngOnDestroy(): void {
    this.userSub$.unsubscribe();
  }
  private createLoginForm() {
    return this.fb.group({
      email: this.fb.control<string>('lily@email.com', [
        Validators.required,
        Validators.email,
      ]),
      password: this.fb.control<string>('abcd1234', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(32),
      ]),
    });
  }
  private createSignupForm() {
    return this.fb.group({
      name: this.fb.control<string>('Lily', [Validators.required]),
      email: this.fb.control<string>('lily@email.com', [
        Validators.required,
        Validators.email,
      ]),
      password: this.fb.control<string>('abcd1234', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(32),
      ]),
      password2: this.fb.control<string>('abcd1234', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(32),
      ]),
    });
  }
}
