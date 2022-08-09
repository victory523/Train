import { Component } from '@angular/core';
import { map } from 'rxjs';
import { WithingsService } from '../withings.service';

@Component({
  selector: 'app-weight',
  templateUrl: './weight.component.html',
  styleUrls: ['./weight.component.css'],
})
export class WeightComponent {
  constructor(private withingsService: WithingsService) {}

  $weight = this.withingsService
    .getWeight()
    .pipe(map((weight) => weight?.toString() ?? '?'));
}
