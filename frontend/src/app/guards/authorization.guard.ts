import {CanActivate, Router} from '@angular/router';
import {UtilsService} from '../services/utils.service';
import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class AuthorizationGuard implements CanActivate {

    constructor(
        private readonly utilsService: UtilsService,
        private readonly router: Router,
    ) {
    }

    canActivate(): boolean {
        if (!this.utilsService.isAuthenticated()) {
            this.router.navigate(['/login']).then();
            return false;
        }

        return this.utilsService.isAuthenticated() && this.utilsService.isSeller();
    }
}
