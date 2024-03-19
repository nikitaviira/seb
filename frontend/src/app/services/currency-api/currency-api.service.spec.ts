import {HttpErrorResponse} from '@angular/common/http';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import {TestBed} from '@angular/core/testing';

import {CurrencyApiService} from './currency-api.service';

describe('CurrencyApiService', () => {
  let service: CurrencyApiService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CurrencyApiService],
    });
    service = TestBed.inject(CurrencyApiService);
    httpMock = TestBed.inject(HttpTestingController);
    // @ts-expect-error: protected access
    baseApiUrl = service.baseApiUrl;
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

    const request = httpMock.expectOne(`${baseApiUrl}/api/currency-list`);
    expect(request.request.method).toBe('GET');
  });

  it('on error should fetch again', () => {
    service.currencies.subscribe({
      error: (err) => expect(err).toBeInstanceOf(HttpErrorResponse),
    });

    httpMock.expectOne(`${baseApiUrl}/api/currency-list`).error(new ProgressEvent(''));

    service.currencies.subscribe();

    const request = httpMock.expectOne(`${baseApiUrl}/api/currency-list`);
    expect(request.request.method).toBe('GET');
  });
});
