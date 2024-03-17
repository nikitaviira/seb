import {Component} from '@angular/core';

@Component({
  selector: 'app-loader',
  standalone: true,
  imports: [],
  template: `
    <div class="loader">
      <div class="bar"></div>
      <div class="bar"></div>
    </div>
  `,
  styleUrl: './loader.component.scss',
})
export class LoaderComponent {}
