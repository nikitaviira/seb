import {HttpClientModule} from '@angular/common/http';
import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {provideRouter} from '@angular/router';
import {provideCharts, withDefaultRegisterables} from 'ng2-charts';
import {provideToastr} from 'ngx-toastr';

import {routes} from './app.routes';

const toastConfig = {
  preventDuplicates: true
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    importProvidersFrom([BrowserAnimationsModule, HttpClientModule]),
    provideCharts(withDefaultRegisterables()),
    provideToastr(toastConfig)
  ],
};
