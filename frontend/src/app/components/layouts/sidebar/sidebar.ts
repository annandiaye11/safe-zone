import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {UserService} from '../../../services/user.service';
import {AuthStateService} from '../../../services/auth.state.service';
import {Link} from '../../../entity/Link';
import {NgClass} from '@angular/common';

@Component({
    selector: 'app-sidebar',
    imports: [
        RouterLink,
        NgClass,
    ],
    templateUrl: './sidebar.html',
    styleUrl: './sidebar.scss'
})
export class Sidebar implements OnInit {
    activeLink = 'produits';
    isAuthenticated: boolean = false;
    user: any
    protected readonly Link = Link;

    constructor(
        private authState: AuthStateService,
        private userService: UserService,
        private router: Router,
    ) {
    }

    setActiveLink(linkName: Link): boolean {
        return this.router.url === linkName;
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
