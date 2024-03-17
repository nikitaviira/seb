import { Component } from '@angular/core';

import { CurrencySelectorComponent } from '../../components/currency-selector/currency-selector.component';
import { CurrencyDto } from '../../services/currency-api/currency-api.service';

@Component({
  selector: 'app-chart',
  standalone: true,
  imports: [
    CurrencySelectorComponent,
  ],
  templateUrl: './chart.component.html',
  styleUrl: './chart.component.scss'
})
export class ChartComponent {
  currentCurrency: CurrencyDto | undefined;

  currencySelected(currency: CurrencyDto) {
    this.currentCurrency = currency;
  }
}
