import {
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription, firstValueFrom } from 'rxjs';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-request-code',
  templateUrl: './request-code.component.html',
  styleUrls: ['./request-code.component.css'],
})
export class RequestCodeComponent implements OnInit {
  form!: FormGroup;

  constructor(
    private userSvc: UserService,
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      email: this.fb.control<string>('', [
        Validators.email,
        Validators.required,
      ]),
    });
  }

  requestCode() {
    let email = this.form.value['email'];
    this.userSvc.requestCode(email).subscribe({
      next: (data) => {
        alert('One-time code for password reset has been sent to you email');
        this.router.navigate(['/reset-password']);
      },
      error: (error) => {
        console.info(error);
        alert(error);
      },
    });
  }
}
