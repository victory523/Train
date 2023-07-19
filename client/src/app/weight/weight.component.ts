import { Component, OnInit } from '@angular/core';
import { HttpRequestState } from '../types';
import { initialHttpRequestState, subscribeToRequestState } from '../utils';
import { WeightService } from '../weight.service';

@Component({
  selector: 'app-weight',
  templateUrl: './weight.component.html',
  styleUrls: ['./weight.component.css'],
})
export class WeightComponent implements OnInit {
  constructor(private weightService: WeightService) {}

  weightState: HttpRequestState<number | undefined> = initialHttpRequestState;

  ngOnInit(): void {
    subscribeToRequestState(this.weightService.getWeight(), newState => {
      this.weightState = newState
    })
  }
}
