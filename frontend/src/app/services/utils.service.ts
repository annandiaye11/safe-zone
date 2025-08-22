import {Injectable} from '@angular/core';
import {HttpHeaders} from '@angular/common/http';
import {Role} from '../entity/Role';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class UtilsService {
    constructor(
        private router: Router
    ) {}

    getHeaders(token: string) {
        return new HttpHeaders({
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        })
    }

    convertRole(role: string) {
        return role === "ROLE_SELLER" ? Role.ROLE_SELLER : Role.ROLE_CLIENT
    }

    logout() {
        localStorage.removeItem('user-token')
        if (!localStorage.getItem("user-token")) {
            this.router.navigate(['/login']).then()
        }
    }

    session(token: string) {
        localStorage.setItem('user-token', token)
        if (localStorage.getItem('user-token')) {
            this.router.navigate(['/']).then()
        } else {
            console.log('error saving token')
        }
    }

    isAuthenticated() {
        return !!localStorage.getItem('user-token')
    }

    isSeller() {
        return this.isAuthenticated() && this.convertRole(localStorage.getItem('user-token')!) === Role.ROLE_SELLER
    }

    getToken() {
        if (!this.isAuthenticated()) return ""

        const token = localStorage.getItem('user-token')
        if (!token) return ""

        return token
    }

    removeToken() {
        if (!this.isAuthenticated()) return

        localStorage.removeItem('user-token')
        if (!localStorage.getItem('user-token')) {
            this.router.navigate(['/']).then()
        }
    }
}
