import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MockComponent} from 'ng-mocks';
import {of} from 'rxjs';

import {LineChartComponent} from '../../components/charts/line-chart.component';
import {CurrencySelectorComponent} from '../../components/currency-selector/currency-selector.component';
import {CurrencyApiService} from '../../services/api/currency-api/currency-api.service';
import {MainApiService} from '../../services/api/main-api/main-api.service';
import {ChartComponent} from './chart.component';

describe('ChartComponent', () => {
  let component: ChartComponent;
  let fixture: ComponentFixture<ChartComponent>;

  const currencies = [
    {code: 'USD', fullName: 'US Dollar'},
    {code: 'JPY', fullName: 'Japanese Yen'},
    {code: 'GBP', fullName: 'Pound Sterling'},
  ];

  const currencyApiServiceMock = {
    currencies: of(currencies),
  };

  const mainApiServiceMock = {
    fetchHistoricalChartData: jasmine.createSpy().and.returnValue(
      of({
        changePercent: 10,
        chartPoints: [],
      })
    ),
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        MockComponent(LineChartComponent),
        ReactiveFormsModule,
        CurrencySelectorComponent,
        BrowserAnimationsModule,
        ChartComponent,
      ],
      providers: [
        {provide: MainApiService, useValue: mainApiServiceMock},
        {provide: CurrencyApiService, useValue: currencyApiServiceMock},
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
