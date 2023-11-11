import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {PartsComponent} from "./components/parts/parts.component";

const routes: Routes = [{ path: '', component: PartsComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PartsRoutingModule {}
