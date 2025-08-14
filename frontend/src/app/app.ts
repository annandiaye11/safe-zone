import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {Header} from './components/layouts/header/header';
import {Footer} from './components/layouts/footer/footer';

@Component({
  selector: 'app-root',
    imports: [RouterOutlet, Header, Footer],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected title = 'frontend';
}
