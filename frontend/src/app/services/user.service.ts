import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/user';
import { environment } from '../../environments/environment';
import { REGISTER } from '../core/config/api-paths';

@Injectable({ providedIn: 'root' })
export class UserService {
  registerUrl = environment.baseUrl + REGISTER;
  constructor(private http: HttpClient) {}

  register(user: User) {
    return this.http.post(this.registerUrl, user);
  }
}
