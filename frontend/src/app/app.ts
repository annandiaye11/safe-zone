import {Component} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {Sidebar} from './components/layouts/sidebar/sidebar';

@Component({
  selector: 'app-root',
    imports: [RouterOutlet, Sidebar],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
    protected title = 'frontend';
    constructor(private router: Router) {
    }

     hideNavbar(): boolean {
        const hisNav = ['/login', '/register'];
        return hisNav.includes(this.router.url);
    }
}
