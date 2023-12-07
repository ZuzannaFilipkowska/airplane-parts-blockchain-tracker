import {Component, Inject} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {Part} from "../../../../models/part";
import {TrackingService} from "../../../../services/tracking.service";
import {MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {FormBuilder, FormGroup, ReactiveFormsModule} from "@angular/forms";

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
export class PartsComponent {

    dataSource!: MatTableDataSource<Part>;

    constructor(
        private trackingService: TrackingService,
        public dialog: MatDialog
    ) {
        this.trackingService.getAllParts().subscribe((data: Part[]) => {
            this.dataSource = new MatTableDataSource(data);
        });
    }

    displayedColumns: string[] = ['name', 'id', 'price', 'weight', 'width', 'length', 'btn'];

    openDialog(): void {
        const dialogRef = this.dialog.open(DialogOverviewExampleDialog, {
            data: {},
        });
    }
}

interface DialogData {
}

@Component({
    selector: 'app-add-part-dialog',
    templateUrl: 'app-add-part-dialog.html',
    standalone: true,
    imports: [
        MatDialogModule,
        MatButtonModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule
    ],
})
export class DialogOverviewExampleDialog {
    form: FormGroup;
    constructor(
        public dialogRef: MatDialogRef<DialogOverviewExampleDialog>,
        @Inject(MAT_DIALOG_DATA) public data: DialogData,
        private fb: FormBuilder
    ) {
        this.form = fb.group({
            name: [''],
            price: [''],
            id: [''],
            desc: [''],
            weight: [''],
            width: [''],
            length: [''],
        });
    }

    onNoClick(): void {
        this.dialogRef.close();
    }
}