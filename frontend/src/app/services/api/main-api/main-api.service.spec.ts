import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {TestBed} from '@angular/core/testing';

import {
  type ChartDto,
  ChartPeriodType,
  type ConversionRequestDto,
  type ConversionResultDto,
  MainApiService,
} from './main-api.service';

describe('MainApiService', () => {
  let service: MainApiService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [MainApiService],
    });
    service = TestBed.inject(MainApiService);
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

  it('should call GET /historical-chart with chartPeriodType HTTP param', () => {
    const response: ChartDto = {
      chartPoints: [],
      changePercent: '12.50',
    };
    const currencyCode = 'USD';
    const chartPeriodType = ChartPeriodType.MONTH;

    service.fetchHistoricalChartData(currencyCode, chartPeriodType).subscribe((resp) => {
      expect(resp).toEqual(response);
    });

    const request = httpMock.expectOne(`${baseApiUrl}/api/USD/historical-chart?chartType=MONTH`);
    expect(request.request.method).toBe('GET');
    request.flush(response);
  });

  it('should call POST /convert with chartPeriodType HTTP param', () => {
    const body: ConversionRequestDto = {
      base: 'USD',
      quote: 'EUR',
      amount: '12',
    };

    const response: ConversionResultDto = {
      base: {
        code: 'EUR',
        fullName: 'Euro',
      },
      quote: {
        code: 'USD',
        fullName: 'US Dollar',
      },
      amount: '12',
      conversionRate: '1',
      invertedConversionRate: '1',
      conversionResult: '12',
    };

    service.fetchConversionResult(body).subscribe((resp) => {
      expect(resp).toEqual(response);
    });

    const request = httpMock.expectOne(`${baseApiUrl}/api/convert`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(body);
    request.flush(response);
  });
});
