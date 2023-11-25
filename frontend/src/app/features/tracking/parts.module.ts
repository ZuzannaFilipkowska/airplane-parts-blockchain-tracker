import { NgModule } from '@angular/core';
import { PartsRoutingModule } from './parts-routing.module';
import { MatCardModule } from '@angular/material/card';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../shared/shared.module';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslateModule } from '@ngx-translate/core';
import {MatTableModule} from "@angular/material/table";
import {PartsComponent} from "./components/parts/parts.component";
import {MatButtonModule} from "@angular/material/button";

@NgModule({
  declarations: [PartsComponent],
  imports: [
    PartsRoutingModule,
    MatCardModule,
    CommonModule,
    HttpClientModule,
    SharedModule,
    MatInputModule,
    MatFormFieldModule,
    TranslateModule,
    ReactiveFormsModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatButtonModule,
  ],
  exports: [PartsComponent],
})
export class PartsModule {}
