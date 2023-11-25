import { Component, OnInit } from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {Part} from "../../../../models/part";
import {TrackingService} from "../../../../services/tracking.service";
import {AuthService} from "../../../../core/auth/auth.service";


const ELEMENT_DATA: Part[] = [
    {
        name: 'Proximity Sensor',
        id: '0673ef5a-6dfd-4a10-88d9-c7685972596e',
        price: 1600,
        weight: 23,
        width: 6,
        length: 7
    },
    {
        name: 'Screw',
        id: '65ded6e3-52fd-43dc-8794-d25dec48ff53',
        price: 6,
        weight: 0.001,
        width: 4,
        length: 1
    },
    {
        name: 'Cargo straps',
        id: '31de7838-52ed-4c8b-9ff3-5b1a82bc847e',
        price: 900,
        weight: 37,
        width: 10,
        length: 1000
    },
    {
        name: 'Oxygen mask',
        id: '123ea25b-8347-415b-9fd6-f641acf24670',
        price: 50,
        weight: 0.1,
        width: 30,
        length: 20
    },
];

@Component({
  selector: 'app-parts',
  templateUrl: './parts.component.html',
  styleUrls: ['./parts.component.scss'],
})
export class PartsComponent implements OnInit {

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
        console.log(this.authService.userValue);
    }

    displayedColumns: string[] = ['name', 'id', 'price', 'weight', 'width', 'length', 'btn'];
    dataSource = new MatTableDataSource(ELEMENT_DATA);

}
