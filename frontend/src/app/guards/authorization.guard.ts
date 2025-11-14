import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
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

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
        const authenticated = this.utilsService.isAuthenticated();

        if (!authenticated) {
            return this.router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
        }

        return this.utilsService.isSeller();
    }
}
