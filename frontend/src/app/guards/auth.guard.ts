import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {UtilsService} from '../services/utils.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(
        private utilsService: UtilsService,
        private router: Router,
    ) {
    }

    canActivate(): boolean {
        if (!this.utilsService.isAuthenticated()) {
            this.router.navigate(['/login']).then();
            return false;
        }

        return true;
    }
}
