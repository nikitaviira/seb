import {NgClass, NgIf, NgOptimizedImage} from '@angular/common';
import {Component} from '@angular/core';
import {FormControl, ReactiveFormsModule, Validators} from '@angular/forms';

import {CurrencySelectorComponent} from '../../components/currency-selector/currency-selector.component';
import {LoaderComponent} from '../../components/loader/loader.component';
import type {CurrencyDto} from '../../services/api/currency-api/currency-api.service';
import {ConversionRequestDto, ConversionResultDto, MainApiService} from '../../services/api/main-api/main-api.service';

@Component({
  selector: 'app-converter',
  standalone: true,
  imports: [CurrencySelectorComponent, NgOptimizedImage, ReactiveFormsModule, NgIf, NgClass, LoaderComponent],
  templateUrl: './converter.component.html',
  styleUrl: './converter.component.scss',
})
export class ConverterComponent {
  protected readonly amountForm: FormControl;
  protected baseCurrency: CurrencyDto = {code: 'EUR', fullName: 'Euro'};
  protected quoteCurrency: CurrencyDto = {code: 'USD', fullName: 'US Dollar'};
  protected conversionResult: ConversionResultDto | undefined;
  protected loading: boolean = false;

  constructor(private mainApiService: MainApiService) {
    this.amountForm = new FormControl('1', [
      Validators.required,
      Validators.pattern(/^(?!0+(\.0+)?$)\d{1,10}(\.\d{1,2})?$/),
    ]);
  }

  setBaseCurrency(baseCurrency: CurrencyDto): void {
    this.baseCurrency = baseCurrency;
  }

  setQuoteCurrency(quoteCurrency: CurrencyDto): void {
    this.quoteCurrency = quoteCurrency;
  }

  get isFormInvalid(): boolean {
    return this.amountForm.invalid && (this.amountForm.dirty || this.amountForm.touched);
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
    if (this.canSubmitForm()) {
      this.fetchConversionResult({
        base: this.baseCurrency.code,
        quote: this.quoteCurrency.code,
        amount: this.amount,
      });
    }
  }

  private canSubmitForm(): boolean {
    return (
      this.amountForm.valid &&
      (this.conversionResult?.base.code !== this.baseCurrency.code ||
        this.conversionResult?.quote.code !== this.quoteCurrency.code ||
        this.conversionResult?.amount !== this.amount)
    );
  }

  private fetchConversionResult(body: ConversionRequestDto): void {
    this.loading = true;
    this.mainApiService.fetchConversionResult(body).subscribe({
      next: (conversionResult) => {
        this.conversionResult = conversionResult;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }
}
