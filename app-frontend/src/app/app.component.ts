import { Component, OnDestroy, OnInit } from '@angular/core';
import { User, UserDTO } from './model';
import { UserService } from './services/user.service';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit, OnDestroy {
  isAuthenticated = false;
  activeUser!: UserDTO | null;
  userSub$!: Subscription;

  constructor(private userSvc: UserService, private router: Router) {}

  ngOnInit(): void {
    this.userSvc.autoLogin();
    this.userSub$ = this.userSvc.user.subscribe((user) => {
      this.isAuthenticated = !!user;
    });
  }

  logout() {
    this.userSvc.logout();
    this.router.navigate(['/']);
  }

  ngOnDestroy(): void {
    this.userSub$.unsubscribe();
  }
}
