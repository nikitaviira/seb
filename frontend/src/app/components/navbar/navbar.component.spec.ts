import { Location } from '@angular/common';
import {Component} from '@angular/core';
import {ComponentFixture, fakeAsync, TestBed, tick, waitForAsync} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { Router, RouterModule} from '@angular/router';

import {NavbarComponent} from './navbar.component';

@Component({ template: '' })
class MockComponent {}

fdescribe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;
  let router: Router;
  let location: Location;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [MockComponent],
      imports: [
        NavbarComponent,
        RouterModule.forRoot([
          { path: 'chart', component: MockComponent },
          { path: 'converter', component: MockComponent }
        ]),
        BrowserAnimationsModule
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    router.initialNavigation();
    fixture.detectChanges();
  });

  it('should have active class for converter link when corresponding route is active', fakeAsync(() => {
    fixture.whenStable().then(() => {
      const converterLink = fixture.debugElement.query(By.css('.links a[href="/converter"]'));
      expect(converterLink.nativeElement.classList.contains('active')).toBeFalse();

      converterLink.nativeElement.click();
      fixture.detectChanges();
      tick();

      expect(converterLink.nativeElement.classList.contains('active')).toBeTrue();
      expect(location.path()).toBe('/converter');
    });
  }));

  it('menu is collapsed on redirect', fakeAsync(() => {
    fixture.whenStable().then(() => {
      const toggleBtn = fixture.debugElement.query(By.css('.toggle-button'));
      toggleBtn.nativeElement.click();
      fixture.detectChanges();
      tick();

      // @ts-expect-error: protected access
      expect(component.collapsed).toBeFalse();

      const converterLink = fixture.debugElement.query(By.css('.links a[href="/converter"]'));
      converterLink.nativeElement.click();
      fixture.detectChanges();
      tick();

      // @ts-expect-error: protected access
      expect(component.collapsed).toBeTrue();
    });
  }));
});
