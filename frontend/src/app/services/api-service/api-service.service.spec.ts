import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import {TestBed} from '@angular/core/testing';

import {ApiService} from './api-service.service';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService],
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send GET request to the specified path', () => {
    const testData = {message: 'This is a test'};
    const path = 'test';

    // @ts-expect-error: protected access
    service.get(path).subscribe((response) => {
      expect(response).toEqual(testData);
    });

    // @ts-expect-error: protected access
    const request = httpMock.expectOne(`${service.baseApiUrl}/api/${path}`);
    expect(request.request.method).toBe('GET');
    request.flush(testData);
  });

  it('should send POST request to the specified path', () => {
    const testData = {message: 'This is a test'};
    const path = 'test';
    const body = {key: 'value'};

    // @ts-expect-error: protected access
    service.post(path, body).subscribe((response) => {
      expect(response).toEqual(testData);
    });

    // @ts-expect-error: protected access
    const request = httpMock.expectOne(`${service.baseApiUrl}/api/${path}`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(body);
    request.flush(testData);
  });
});
