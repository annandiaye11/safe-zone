import {Injectable} from '@angular/core';
import {CanActivate} from '@angular/router';
import {UtilsService} from '../services/utils.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(
        private utilsService: UtilsService
    ) {}

    canActivate() {
        console.log("Can I access to this page right now ?: ", !this.utilsService.isAuthenticated());
        return !this.utilsService.isAuthenticated()
    }
}
