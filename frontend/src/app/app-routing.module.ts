import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AuthGuard} from "./core/auth/auth.guard";

const routes: Routes = [
  {
    path: 'login',
    loadChildren: () =>
      import('./features/login/login.module').then((m: any) => m.LoginModule),
  },
  {
    path: 'czesci',
    loadChildren: () =>
      import('./features/tracking/parts.module').then(
        (m: any) => m.PartsModule
      ),
    //canActivate: [AuthGuard],
  },
  {
    path: 'market',
    loadChildren: () =>
      import('./features/marketplace/market.module').then((m) => m.MarketModule),
    canActivate: [AuthGuard],
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
