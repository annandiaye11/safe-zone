import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {AuthService} from '../../../services/auth.service';
import {UtilsService} from '../../../services/utils.service';
import {UserService} from '../../../services/user.service';
import {User} from '../../../entity/User';

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
    user: any

    constructor(
        private utilsService: UtilsService,
        private userService: UserService
    ) {}

    onLogout() {
        this.utilsService.logout()
    }

    ngOnInit() {
        this.isAuthenticated = this.utilsService.isAuthenticated()
        this.getProfile()
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
