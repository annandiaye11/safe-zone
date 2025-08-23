import {CanActivate} from '@angular/router';
import {UtilsService} from '../services/utils.service';
import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class AuthorizationGuard implements CanActivate {

    constructor(
        private utilsService: UtilsService
    ) {}

    canActivate() {
        console.log("Am I seller ?: ", this.utilsService.isAuthenticated() && this.utilsService.isSeller())
        return this.utilsService.isAuthenticated() && this.utilsService.isSeller()
    }
}
