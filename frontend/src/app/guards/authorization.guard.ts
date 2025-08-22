import {CanActivate} from '@angular/router';
import {JwtService} from '../services/jwt.service';
import {UtilsService} from '../services/utils.service';

export class AuthorizationGuard implements CanActivate {

    constructor(
        private jwtService: JwtService,
        private utilsService: UtilsService
    ) {}

    canActivate() {
        if (!this.utilsService.isAuthenticated()) {
        }

        const token = this.utilsService.getToken()
        const isTokenExpired = this.jwtService.getExpirationTime(token)

        return false
    }

}
