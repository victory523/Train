import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RideComponent } from '../ride/ride.component';
import { WeightComponent } from '../weight/weight.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RideComponent, WeightComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent {}
