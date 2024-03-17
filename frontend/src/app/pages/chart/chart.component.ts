import 'chartjs-adapter-moment';

import {AsyncPipe, NgClass, NgIf} from '@angular/common';
import {Component, ViewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ChartOptions} from 'chart.js';
import moment from 'moment';
import {BaseChartDirective} from 'ng2-charts';

import {CurrencySelectorComponent} from '../../components/currency-selector/currency-selector.component';
import {CurrencyDto} from '../../services/currency-api/currency-api.service';
import {
  ChartPeriodType,
  ChartPointDto,
  MainApiService,
} from '../../services/main-api/main-api.service';
import { LoaderComponent } from '../../components/loader/loader.component';

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
    CurrencySelectorComponent
  ],
  templateUrl: './chart.component.html',
  styleUrl: './chart.component.scss',
})
export class ChartComponent {
  @ViewChild(BaseChartDirective, {static: true}) chart!: BaseChartDirective;
  public lineChartData: any = {
    datasets: [],
  };

  public lineChartOptions: ChartOptions = {
    scales: {
      x: {
        type: 'time',
        ticks: {
          autoSkip: true,
          maxTicksLimit: 20,
          maxRotation: 0,
          color: 'black',
        },
        border: {
          color: 'black',
        },
      },
      y: {
        border: {
          color: 'black',
        },
        ticks: {
          color: 'black',
        },
      },
    },
    plugins: {
      tooltip: {
        callbacks: {
          title(tooltipItem: any) {
            return moment(tooltipItem[0].raw.x).format('MMMM DD YYYY');
          },
        },
      },
    },
    devicePixelRatio: 1,
    elements: {
      point: {
        radius: 0,
      },
    },
    interaction: {
      mode: 'index',
      intersect: false,
    },
    maintainAspectRatio: false,
  };

  chartPeriodTypeLabelMapping: Record<ChartPeriodType, string> = {
    [ChartPeriodType.YEAR]: '1Y',
    [ChartPeriodType.YTD]: 'YTD',
    [ChartPeriodType.MONTH]: '1M',
    [ChartPeriodType.ALL]: 'ALL',
  };
  chartPeriodTypes: ChartPeriodType[] = Object.values(ChartPeriodType);
  selectedChartPeriod: ChartPeriodType = ChartPeriodType.ALL;
  changePercent: number | undefined;

  loading: boolean = true;
  currentCurrency!: CurrencyDto;

  constructor(private mainApiService: MainApiService) {}

  currencySelected(currency: CurrencyDto) {
    this.currentCurrency = currency;
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

  onChartPeriodChange(chartPeriodType: ChartPeriodType) {
    this.selectedChartPeriod = chartPeriodType;
    this.fetchHistoricalChartData(this.currentCurrency.code);
  }

  fetchHistoricalChartData(currencyCode: string) {
    this.loading = true;
    this.mainApiService
      .fetchHistoricalChartData(currencyCode, this.selectedChartPeriod)
      .subscribe((chartData) => {
        this.loading = false;
        this.changePercent = Number.parseFloat(chartData.changePercent);
        this.setDataset(chartData.chartPoints);

      });
  }

  setDataset(chartPoints: ChartPointDto[]) {
    this.lineChartData.datasets = [
      {
        data: chartPoints.map((point) => ({
          x: new Date(point.date),
          y: Number.parseFloat(point.value),
        })),
        borderColor: '#45b400',
        fill: false,
        tension: 0.4,
        borderWidth: 2,
      },
    ];
    this.chart.render();
  }
}
