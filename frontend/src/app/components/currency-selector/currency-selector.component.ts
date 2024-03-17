import { AsyncPipe, NgClass, NgForOf, NgIf, NgOptimizedImage, NgStyle } from '@angular/common';
import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { combineLatestWith, map, Observable, startWith, tap } from 'rxjs';

import { CurrencyApiService, CurrencyDto } from '../../services/currency-api/currency-api.service';

@Component({
  selector: 'app-currency-selector',
  standalone: true,
  imports: [
    NgForOf,
    AsyncPipe,
    NgClass,
    NgIf,
    ReactiveFormsModule,
    NgStyle,
    NgOptimizedImage,
  ],
  templateUrl: './currency-selector.component.html',
  styleUrl: './currency-selector.component.scss'
})
export class CurrencySelectorComponent implements OnInit {
  @Input() currency: CurrencyDto = {code: 'USD', fullName: 'US Dollar'};
  @Output() currencySelected = new EventEmitter<CurrencyDto>();
  @ViewChild('currencyInput') currencyInput!: ElementRef<HTMLInputElement>;
  @ViewChild('currencyDropdown') currencyDropdown!: ElementRef<HTMLDivElement>;

  filteredCurrencies$: Observable<CurrencyDto[]>;
  firstFilteredCurrency: CurrencyDto | undefined;
  filter: FormControl;
  filter$: Observable<string>;
  isFocused: boolean = false;

  constructor(currencyApiService: CurrencyApiService) {
    this.filter = new FormControl('');
    this.filter$ = this.filter.valueChanges.pipe(startWith(''));
    this.filteredCurrencies$ = currencyApiService.currencies.pipe(
      combineLatestWith(this.filter$),
      map(([currencies, filterString]) =>
        currencies.filter((currency) =>
          currency.code.toLowerCase().startsWith(filterString.toLowerCase())
        )
      ),
      tap((arr) => {
        this.firstFilteredCurrency = arr[0];
      })
    );
  }

  ngOnInit(): void {
    this.currencySelected.emit(this.currency);
  }

  onCurrencySelect(currency: CurrencyDto): void {
    this.currency = currency;
    this.currencySelected.emit(currency);
    this.onBlur();
  }

  clearQuery(): void {
    this.filter.setValue('');
  }

  onEnter(): void {
    const {firstFilteredCurrency} = this;
    if (firstFilteredCurrency) {
      this.onCurrencySelect(firstFilteredCurrency);
    }
  }

  onInput(): void {
    this.currencyDropdown.nativeElement.scrollTo({
      top: 0,
      behavior: 'instant'
    });
  }

  onFocus(): void {
    this.isFocused = true;
  }

  onBlur(): void {
    this.clearQuery();
    this.currencyInput.nativeElement.blur();
    this.isFocused = false;
  }
}
