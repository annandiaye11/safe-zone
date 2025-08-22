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
            return false
        }

        const parts = token.split(".")
        if (parts.length !== 3) {
            return false
        }

        const payload = JSON.parse(atob(parts[1]))
        console.log(payload)
        const expirationTime = payload.exp * 1000
        if (Date.now() > expirationTime) {
            localStorage.removeItem('user-token')
        }

        return true
    }

}

/*export const AuthGuard = () => {
    const token = localStorage.getItem('user-token')

    if (token !== null) {
        const tokenParts = token.split('.')
        if (tokenParts.length !== 3) {
            return false // Invalid token format
        }

        const payload = JSON.parse(atob(tokenParts[1]))
        const expirationTime = payload.exp * 1000 // Convert to milliseconds

        if (Date.now() > expirationTime) {
            localStorage.removeItem('user-token') // Token expired
            return false
        }

        return true // Token is valid and not expired
    }

    return localStorage.getItem('user-token') === null
}*/
