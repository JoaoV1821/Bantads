import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListarGerenteComponent } from './listar-gerente.component';

describe('ListarGerenteComponent', () => {
  let component: ListarGerenteComponent;
  let fixture: ComponentFixture<ListarGerenteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListarGerenteComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ListarGerenteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
