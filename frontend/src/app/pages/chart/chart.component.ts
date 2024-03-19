import {AsyncPipe, NgClass, NgIf} from '@angular/common';
import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {BaseChartDirective} from 'ng2-charts';

import {CurrencySelectorComponent} from '../../components/currency-selector/currency-selector.component';
import {LineChartComponent} from '../../components/charts/line-chart.component';
import {LoaderComponent} from '../../components/loader/loader.component';
import {CurrencyDto} from '../../services/api/currency-api/currency-api.service';
import {
  ChartPeriodType,
  ChartPointDto,
  MainApiService,
} from '../../services/api/main-api/main-api.service';

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
export class ChartComponent {
  chartPeriodTypeLabelMapping: Record<ChartPeriodType, string> = {
    [ChartPeriodType.YEAR]: '1Y',
    [ChartPeriodType.YTD]: 'YTD',
    [ChartPeriodType.MONTH]: '1M',
    [ChartPeriodType.ALL]: 'ALL',
  };
  chartPeriodTypes: ChartPeriodType[] = Object.values(ChartPeriodType);
  selectedChartPeriod: ChartPeriodType = ChartPeriodType.ALL;

  currentChangePercent: number | undefined;
  currentChartPoints: ChartPointDto[] = [];

  loading: boolean = true;
  currentCurrency: CurrencyDto | undefined;

  constructor(private mainApiService: MainApiService) {}

  currencySelected(currency: CurrencyDto) {
    this.currentCurrency = currency;
    this.selectedChartPeriod = ChartPeriodType.ALL;
    this.fetchHistoricalChartData(currency.code);
  }

  changePercentClassName(): string {
    const percent = this.currentChangePercent;
    if (!percent || percent === 0) {
      return 'neutral';
    }

    if (percent < 0) {
      return 'negative';
    }

    return 'positive';
  }

  onChartPeriodChange(chartPeriodType: ChartPeriodType) {
    if (this.currentCurrency && this.selectedChartPeriod !== chartPeriodType) {
      this.selectedChartPeriod = chartPeriodType;
      this.fetchHistoricalChartData(this.currentCurrency.code);
    }
  }

  fetchHistoricalChartData(currencyCode: string) {
    this.loading = true;
    this.mainApiService
      .fetchHistoricalChartData(currencyCode, this.selectedChartPeriod)
      .subscribe((chartData) => {
        this.loading = false;
        this.currentChangePercent = Number.parseFloat(chartData.changePercent);
        this.currentChartPoints = chartData.chartPoints;
      });
  }
}
