import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MarketRoutingModule } from './market-routing.module';
import { MarketComponent } from './market.component';
import { SharedModule } from '../../shared/shared.module';
import { MatButtonModule } from '@angular/material/button';
import { TranslateModule } from '@ngx-translate/core';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@NgModule({
  declarations: [MarketComponent],
  imports: [
    CommonModule,
    MarketRoutingModule,
    SharedModule,
    MatButtonModule,
    TranslateModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
  ],
})
export class MarketModule {}
