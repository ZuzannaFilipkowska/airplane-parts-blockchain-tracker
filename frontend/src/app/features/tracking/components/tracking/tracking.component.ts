import { Component, OnInit } from '@angular/core';
import { TrackingService } from '../../tracking.service';
import { distinctUntilChanged, take, tap } from 'rxjs';
import { FormControl } from '@angular/forms';

export interface Parcel {
  trackingId: number;
  address: string;
  status: string;
}
@Component({
  selector: 'app-tracking',
  templateUrl: './tracking.component.html',
  styleUrls: ['./tracking.component.scss'],
})
export class TrackingComponent implements OnInit {
  packageIdControl: FormControl<string | null> = new FormControl<string>('');
  isLoading: boolean = false;
  isError: boolean = false;

  parcel: Parcel | null = null;

  constructor(private helloWorldService: TrackingService) {}

  ngOnInit(): void {
    this.packageIdControl.valueChanges
      .pipe(
        distinctUntilChanged(),
        tap((id: any) => {
          this.isLoading = true;
          this.helloWorldService
            .getPackageData(<string>this.packageIdControl.value)
            .pipe(take(1))
            .subscribe(
              (res: any) => {
                this.parcel = res
                  ? {
                      status: res.status,
                      address: res.address,
                      trackingId: res.trackingNumber,
                    }
                  : res;
                this.isLoading = false;
              },
              (error) => {
                this.isLoading = false;
                this.parcel = null;
              }
            );
        })
      )
      .subscribe();
  }
}
