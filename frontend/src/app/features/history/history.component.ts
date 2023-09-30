import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Package } from '../../models/package';
import { TrackingService } from '../tracking/tracking.service';
import { AuthService } from '../../services/auth.service';

const ELEMENT_DATA: Package[] = [
  { status: 'delivered', address: 'Lublin', number: 122234079 },
  { status: 'delivered', address: 'Lublin', number: 1222340079 },
  { status: 'delivered', address: 'Lublin', number: 12223453453459 },
  { status: 'delivered', address: 'Lublin', number: 1223453459 },
  { status: 'delivered', address: 'Lublin', number: 122223423429 },
  { status: 'delivered', address: 'Lublin', number: 6666666666 },
  { status: 'delivered', address: 'Warsaw', number: 1222340079 },
  { status: 'in-proggress', address: 'Lublin', number: 1222340079 },
];

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
  dataSource = new MatTableDataSource(ELEMENT_DATA);

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
}
