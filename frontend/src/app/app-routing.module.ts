import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuardService } from './services/auth-guard.service';

const routes: Routes = [
  {
    path: 'login',
    loadChildren: () =>
      import('./features/login/login.module').then((m: any) => m.LoginModule),
  },
  {
    path: 'register',
    loadChildren: () =>
      import('./features/register/register.module').then(
        (m: any) => m.RegisterModule
      ),
  },
  {
    path: 'track',
    loadChildren: () =>
      import('./features/tracking/tracking.module').then(
        (m: any) => m.TrackingModule
      ),
  },
  {
    path: 'history',
    loadChildren: () =>
      import('./features/history/history.module').then((m) => m.HistoryModule),
    canActivate: [AuthGuardService],
  },
  {
    path: '**',
    redirectTo: 'register',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
