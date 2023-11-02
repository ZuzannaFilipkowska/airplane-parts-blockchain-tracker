import { NgModule } from '@angular/core';
import { TrackingComponent } from './components/tracking/tracking.component';
import { TrackingRoutingModule } from './tracking-routing.module';
import { MatCardModule } from '@angular/material/card';
import { HttpClientModule } from '@angular/common/http';
import { TrackingService } from './tracking.service';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../shared/shared.module';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslateModule } from '@ngx-translate/core';
import {MatTableModule} from "@angular/material/table";

@NgModule({
  declarations: [TrackingComponent],
    imports: [
        TrackingRoutingModule,
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
    ],
  exports: [TrackingComponent],
  providers: [TrackingService],
})
export class TrackingModule {}
