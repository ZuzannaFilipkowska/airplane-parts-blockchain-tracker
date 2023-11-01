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
    path: 'czesci',
    loadChildren: () =>
      import('./features/tracking/tracking.module').then(
        (m: any) => m.TrackingModule
      ),
    canActivate: [AuthGuardService],
  },
  {
    path: 'market',
    loadChildren: () =>
      import('./features/history/history.module').then((m) => m.HistoryModule),
    canActivate: [AuthGuardService],
  },
  {
    path: '**',
    redirectTo: 'czesci',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
