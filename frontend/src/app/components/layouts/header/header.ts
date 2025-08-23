import {Component, OnInit} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {AuthService} from '../../../services/auth.service';
import {UtilsService} from '../../../services/utils.service';

@Component({
    selector: 'app-header',
    imports: [
        RouterLink
    ],
    templateUrl: './header.html',
    styleUrl: './header.scss'
})
export class Header implements OnInit {
    protected isAuthenticated: boolean = false;

    constructor(
        private utilService: UtilsService,
        private router: Router
    ) {}

    onLogout() {
        this.utilService.logout()
    }

    onLogin() {
        this.router.navigate(['/login']).then()
    }

    ngOnInit() {
        this.isAuthenticated = this.utilService.isAuthenticated()
    }
}
