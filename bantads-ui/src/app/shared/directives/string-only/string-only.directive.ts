import { Directive, HostListener, ElementRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Directive({
  selector: '[stringOnly]',
  providers:[{
    provide: NG_VALUE_ACCESSOR,
    useExisting: StringOnlyDirective,
    multi: true
  }]
})
export class StringOnlyDirective implements ControlValueAccessor{

  constructor(private el: ElementRef) { }

  onChange: any = () => {};
  onTouched: any = () => {};

  @HostListener('keyup', ['$event'])
  onKeyUp($event: any){
    let valor = $event.target.value;
    valor = valor.replace(/[^a-zA-Z\s]/g, '');
    $event.target.value = valor;
    this.onChange(valor);
  }

  registerOnChange(fn: any): void {
      this.onChange = fn;
  }
  registerOnTouched(fn: any): void {
      this.onTouched = fn;
  }
  writeValue(obj: any): void {
      this.el.nativeElement.value = obj;
  }
}
