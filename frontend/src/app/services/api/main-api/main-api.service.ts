import {HttpParams} from '@angular/common/http';
import {Injectable, Injector} from '@angular/core';
import {catchError, Observable} from 'rxjs';

import {ApiServiceBase} from '../ApiServiceBase';
import {CurrencyDto} from '../currency-api/currency-api.service';

export interface ChartPointDto {
  date: string;
  value: string;
}

export interface ChartDto {
  chartPoints: ChartPointDto[];
  changePercent: string;
}

export interface ConversionResultDto {
  base: CurrencyDto;
  quote: CurrencyDto;
  amount: string;
  conversionRate: string;
  invertedConversionRate: string;
  conversionResult: string;
}

export interface ConversionRequestDto {
  base: string;
  quote: string;
  amount: string;
}

export enum ChartPeriodType {
  MONTH = 'MONTH',
  YTD = 'YTD',
  YEAR = 'YEAR',
  ALL = 'ALL',
}

@Injectable({
  providedIn: 'root',
})
export class MainApiService extends ApiServiceBase {
  constructor(injector: Injector) {
    super(injector);
  }

  fetchHistoricalChartData(
    currencyCode: string,
    chartPeriodType: ChartPeriodType
  ): Observable<ChartDto> {
    const params = new HttpParams().set('chartType', chartPeriodType);
    return this.get<ChartDto>(`${currencyCode}/historical-chart`, {
      params,
    }).pipe(
      catchError((error) => {
        throw error;
      })
    );
  }

  fetchConversionResult(
    body: ConversionRequestDto
  ): Observable<ConversionResultDto> {
    return this.post<ConversionResultDto>('convert', body).pipe(
      catchError((error) => {
        throw error;
      })
    );
  }
}
