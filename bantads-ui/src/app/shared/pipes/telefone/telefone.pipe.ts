import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'telefone'
})
export class TelefonePipe implements PipeTransform {

  transform(value: string | undefined): string {
    if (!value) {
      return '';
    }
    
    value = value.replace(/\D/g, '');
    return value.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1)$2-$3');
  }

}
