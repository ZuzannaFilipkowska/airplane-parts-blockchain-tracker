import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import {faBox} from "@fortawesome/free-solid-svg-icons";
import {AuthService} from "../../../core/auth/auth.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  loginForm: FormGroup;
  hide: boolean = true;
  protected readonly faBox = faBox;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private _snackBar: MatSnackBar,
    public router: Router
  ) {
    this.loginForm = fb.group({
      username: new FormControl<string>(''),
      password: new FormControl<string>(''),
    });
  }

  onSubmit() {
    this.authService
      .login(this.loginForm.value.username, this.loginForm.value.password)
      .subscribe({
        next: () => {
          this.router.navigate(['/track']);
          this.openSnackBar('Logowanie powiodło się', 'Witaj');
        },
        error: () => {
          this.openSnackBar('Logowanie nie udało się', 'Spróbuj ponownie');
        }
      });
  }

  openSnackBar(message: string, label: string) {
    this._snackBar.open(message, label, {
      duration: 5000,
    });
  }
}
