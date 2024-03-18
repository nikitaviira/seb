import {HttpClient} from '@angular/common/http';
import {Injectable, Injector} from '@angular/core';
import {Observable} from 'rxjs';

import {environment} from '../../environments/environment';

@Injectable()
export class ApiService {
  private httpClient: HttpClient;
  protected baseApiUrl = environment.baseUrl;

  constructor(protected injector: Injector) {
    this.httpClient = injector.get(HttpClient);
  }

  protected get<T>(
    path: string,
    options: {[param: string]: unknown} = {}
  ): Observable<T> {
    return this.httpClient.get<T>(`${this.baseApiUrl}/api/${path}`, options);
  }

  protected post<T>(
    path: string,
    body: any,
    options: {[param: string]: unknown} = {}
  ): Observable<T> {
    return this.httpClient.post<T>(
      `${this.baseApiUrl}/api/${path}`,
      body,
      options
    );
  }
}
