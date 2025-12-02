import { Directive, ElementRef, EventEmitter, OnInit, Output } from '@angular/core';

@Directive({
  selector: '[lazyLoad]',
  standalone: true
})
export class LazyLoadDirective implements OnInit {

  @Output() lazyLoad = new EventEmitter<void>();

  constructor(private el: ElementRef) {}

  ngOnInit() {
    const observer = new IntersectionObserver(([entry]) => {
      if (entry.isIntersecting) {
        this.lazyLoad.emit();
        observer.disconnect();
      }
    });

    observer.observe(this.el.nativeElement);
  }
}
