import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { WithingsService } from '../withings.service';

@Component({
  selector: 'app-weight',
  templateUrl: './weight.component.html',
  styleUrls: ['./weight.component.css'],
})
export class WeightComponent implements OnInit {
  constructor(private withingsService: WithingsService) {}

  weight?: number;

  ngOnInit(): void {
    this.getWeight();
  }

  getWeight(): void {
    this.withingsService
      .getWeight()
      .subscribe((weightResponse) => (this.weight = weightResponse.weight), error => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          debugger;
          window.location.href = error.error._links.oauth2Login.href;
        }
      });
  }
}
