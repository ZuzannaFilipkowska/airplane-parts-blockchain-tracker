import { Component, OnInit } from '@angular/core';
import {TrackingService} from "../../services/tracking.service";
import {AuthService} from "../../core/auth/auth.service";


@Component({
  selector: 'app-market',
  templateUrl: './market.component.html',
  styleUrls: ['./market.component.scss'],
})
export class MarketComponent implements OnInit {
  constructor(
    private trackingService: TrackingService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    console.log(this.authService.userValue);
  }

  displayedColumns: string[] = ['number', 'address', 'status'];
  // dataSource = new MatTableDataSource(ELEMENT_DATA);

  // applyFilter(event: Event) {
  //   const filterValue = (event.target as HTMLInputElement).value;
  //   this.dataSource.filter = filterValue.trim().toLowerCase();
  // }
}
