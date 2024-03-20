import {
  animate,
  AUTO_STYLE,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import {
  AsyncPipe,
  NgClass,
  NgForOf,
  NgIf,
  NgOptimizedImage,
  NgStyle,
} from '@angular/common';
import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  Output,
  ViewChild,
} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {combineLatestWith, map, Observable, startWith, tap} from 'rxjs';

import {
  CurrencyApiService,
  type CurrencyDto,
} from '../../services/api/currency-api/currency-api.service';

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
  animations: [
    trigger('fade', [
      state('false', style({visibility: 'hidden', opacity: 0})),
      state('true', style({visibility: AUTO_STYLE, opacity: AUTO_STYLE})),
      transition('false => true', animate('150ms ease-in')),
      transition('true => false', animate('150ms ease-out')),
    ]),
  ],
  templateUrl: './currency-selector.component.html',
  styleUrl: './currency-selector.component.scss',
})
export class CurrencySelectorComponent {
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
        this.filterCurrencies(currencies, filterString)
      ),
      tap((filteredCurrencies) => {
        this.firstFilteredCurrency = filteredCurrencies[0];
      })
    );
  }

  filterCurrencies(
    currencies: CurrencyDto[],
    filterString: string
  ): CurrencyDto[] {
    const filter = filterString.toLowerCase();
    return currencies.filter(
      (currency) =>
        currency.code.toLowerCase().includes(filter) ||
        currency.fullName.toLowerCase().includes(filter)
    );
  }

  onCurrencySelect(currency: CurrencyDto): void {
    this.currency = currency;
    this.currencySelected.emit(currency);
    this.onBlur();
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
      behavior: 'instant',
    });
  }

  get isDisabled(): boolean {
    return this.currency.code === 'EUR';
  }

  onFocus(): void {
    this.isFocused = true;
  }

  onBlur(): void {
    this.clearQuery();
    this.currencyInput.nativeElement.blur();
    this.isFocused = false;
  }

  clearQuery(): void {
    this.filter.setValue('');
  }
}
