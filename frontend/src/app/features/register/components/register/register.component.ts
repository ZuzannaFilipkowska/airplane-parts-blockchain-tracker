import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { UserService } from '../../../../services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent {
  registerForm: FormGroup;
  hide: boolean = true;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private _snackBar: MatSnackBar,
    public router: Router
  ) {
    this.registerForm = fb.group({
      username: new FormControl<string>(''),
      email: new FormControl<string>(''),
      name: new FormControl<string>(''),
      surname: new FormControl<string>(''),
      password: new FormControl<string>(''),
    });
  }

  onSubmit() {
    console.log(this.registerForm.getRawValue());
    this.userService.register(this.registerForm.getRawValue()).subscribe(
      (res) => {
        this.router.navigate(['/login']);
        this.openSnackBar('Registration was successful', 'Log in');
      },
      (error) => {
        this.openSnackBar('Registration failed', 'Try again');
      }
    );
  }

  openSnackBar(message: string, label: string) {
    this._snackBar.open(message, label, {
      duration: 5000,
    });
  }
}
