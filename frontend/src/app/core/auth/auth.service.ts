import {Injectable} from "@angular/core";
import {BehaviorSubject, map, Observable} from "rxjs";
import {User} from "../../models/user";
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private userSubject: BehaviorSubject<User | null> =
      new BehaviorSubject<User | null>(this.getUserFromLocalStorage());
  public user: Observable<User | null> = this.userSubject.asObservable();
  private isLoggedInSubject: BehaviorSubject<boolean> =
      new BehaviorSubject<boolean>(false);
  public isLoggedIn: Observable<boolean> =
      this.isLoggedInSubject.asObservable();

  constructor(
      private router: Router,
      private http: HttpClient
  ) {}

  get userValue(): User | null {
    return this.userSubject.value;
  }

  private getUserFromLocalStorage(): User | null {
    return JSON.parse(localStorage.getItem('user') || 'null');
  }

  login(username: string, password: string): Observable<User> {
    return this.http
    .post<User>(`${environment.baseUrl}/api/v1/auth/login`, {
      username,
      password
    })
    .pipe(
        map((user) => {
          user.authdata = window.btoa(`${username}:${password}`);
          localStorage.setItem('user', JSON.stringify(user));
          this.userSubject.next(user);
          this.isLoggedInSubject.next(true);
          return user;
        })
    );
  }

  logout(): void {
    localStorage.removeItem('user');
    this.userSubject.next(null);
    this.isLoggedInSubject.next(false);
    this.router.navigate(['/login']);
  }
}