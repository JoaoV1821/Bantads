import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultarTop3Component } from './consultar-top3.component';

describe('ConsultarTop3Component', () => {
  let component: ConsultarTop3Component;
  let fixture: ComponentFixture<ConsultarTop3Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConsultarTop3Component]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ConsultarTop3Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
