import {Component, OnInit} from '@angular/core';
import {RouterLink} from "@angular/router";
import {AuthService} from '../../../services/auth.service';

@Component({
  selector: 'app-sidebar',
    imports: [
        RouterLink
    ],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class Sidebar implements OnInit {
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
