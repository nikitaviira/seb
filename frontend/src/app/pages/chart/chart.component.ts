import {AsyncPipe, NgClass, NgIf} from '@angular/common';
import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {BaseChartDirective} from 'ng2-charts';

import {LineChartComponent} from '../../components/charts/line-chart.component';
import {CurrencySelectorComponent} from '../../components/currency-selector/currency-selector.component';
import {LoaderComponent} from '../../components/loader/loader.component';
import type {CurrencyDto} from '../../services/api/currency-api/currency-api.service';
import {ChartPeriodType, type ChartPointDto, MainApiService} from '../../services/api/main-api/main-api.service';

@Component({
  selector: 'app-chart',
  standalone: true,
  imports: [
    NgIf,
    AsyncPipe,
    FormsModule,
    BaseChartDirective,
    NgClass,
    LoaderComponent,
    CurrencySelectorComponent,
    LineChartComponent,
  ],
  templateUrl: './chart.component.html',
  styleUrl: './chart.component.scss',
})
export class ChartComponent implements OnInit {
  protected readonly chartPeriodTypeLabelMapping: Record<ChartPeriodType, string> = {
    [ChartPeriodType.YEAR]: '1Y',
    [ChartPeriodType.YTD]: 'YTD',
    [ChartPeriodType.MONTH]: '1M',
    [ChartPeriodType.ALL]: 'ALL',
  };
  protected readonly chartPeriodTypes: ChartPeriodType[] = Object.values(ChartPeriodType);

  protected chartPeriod: ChartPeriodType = ChartPeriodType.ALL;
  protected currency: CurrencyDto = {code: 'USD', fullName: 'US Dollar'};

  protected loading: boolean = true;
  protected changePercent: number | undefined;
  protected chartPoints: ChartPointDto[] = [];

  constructor(private mainApiService: MainApiService) {}

  ngOnInit(): void {
    this.currencySelected(this.currency);
  }

  currencySelected(currency: CurrencyDto): void {
    this.currency = currency;
    this.chartPeriod = ChartPeriodType.ALL;
    this.fetchHistoricalChartData(currency.code);
  }

  changePercentClassName(): string {
    const percent = this.changePercent;
    if (!percent || percent === 0) {
      return 'neutral';
    }

    if (percent < 0) {
      return 'negative';
    }

    return 'positive';
  }

  onChartPeriodChange(chartPeriodType: ChartPeriodType): void {
    if (this.currency && this.chartPeriod !== chartPeriodType) {
      this.chartPeriod = chartPeriodType;
      this.fetchHistoricalChartData(this.currency.code);
    }
  }

  private fetchHistoricalChartData(currencyCode: string): void {
    this.loading = true;
    this.mainApiService.fetchHistoricalChartData(currencyCode, this.chartPeriod).subscribe({
      next: (chartData) => {
        this.loading = false;
        this.changePercent = Number.parseFloat(chartData.changePercent);
        this.chartPoints = chartData.chartPoints;
      },
      error: () => {
        this.loading = false;
      },
    });
  }
}
