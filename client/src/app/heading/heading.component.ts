import { Component, HostBinding, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-heading',
  templateUrl: './heading.component.html',
  styleUrls: ['./heading.component.css']
})
export class HeadingComponent {

  @Input() level = 1;

  @HostBinding('class')
  get class() {
    return `level${this.level}`
  }
}
