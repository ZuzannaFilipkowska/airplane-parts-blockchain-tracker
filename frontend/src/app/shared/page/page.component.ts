import { Component } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable, of } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import {
  faBox, faGear,
  faMagnifyingGlass,
  faRectangleList,
  IconDefinition,
} from '@fortawesome/free-solid-svg-icons';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import {AuthService} from "../../core/auth/auth.service";

@Component({
  selector: 'app-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.scss'],
})
export class PageComponent {
  isHandset$: Observable<boolean> = this.breakpointObserver
    .observe(Breakpoints.Handset)
    .pipe(
      map((result) => result.matches),
      shareReplay()
    );
  faBox: IconDefinition = faBox;
  faSearch: IconDefinition = faMagnifyingGlass;
  faHistory: IconDefinition = faRectangleList;
  isLoggedIn$: Observable<boolean> = of(false);
  faSettings: IconDefinition = faGear;

  constructor(
    private breakpointObserver: BreakpointObserver,
    private authService: AuthService,
    private router: Router,
    private _snackBar: MatSnackBar
  ) {
    this.isLoggedIn$ = this.authService.isLoggedIn;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
    this.openSnackBar("You've been logged out", 'OK');
  }

  openSnackBar(message: string, label: string) {
    this._snackBar.open(message, label, {
      duration: 5000,
    });
  }
}
