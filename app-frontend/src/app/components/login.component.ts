import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ExpenseService } from '../services/expense.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../services/user.service';
import { User } from '../model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  constructor(
    private router: Router,
    private expenseSvc: ExpenseService,
    private userSvc: UserService,
    private fb: FormBuilder
  ) {}

  hide = true;
  isLoginMode = true;
  isLoading = false;
  // activeUser = '4';
  activeUser!: User;
  loginForm!: FormGroup;
  signupForm!: FormGroup;

  error: string | null = null;

  ngOnInit(): void {
    this.loginForm = this.createLoginForm();
    this.signupForm = this.createSignupForm();
    if (this.isLoginMode) {
    } else {
    }
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
    // this.userSvc
    //   .login(email, password)
    //   .then((result) => {
    //     //TO AMEND
    //     (this.activeUser = result),
    //       (this.userSvc.activeUser = result),
    //       (this.isLoading = false),
    //       this.router.navigate(['/', this.activeUser.id, 'home']);
    //   })
    //   .catch((err) => {
    //     (this.isLoading = false), alert(err.error.message);
    //   });
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

  // submitForm() {
  //   //make call to user service
  //   if (this.isLoginMode) {
  //     const userEmail = this.loginForm.value['email'];
  //     this.userSvc.getActiveUser(userEmail).then((result) => {
  //       (this.activeUser = result),
  //         (this.userSvc.activeUser = result),
  //         this.router.navigate(['/', this.activeUser.id, 'home']);
  //     });

  //     // this.userSvc.activeUser = this.activeUser;
  //     // this.router.navigate(['/', this.activeUser, 'home']);
  //   } else {
  //     alert('Account created successfully. Please login.');
  //   }
  // }

  switchMode() {
    this.isLoginMode = !this.isLoginMode;
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
    });
  }
}
