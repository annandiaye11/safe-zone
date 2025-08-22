import {CanActivate, Router} from '@angular/router';
import {Injectable} from '@angular/core';
import {JwtService} from '../services/jwt.service';
import {UtilsService} from '../services/utils.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(
        private jwtService: JwtService,
        private utilsService: UtilsService
    ) {}

    canActivate() {
        const token = localStorage.getItem('user-token')

        if (token === null) {
            return true
        }

        const expirationTime = this.jwtService.getExpirationTime(token) * 1000

        if (Date.now() > expirationTime) {
            this.utilsService.removeToken()
            return true
        }

        return false
    }
}
