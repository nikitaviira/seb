import { Routes } from '@angular/router';
import {ConverterComponent} from "./pages/converter/converter.component";
import {ChartComponent} from "./pages/chart/chart.component";

export const routes: Routes = [
  { path: 'chart', component: ChartComponent },
  { path: 'converter', component: ConverterComponent },
  { path: '', redirectTo: '/chart', pathMatch: 'full' },
];
