import {HttpParams} from '@angular/common/http';
import {Injectable, Injector} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {catchError, Observable} from 'rxjs';

import {ApiServiceBase} from '../ApiServiceBase';
import type {CurrencyDto} from '../currency-api/currency-api.service';

export type ChartPointDto = {
  readonly date: string;
  readonly value: string;
}

export type ChartDto = {
  readonly chartPoints: ChartPointDto[];
  readonly changePercent: string;
}

export type ConversionResultDto = {
  readonly base: CurrencyDto;
  readonly quote: CurrencyDto;
  readonly amount: string;
  readonly conversionRate: string;
  readonly invertedConversionRate: string;
  readonly conversionResult: string;
}

export type ConversionRequestDto = {
  readonly base: string;
  readonly quote: string;
  readonly amount: string;
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
  constructor(
    injector: Injector,
    private toastService: ToastrService
  ) {
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
        this.showErrorToast(error);
        throw error;
      })
    );
  }

  fetchConversionResult(
    body: ConversionRequestDto
  ): Observable<ConversionResultDto> {
    return this.post<ConversionResultDto>('convert', body).pipe(
      catchError((error) => {
        this.showErrorToast(error);
        throw error;
      })
    );
  }

  private showErrorToast(error: any) {
    this.toastService.error(error.error?.message ?? 'Unexpected server error');
  }
}
