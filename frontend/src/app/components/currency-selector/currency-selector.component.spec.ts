import {
  ComponentFixture,
  fakeAsync,
  TestBed,
  tick,
  waitForAsync,
} from '@angular/core/testing';

import {CurrencySelectorComponent} from './currency-selector.component';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {CurrencyApiService} from '../../services/api/currency-api/currency-api.service';
import {of} from 'rxjs';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {By} from '@angular/platform-browser';

fdescribe('CurrencySelectorComponent', () => {
  let component: CurrencySelectorComponent;
  let fixture: ComponentFixture<CurrencySelectorComponent>;
  const currencies = [
    {code: 'USD', fullName: 'US Dollar'},
    {code: 'JPY', fullName: 'Japanese Yen'},
    {code: 'GBP', fullName: 'Pound Sterling'},
  ];

  const currencyApiServiceMock = {
    currencies: of(currencies),
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        ReactiveFormsModule,
        CurrencySelectorComponent,
      ],
      providers: [
        {provide: CurrencyApiService, useValue: currencyApiServiceMock},
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CurrencySelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.currency).toEqual({code: 'USD', fullName: 'US Dollar'});
    expect(component.filter.value).toEqual('');
    expect(component.isFocused).toEqual(false);
    component.filteredCurrencies$.subscribe((filteredCurrencies) => {
      expect(filteredCurrencies).toEqual(currencies);
      expect(component.firstFilteredCurrency).toEqual({
        code: 'USD',
        fullName: 'US Dollar',
      });
    });
  });

  it('should apply disabled class if currency is EUR', () => {
    component.currency = {code: 'EUR', fullName: 'Euro'};
    fixture.detectChanges();

    const currencySelectContainer = fixture.nativeElement.querySelector(
      '.currency-select-container'
    );
    expect(currencySelectContainer.classList.contains('disabled')).toBeTruthy();
  });

  it('filter should be applied on currency list', fakeAsync(() => {
    fixture.whenStable().then(() => {
      component.isFocused = true;
      const input = fixture.debugElement.query(By.css('input'));
      input.nativeElement.value = 'Pound';
      input.triggerEventHandler('input', {target: input.nativeElement});
      fixture.detectChanges();
      tick();

      const currencies = fixture.debugElement.queryAll(
        By.css('.currency-block')
      );
      expect(currencies).toHaveSize(1);

      const currencyElement = currencies[0];
      currencyElement.triggerEventHandler('click', {
        target: currencyElement.nativeElement,
      });
      tick();

      expect(component.currency).toEqual({
        code: 'GBP',
        fullName: 'Pound Sterling',
      });
    });
  }));

  it('when Enter key is pressed, first matching currency is selected', fakeAsync(() => {
    fixture.whenStable().then(() => {
      component.isFocused = true;
      const input = fixture.debugElement.query(By.css('input'));
      input.nativeElement.value = 'JP';
      input.triggerEventHandler('input', {target: input.nativeElement});
      fixture.detectChanges();
      tick();

      expect(component.filter.value).toBe('JP');

      input.triggerEventHandler('keydown.enter', {target: input.nativeElement});
      fixture.detectChanges();
      tick();

      expect(component.currency).toEqual({
        code: 'JPY',
        fullName: 'Japanese Yen',
      });
    });
  }));

  it('when Escape key is pressed, dropdown is closed and input is erased', fakeAsync(() => {
    fixture.whenStable().then(() => {
      component.isFocused = true;
      const input = fixture.debugElement.query(By.css('input'));
      input.nativeElement.value = 'JP';
      input.triggerEventHandler('input', {target: input.nativeElement});
      fixture.detectChanges();
      tick();

      expect(component.filter.value).toBe('JP');

      input.triggerEventHandler('keydown.escape', {
        target: input.nativeElement,
      });
      fixture.detectChanges();
      tick();

      expect(component.currency).toEqual({code: 'USD', fullName: 'US Dollar'});
      expect(component.filter.value).toBe('');
      expect(component.isFocused).toBeFalse();
    });
  }));

  it('when clean button is pressed, query is erased', fakeAsync(() => {
    fixture.whenStable().then(() => {
      component.isFocused = true;
      const input = fixture.debugElement.query(By.css('input'));
      input.nativeElement.value = 'JP';
      input.triggerEventHandler('input', {target: input.nativeElement});
      fixture.detectChanges();
      tick();

      expect(component.filter.value).toBe('JP');

      const cleanBtn = fixture.debugElement.query(By.css('.clear-button'));
      cleanBtn.triggerEventHandler('click', {target: cleanBtn.nativeElement});
      fixture.detectChanges();
      tick();

      expect(component.currency).toEqual({code: 'USD', fullName: 'US Dollar'});
      expect(component.filter.value).toBe('');
      expect(component.isFocused).toBeFalse();
    });
  }));

  it('when no results are found, "No results available" message is shown', fakeAsync(() => {
    fixture.whenStable().then(() => {
      component.isFocused = true;
      const input = fixture.debugElement.query(By.css('input'));
      input.nativeElement.value = 'random query';
      input.triggerEventHandler('input', {target: input.nativeElement});
      fixture.detectChanges();
      tick();

      expect(component.filter.value).toBe('random query');

      const noResultMsg = fixture.debugElement.query(By.css('.no-results'));
      expect(noResultMsg.nativeElement).toBeDefined();
    });
  }));
});
