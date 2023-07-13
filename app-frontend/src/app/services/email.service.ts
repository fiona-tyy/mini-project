import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Friend, UserDTO } from '../model';

@Injectable()
export class EmailService {
  constructor(private http: HttpClient) {}

  sendReminderEmail(friend: Friend, user: UserDTO) {
    return this.http.post('/api/email/remind', {
      toName: friend.name,
      toEmail: friend.email,
      senderName: user.name,
      senderEmail: user.email,
      amount: friend.amount_outstanding,
    });
  }
}
