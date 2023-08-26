import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WeightComponent } from '../weight/weight.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, WeightComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

}
