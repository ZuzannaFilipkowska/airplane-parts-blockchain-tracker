import { Component } from '@angular/core';
import { PartsService } from './services/parts.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  title = 'frontend';

  constructor(private partsService: PartsService) {
      this.partsService.getAllParts().subscribe(data => console.log(data)
      );
  }
}
