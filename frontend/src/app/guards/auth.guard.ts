import {Injectable} from '@angular/core'
import {ActivatedRouteSnapshot, CanActivate, Router} from '@angular/router'
import {UtilsService} from '../services/utils.service'

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(
        private readonly utilsService: UtilsService,
        private readonly router: Router,
    ) {
    }

    // canActivate(): boolean {
    //     if (!this.utilsService.isAuthenticated()) {
    //         this.router.navigate(['/login']).then(r => console.log(r))
    //         return true
    //     }
    //
    //     return false
    // }
    canActivate(route: ActivatedRouteSnapshot): boolean {
        const isAuth = this.utilsService.isAuthenticated()

        const publicRoutes = ['login', 'register', '']

        const currentRoute = route.routeConfig?.path || ''

        if (publicRoutes.includes(currentRoute)) {
            return true
        }

        if (isAuth) {
            return true
        }

        this.router.navigate(['/login']).then(r => console.log(r))
        return false
    }
}
