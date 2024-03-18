import { HttpErrorResponse } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { CurrencyApiService } from './currency-api.service';

fdescribe('CurrencyApiService', () => {
  let service: CurrencyApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CurrencyApiService]
    });
    service = TestBed.inject(CurrencyApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('currencies should only be queried once when subscribed multiple times', () => {
    service.currencies.subscribe();
    service.currencies.subscribe();
    service.currencies.subscribe();

    // @ts-expect-error: protected access
    const request = httpMock.expectOne(`${service.baseApiUrl}/api/currency-list`);
    expect(request.request.method).toBe('GET');
  });

  it('on error should fetch again', () => {
    service.currencies.subscribe({
      error: (err) => expect(err).toBeInstanceOf(HttpErrorResponse)
    });

    // @ts-expect-error: protected access
    httpMock.expectOne(`${service.baseApiUrl}/api/currency-list`).error();

    service.currencies.subscribe();

    // @ts-expect-error: protected access
    const request = httpMock.expectOne(`${service.baseApiUrl}/api/currency-list`);
    expect(request.request.method).toBe('GET');
  });
});
