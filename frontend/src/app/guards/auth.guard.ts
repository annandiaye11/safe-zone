import {Injectable} from '@angular/core';
import {CanActivate} from '@angular/router';
import {UtilsService} from '../services/utils.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(
        private utilsService: UtilsService
    ) {
    }

    canActivate() {
        return !this.utilsService.isAuthenticated()
    }
}
