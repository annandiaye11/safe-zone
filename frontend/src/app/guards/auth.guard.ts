import {CanActivate, Router} from '@angular/router';
import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(
        private router: Router
    ) {}

    canActivate() {
        const token = localStorage.getItem('user-token')

        if (token === null) {
            return true
        }

        const parts = token.split(".")
        if (parts.length !== 3) {
            return true
        }

        const payload = JSON.parse(atob(parts[1]))
        console.log(payload)
        const expirationTime = payload.exp * 1000
        if (Date.now() > expirationTime) {
            localStorage.removeItem('user-token')
            return true
        }

        return false
    }
}
