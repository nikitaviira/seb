import {
  ComponentFixture,
  fakeAsync,
  TestBed,
  tick,
  waitForAsync,
} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {of} from 'rxjs';

import {CurrencySelectorComponent} from '../../components/currency-selector/currency-selector.component';
import {LoaderComponent} from '../../components/loader/loader.component';
import {CurrencyApiService} from '../../services/api/currency-api/currency-api.service';
import {
  ConversionRequestDto,
  MainApiService,
} from '../../services/api/main-api/main-api.service';
import {ConverterComponent} from './converter.component';

describe('ConverterComponent', () => {
  let component: ConverterComponent;
  let fixture: ComponentFixture<ConverterComponent>;
  const currencies = [
    {code: 'USD', fullName: 'US Dollar'},
    {code: 'JPY', fullName: 'Japanese Yen'},
    {code: 'GBP', fullName: 'Pound Sterling'},
  ];

  const currencyApiServiceMock = {
    currencies: of(currencies),
  };

  const mainApiServiceMock = {
    fetchConversionResult(body: ConversionRequestDto) {
      return of({
        base: {code: body.base, fullName: ''},
        quote: {code: body.quote, fullName: ''},
        amount: body.amount,
        conversionRate: '1',
        invertedConversionRate: '1',
        conversionResult: body.amount,
      });
    },
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        ConverterComponent,
        CurrencySelectorComponent,
        LoaderComponent,
        BrowserAnimationsModule,
      ],
      providers: [
        {provide: MainApiService, useValue: mainApiServiceMock},
        {provide: CurrencyApiService, useValue: currencyApiServiceMock},
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConverterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('form is initialized with EUR to USD rates and amount of 1', fakeAsync(() => {
    fixture.whenStable().then(() => {
      const submitBtn = fixture.debugElement.query(By.css('.submit-btn'));
      submitBtn.triggerEventHandler('mousedown', {
        target: submitBtn.nativeElement,
      });
      fixture.detectChanges();
      tick();

      // @ts-expect-error: protected access
      expect(component.conversionResult).toEqual({
        base: {code: 'EUR', fullName: ''},
        quote: {code: 'USD', fullName: ''},
        amount: '1',
        conversionRate: '1',
        invertedConversionRate: '1',
        conversionResult: '1',
      });
    });
  }));

  it('switcher should switch base and quote currencies', fakeAsync(() => {
    fixture.whenStable().then(() => {
      const switcherBtn = fixture.debugElement.query(By.css('.switcher'));
      switcherBtn.triggerEventHandler('mousedown', {
        target: switcherBtn.nativeElement,
      });
      fixture.detectChanges();
      tick();

      const submitBtn = fixture.debugElement.query(By.css('.submit-btn'));
      submitBtn.triggerEventHandler('mousedown', {
        target: submitBtn.nativeElement,
      });
      fixture.detectChanges();
      tick();

      // @ts-expect-error: protected access
      expect(component.conversionResult).toEqual({
        base: {code: 'USD', fullName: ''},
        quote: {code: 'EUR', fullName: ''},
        amount: '1',
        conversionRate: '1',
        invertedConversionRate: '1',
        conversionResult: '1',
      });
    });
  }));

  it('amount input should be validated', fakeAsync(() => {
    fixture.whenStable().then(() => {
      const input = fixture.debugElement.query(By.css('.amount-input'));
      input.nativeElement.value = 'not a number';
      input.triggerEventHandler('input', {target: input.nativeElement});
      fixture.detectChanges();
      tick();

      const submitBtn = fixture.debugElement.query(By.css('.submit-btn'));
      submitBtn.triggerEventHandler('mousedown', {
        target: submitBtn.nativeElement,
      });
      fixture.detectChanges();
      tick();

      // @ts-expect-error: protected access
      expect(component.conversionResult).toBeUndefined();

      const validationError =
        fixture.nativeElement.querySelector('.validation-error');
      expect(validationError.textContent).toEqual(
        'Please enter a valid amount'
      );
    });
  }));
});
