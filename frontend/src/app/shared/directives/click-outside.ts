import { Directive, ElementRef, EventEmitter, HostListener, Output } from '@angular/core';

@Directive({
  selector: '[clickOutside]',
  standalone: true
})
export class ClickOutsideDirective {

  @Output() clickOutside = new EventEmitter<void>();

  constructor(private elementRef: ElementRef) {}

  @HostListener('document:click', ['$event.target'])
  onClick(target: EventTarget | null) {

    // verificăm dacă target este un HTMLElement
    if (!(target instanceof HTMLElement)) {
      return;
    }

    const clickedInside = this.elementRef.nativeElement.contains(target);

    if (!clickedInside) {
      this.clickOutside.emit();
    }
  }
}
