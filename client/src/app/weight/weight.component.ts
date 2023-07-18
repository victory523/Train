import { Component } from '@angular/core';
import { map } from 'rxjs';
import { WeightService } from '../weight.service';

@Component({
  selector: 'app-weight',
  templateUrl: './weight.component.html',
  styleUrls: ['./weight.component.css'],
})
export class WeightComponent {
  constructor(private weightService: WeightService) {}

  $weight = this.weightService
    .getWeight()
    .pipe(map((weight) => weight?.toString() ?? '?'));
}
