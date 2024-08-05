import { TestBed } from '@angular/core/testing';

import { LoginService } from './login.service';

describe('AutocadastroService', () => {
  let service: AutocadastroService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AutocadastroService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
