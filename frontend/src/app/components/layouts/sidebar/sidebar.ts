import {Component, OnInit} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from "@angular/router";
import {AuthService} from '../../../services/auth.service';
import {UtilsService} from '../../../services/utils.service';
import {UserService} from '../../../services/user.service';
import {User} from '../../../entity/User';
import {AuthStateService} from '../../../services/auth.state.service';

@Component({
  selector: 'app-sidebar',
    imports: [
        RouterLink,
    ],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class Sidebar implements OnInit {
    activeLink = 'produits';
    isAuthenticated: boolean = false;
    user: any

    constructor(
        private authState: AuthStateService,
        private userService: UserService,
    ) {}

    setActiveLink(linkName: string): void {
        this.activeLink = linkName;
    }

    ngOnInit() {
        this.authState.isAuthenticated$.subscribe(value => {
            this.isAuthenticated = value;
        })

        this.authState.user$.subscribe(user => {
            this.user = user;
        })
    }

    onLogout() {
        this.authState.logout()
    }

    getProfile() {
        this.userService.getProfile().subscribe({
            next: (data: any) => {
                this.user = {...data};
            },
            error: (err) => {
                console.log("error", err)
            }
        })
    }
}
