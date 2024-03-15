import { Routes } from '@angular/router';
import {ConverterComponent} from "./converter/converter.component";
import {ChartComponent} from "./chart/chart.component";

export const routes: Routes = [
  { path: 'chart', component: ChartComponent },
  { path: 'converter', component: ConverterComponent },
  { path: '', redirectTo: '/chart', pathMatch: 'full' },
];
