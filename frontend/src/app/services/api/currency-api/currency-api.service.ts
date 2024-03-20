import {Injectable, Injector} from '@angular/core';
import {catchError, Observable, ReplaySubject, share} from 'rxjs';

import {ApiServiceBase} from '../ApiServiceBase';

export type CurrencyDto = {
  code: string;
  fullName: string;
}

@Injectable({
  providedIn: 'root',
})
export class CurrencyApiService extends ApiServiceBase {
  private readonly currencies$: Observable<CurrencyDto[]>;

  constructor(injector: Injector) {
    super(injector);
    this.currencies$ = this.fetchCurrencies().pipe(
      share({
        connector: () => new ReplaySubject(1),
        resetOnComplete: false,
        resetOnError: true,
      })
    );
  }

  private fetchCurrencies(): Observable<CurrencyDto[]> {
    return this.get<CurrencyDto[]>('currency-list').pipe(
      catchError((error) => {
        throw error;
      })
    );
  }

  get currencies(): Observable<CurrencyDto[]> {
    return this.currencies$;
  }
}
