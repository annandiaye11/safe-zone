import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Role} from '../entity/Role';
import {Router} from '@angular/router';
import {JwtService} from './jwt.service';
import {map} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UtilsService {
    constructor(
        private router: Router,
        private jwtService: JwtService,
        private http: HttpClient,
    ) {}

    getHeaders(token: string) {
        return new HttpHeaders({
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        })
    }

    convertRole(role: string) {
        return role === "SELLER" ? Role.SELLER : Role.CLIENT
    }

    logout() {
        localStorage.removeItem('user-token')
        if (!localStorage.getItem("user-token")) {
            this.router.navigate(['/login']).then()
        }
    }

    isAuthenticated() {
        if (this.getToken().trim().length === 0) {
            this.removeToken()
            return false
        }

        if (this.jwtService.getExpirationTime(this.getToken()) < Date.now()) {
            this.removeToken()
            return false
        }

        return !!localStorage.getItem('user-token')
    }

    isSeller() {
        return this.jwtService.getUserRole(this.getToken()) === Role.SELLER
    }

    getToken() {
        const token = localStorage.getItem('user-token')

        return token === null ? "" : token
    }

    removeToken() {
        localStorage.removeItem('user-token')
    }

    publicIpAddress() {
        return this.http.get('https://free.freeipapi.com/api/json')
    }
}
