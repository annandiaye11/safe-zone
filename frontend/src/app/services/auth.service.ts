import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment.development';
import {HttpClient} from '@angular/common/http';
import {User} from '../entity/User';
import {Auth} from '../entity/Auth';
import {Router} from '@angular/router';
import {AuthStateService} from './auth.state.service';
import {UtilsService} from './utils.service';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    apiUrl = `${environment.apiURL}/auth`

    constructor(
        private http: HttpClient,
        private route: Router,
        private authState: AuthStateService,
        private utilsService: UtilsService
    ) {
    }

    login(credentials: Auth) {
        return this.http.post(`${this.apiUrl}/login`, credentials)
    }

    register(request: User) {
        return this.http.post(`${this.apiUrl}/register`, request)
    }

    logout() {
        localStorage.removeItem('user-token')
        if (!localStorage.getItem('user-token')) {
            this.route.navigate(['/login']).then()
        }
    }

    saveToken(token: string) {
        localStorage.setItem('user-token', token)
        if (localStorage.getItem('user-token')) {
            this.route.navigate(['/']).then()
        } else {
            console.log('error saving token')
        }
    }

    isAuthenticated() {
        return !!localStorage.getItem('user-token')
    }
}
