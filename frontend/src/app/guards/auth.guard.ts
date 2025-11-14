import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
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

    canActivate(): boolean {
        if (this.utilsService.isAuthenticated()) return true

        this.router.navigate(['/login']).then(r => console.log(r))
        return false


    }
}
