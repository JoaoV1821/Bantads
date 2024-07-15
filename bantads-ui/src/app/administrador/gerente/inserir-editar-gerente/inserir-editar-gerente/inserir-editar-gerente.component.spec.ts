import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InserirEditarGerenteComponent } from './inserir-editar-gerente.component';

describe('InserirEditarGerenteComponent', () => {
  let component: InserirEditarGerenteComponent;
  let fixture: ComponentFixture<InserirEditarGerenteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InserirEditarGerenteComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(InserirEditarGerenteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
