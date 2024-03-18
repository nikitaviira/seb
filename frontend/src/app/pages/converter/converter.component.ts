import { JsonPipe, NgClass, NgIf, NgOptimizedImage } from '@angular/common';
import {Component} from '@angular/core';
import {FormControl, ReactiveFormsModule, Validators} from '@angular/forms';

import {CurrencySelectorComponent} from '../../components/currency-selector/currency-selector.component';
import {CurrencyDto} from '../../services/currency-api/currency-api.service';
import { ConversionRequestDto, ConversionResultDto, MainApiService } from '../../services/main-api/main-api.service';

@Component({
  selector: 'app-converter',
  standalone: true,
  imports: [CurrencySelectorComponent, NgOptimizedImage, ReactiveFormsModule, NgIf, NgClass, JsonPipe],
  templateUrl: './converter.component.html',
  styleUrl: './converter.component.scss',
})
export class ConverterComponent {
  amountForm: FormControl;
  baseCurrency: CurrencyDto = {code: 'EUR', fullName: 'Euro'};
  quoteCurrency: CurrencyDto = {code: 'USD', fullName: 'US Dollar'};
  conversionResult: ConversionResultDto | undefined;

  constructor(private mainApiService: MainApiService) {
    this.amountForm = new FormControl('1', [
      Validators.required,
      Validators.pattern(/^\d{1,10}(\.\d{1,2})?$/),
    ]);
  }

  setBaseCurrency(baseCurrency: CurrencyDto): void {
    this.baseCurrency = baseCurrency;
  }

  setQuoteCurrency(quoteCurrency: CurrencyDto): void {
    this.quoteCurrency = quoteCurrency;
  }

  get amount(): string {
    return this.amountForm.value;
  }

  switchCurrencies(): void {
    const {baseCurrency} = this;
    this.baseCurrency = this.quoteCurrency;
    this.quoteCurrency = baseCurrency;
  }

  convert(): void {
    if (this.amountForm.valid) {
      const body: ConversionRequestDto = {
        base: this.baseCurrency.code,
        quote: this.quoteCurrency.code,
        amount: this.amount
      };
      this.fetchConversionResult(body);
    }
  }

  fetchConversionResult(body: ConversionRequestDto) {
    this.mainApiService
      .fetchConversionResult(body)
      .subscribe((conversionResult) => {
        this.conversionResult = conversionResult;
      });
  }
}
