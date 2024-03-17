import 'chartjs-adapter-moment';

import {
  Component,
  Input,
  OnChanges,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import {ChartOptions} from 'chart.js';
import moment from 'moment/moment';
import {BaseChartDirective} from 'ng2-charts';

import {ChartPointDto} from '../../services/main-api/main-api.service';

@Component({
  selector: 'app-line-chart',
  standalone: true,
  imports: [BaseChartDirective],
  template: `
    <canvas
      baseChart
      [type]="'line'"
      [data]="lineChartData"
      [legend]="false"
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

  ngOnChanges(changes: SimpleChanges): void {
    const change = changes['chartPoints'];
    if (change) {
      this.setDataset(change.currentValue);
    }
  }

  setDataset(chartPoints: ChartPointDto[]): void {
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
