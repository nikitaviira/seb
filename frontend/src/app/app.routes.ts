import {Routes} from '@angular/router';

import {ChartComponent} from './pages/chart/chart.component';
import {ConverterComponent} from './pages/converter/converter.component';

export const routes: Routes = [
  {path: 'chart', component: ChartComponent},
  {path: 'converter', component: ConverterComponent},
  {path: '', redirectTo: '/chart', pathMatch: 'full'},
];
