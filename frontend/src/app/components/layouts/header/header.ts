import {Component, OnInit} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {AuthService} from '../../../services/auth.service';
import {UtilsService} from '../../../services/utils.service';
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
    ) {}

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
