import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Part } from '../../models/part';
import { TrackingService } from '../tracking/tracking.service';
import { AuthService } from '../../services/auth.service';


@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss'],
})
export class HistoryComponent implements OnInit {
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
