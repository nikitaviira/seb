import 'chartjs-adapter-moment';

import {Component, Input, OnChanges, ViewChild} from '@angular/core';
import {ChartData, ChartOptions, ChartType} from 'chart.js';
import moment from 'moment/moment';
import {BaseChartDirective} from 'ng2-charts';

import {ChartPointDto} from '../../services/api/main-api/main-api.service';
import CrosshairPlugin from './crosshairPlugin';

@Component({
  selector: 'app-line-chart',
  standalone: true,
  imports: [BaseChartDirective],
  template: `
    <canvas
      baseChart
      [type]="chartType"
      [data]="lineChartData"
      [plugins]="[crosshairPlugin]"
      [legend]="legend"
      [options]="lineChartOptions">
    </canvas>
  `,
  styles: [
    `
      :host {
        height: 100%;
        display: block;
      }
    `,
  ],
})
export class LineChartComponent implements OnChanges {
  @ViewChild(BaseChartDirective, {static: true}) chart!: BaseChartDirective;
  @Input() chartPoints: ChartPointDto[] = [];

  protected readonly crosshairPlugin = CrosshairPlugin;
  protected readonly chartType: ChartType = 'line';
  protected readonly legend: boolean = false;

  protected readonly lineChartData: ChartData<'line'> = {
    datasets: [],
  };

  protected readonly lineChartOptions: ChartOptions = {
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
      crosshair: {
        color: 'black',
      },
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

  ngOnChanges(): void {
    this.setDataset(this.chartPoints);
  }

  private setDataset(chartPoints: ChartPointDto[]): void {
    this.lineChartData.datasets = [
      {
        data: chartPoints.map((point) => ({
          x: Number(new Date(point.date)),
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
