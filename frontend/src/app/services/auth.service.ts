import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { User } from '../models/user';
import { environment } from '../../environments/environment';
import { LOGIN } from '../core/config/api-paths';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;
  private loginUrl = environment.baseUrl + LOGIN;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<User | null>(
      localStorage.getItem('currentUser')
        ? JSON.parse(<string>localStorage.getItem('currentUser'))
        : null
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  login(username: string, password: string) {
    return this.http.post<any>(this.loginUrl, { username, password }).pipe(
      map((res) => {
        let user: User = { email: '', name: '', surname: '', username: '' };
        if (res && res.accessToken) {
          user = {
            username: res.user,
            name: res.name,
            surname: res.surname,
            email: res.email,
          };
          localStorage.setItem('currentUser', JSON.stringify(res));
          localStorage.setItem('token', JSON.stringify(res.accessToken));
          this.currentUserSubject.next(user);
        }

        return user;
      })
    );
  }

  logout() {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    return this.currentUserValue !== null;
  }
}
