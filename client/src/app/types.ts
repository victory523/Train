import { HttpErrorResponse } from "@angular/common/http";

export interface HttpRequestState<T> {
  isLoading: boolean;
  hasFailed: boolean;
  isReady: boolean;
  value?: T;
  error?: HttpErrorResponse | Error;
}

export interface WeightResponse {
    weight?: number;
}
