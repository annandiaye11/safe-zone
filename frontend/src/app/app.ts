import {Component, OnInit} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {Sidebar} from './components/layouts/sidebar/sidebar';
import {UtilsService} from './services/utils.service';
import {Header} from './components/layouts/header/header';

@Component({
  selector: 'app-root',
    imports: [RouterOutlet, Sidebar, Header],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
    protected title = 'frontend';
    protected isAuthenticated: boolean = false;

    constructor(
        private router: Router,
        private utilService: UtilsService,
        private jwtService: UtilsService,
    ) {}

    ngOnInit() {
        this.isAuthenticated = this.utilService.isAuthenticated()
        this.showSidebar()
        this.showHeader()
    }

     hideNavbar(): boolean {
        const hisNav = ['/login', '/register'];
        return hisNav.includes(this.router.url);
    }

    showSidebar() {
        return this.isAuthenticated && this.jwtService.isSeller()
    }

    showHeader() {
        return this.isAuthenticated && !this.jwtService.isSeller()
    }
}
