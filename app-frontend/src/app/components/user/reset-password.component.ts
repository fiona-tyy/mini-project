import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
})
export class ResetPasswordComponent implements OnInit {
  form!: FormGroup;
  hide1 = true;
  hide2 = true;

  constructor(
    private userSvc: UserService,
    private router: Router,
    private fb: FormBuilder
  ) {}
  ngOnInit(): void {
    this.form = this.fb.group({
      code: this.fb.control<string>('', [
        Validators.required,
        Validators.minLength(1),
      ]),
      password: this.fb.control<string>('', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(32),
      ]),
      password2: this.fb.control<string>('', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(32),
      ]),
    });
  }

  invalid() {
    return (
      this.form.invalid ||
      this.form.controls['password'].value !=
        this.form.controls['password2'].value
    );
  }

  resetPassword() {
    console.info('form', this.form.value);
    this.userSvc
      .resetPassword(this.form.value['code'], this.form.value['password'])
      .subscribe({
        next: () => {
          alert('Successfully reset password! Login with new password.');
          this.router.navigate(['/']);
        },
        error: (error) => {
          console.info(error);
          alert(error);
        },
      });
  }
}
