import {Component, OnDestroy} from '@angular/core';
import {NavigationStart, Router, RouterLink, RouterLinkActive} from "@angular/router";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {animate, AUTO_STYLE, state, style, transition, trigger} from "@angular/animations";
import {filter} from "rxjs";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    NgOptimizedImage,
    NgIf,
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
  animations: [
    trigger('collapse', [
      state('false', style({ height: AUTO_STYLE, visibility: AUTO_STYLE })),
      state('true', style({ height: '0', visibility: 'hidden' })),
      transition('false => true', animate('300ms ease-in')),
      transition('true => false', animate('300ms ease-out'))
    ])
  ]
})
export class NavbarComponent implements OnDestroy {
  collapsed: boolean = true;

  constructor(private router: Router) { }

  private sub = this.router.events
    .pipe(filter(event => event instanceof NavigationStart))
    .subscribe(_ => {
      if (!this.collapsed) {
        this.toggleCollapse();
      }
    });

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  toggleCollapse() {
    this.collapsed = !this.collapsed;
  }
}
