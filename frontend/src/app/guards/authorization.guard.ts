import {CanActivate, Router} from '@angular/router';
import {UtilsService} from '../services/utils.service';
import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class AuthorizationGuard implements CanActivate {

    constructor(
        private utilsService: UtilsService,
        private router: Router,
    ) {
    }

    canActivate() {
        if (!this.utilsService.isAuthenticated()) {
            this.router.navigate(['/login']).then()
        }

        return this.utilsService.isAuthenticated() && this.utilsService.isSeller()
    }
}
