import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {AuthStateService} from '../../../services/auth.state.service';

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
        private authState: AuthStateService,
        protected router: Router
    ) {
    }

    ngOnInit() {
        this.authState.isAuthenticated$.subscribe(value => {
            this.isAuthenticated = value;
        })
    }

    onLogout() {
        this.authState.logout()
    }

    onLogin() {
        this.router.navigate(['/login']).then()
    }

    onProfile() {
        this.router.navigate(['/profile']).then()
    }
}
