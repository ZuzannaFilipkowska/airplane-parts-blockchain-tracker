import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Part } from '../../models/part';
import { AuthService } from '../../services/auth.service';
import {TrackingService} from "../../services/tracking.service";


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
    console.log(this.authService.currentUserValue);
  }

  displayedColumns: string[] = ['number', 'address', 'status'];
  // dataSource = new MatTableDataSource(ELEMENT_DATA);

  // applyFilter(event: Event) {
  //   const filterValue = (event.target as HTMLInputElement).value;
  //   this.dataSource.filter = filterValue.trim().toLowerCase();
  // }
}
