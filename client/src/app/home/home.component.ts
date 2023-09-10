import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WeightComponent } from '../weight/weight.component';
import { RideComponent } from '../ride/ride.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RideComponent, WeightComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent {
  @Input()
  period: number | undefined;
}
