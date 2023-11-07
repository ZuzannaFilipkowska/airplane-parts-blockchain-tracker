import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../../../services/auth.service";
import {MatTableDataSource} from "@angular/material/table";
import {Part} from "../../../../models/part";
import {TrackingService} from "../../../../services/tracking.service";


const ELEMENT_DATA: Part[] = [
    {
        name: 'Sensor',
        id: 'adasdasdasd',
        price: 100,
        weight: 1,
        width: 1,
        length: 1
    },
    {
        name: 'Sensor 2',
        id: 'adasdaasdadsasd',
        price: 100,
        weight: 1,
        width: 1,
        length: 1
    },
    {
        name: 'Sensor 3',
        id: 'a2asdasdasd',
        price: 100,
        weight: 1,
        width: 1,
        length: 1
    },
    {
        name: 'Sensor 4',
        id: 'bbbbdasdasd',
        price: 100,
        weight: 1,
        width: 1,
        length: 1
    },
];

@Component({
  selector: 'app-parts',
  templateUrl: './parts.component.html',
  styleUrls: ['./parts.component.scss'],
})
export class PartsComponent implements OnInit {
  // packageIdControl: FormControl<string | null> = new FormControl<string>('');
  // isLoading: boolean = false;
  // isError: boolean = false;

  // parcel: Parcel | null = null;

  // constructor(private helloWorldService: TrackingService) {}

  // ngOnInit(): void {
  //   this.packageIdControl.valueChanges
  //     .pipe(
  //       distinctUntilChanged(),
  //       tap((id: any) => {
  //         this.isLoading = true;
  //         this.helloWorldService
  //           .getPackageData(<string>this.packageIdControl.value)
  //           .pipe(take(1))
  //           .subscribe(
  //             (res: any) => {
  //               this.parcel = res
  //                 ? {
  //                     status: res.status,
  //                     address: res.address,
  //                     trackingId: res.trackingNumber,
  //                   }
  //                 : res;
  //               this.isLoading = false;
  //             },
  //             (error) => {
  //               this.isLoading = false;
  //               this.parcel = null;
  //             }
  //           );
  //       })
  //     )
  //     .subscribe();
  // }

    constructor(
        private trackingService: TrackingService,
        private authService: AuthService
    ) {}

    ngOnInit(): void {
        console.log(this.authService.currentUserValue);
    }

    displayedColumns: string[] = ['name', 'id', 'price', 'weight', 'width', 'length'];
    dataSource = new MatTableDataSource(ELEMENT_DATA);

    applyFilter(event: Event): void {
        const filterValue = (event.target as HTMLInputElement).value;
        this.dataSource.filter = filterValue.trim().toLowerCase();
    }
}
