import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {UtilsService} from '../services/utils.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(
        private readonly utilsService: UtilsService,
        private readonly router: Router,
    ) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
        if (this.utilsService.isAuthenticated()) {
            return true;
        }

        return this.router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
    }
}
