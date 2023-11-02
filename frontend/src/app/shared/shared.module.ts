import { NgModule } from '@angular/core';
import { PageComponent } from './page/page.component';
import { CommonModule } from '@angular/common';
import { LayoutModule } from '@angular/cdk/layout';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { MatMenuModule } from '@angular/material/menu';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import {RouterLinkActive, RouterLinkWithHref} from "@angular/router";

@NgModule({
  declarations: [PageComponent],
    imports: [
        CommonModule,
        LayoutModule,
        MatToolbarModule,
        MatButtonModule,
        MatSidenavModule,
        MatIconModule,
        MatListModule,
        MatMenuModule,
        MatSnackBarModule,
        FontAwesomeModule,
        RouterLinkWithHref,
        RouterLinkActive,
    ],
  exports: [PageComponent],
})
export class SharedModule {}
