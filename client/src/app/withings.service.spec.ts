import { TestBed } from '@angular/core/testing';

import { WithingsService } from './withings.service';

describe('WithingsService', () => {
  let service: WithingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WithingsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
