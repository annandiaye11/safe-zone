import {Component, OnInit} from '@angular/core';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {AuthService} from '../../../services/auth.service';

@Component({
    selector: 'app-header',
    imports: [
        RouterLink
    ],
    templateUrl: './header.html',
    styleUrl: './header.scss'
})
export class Header implements OnInit {
    isAuthenticated: boolean = false;

    constructor(
        private authService: AuthService
    ) {
        this.isAuthenticated = this.authService.isAuthenticated();
    }

    onLogout() {
        this.authService.logout()
    }

    ngOnInit() {
        this.isAuthenticated = this.authService.isAuthenticated()
    }
}
