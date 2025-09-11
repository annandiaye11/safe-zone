import {Injectable, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Role} from '../entity/Role';
import {Router} from '@angular/router';
import {JwtService} from './jwt.service';
import {Product} from '../entity/Product';

@Injectable({
    providedIn: 'root'
})
export class UtilsService implements OnInit {

    private readonly TOKEN_KEY = 'user-token';

    constructor(
        private router: Router,
        private jwtService: JwtService,
        private http: HttpClient,
    ) {
    }

    ngOnInit() {
        this.getToken()
        this.isAuthenticated()
        this.isSeller()
    }

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
        localStorage.removeItem(this.TOKEN_KEY)
        if (!localStorage.getItem(this.TOKEN_KEY)) {
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

        return !!localStorage.getItem(this.TOKEN_KEY)
    }

    isSeller() {
        return this.getToken().trim().length === 0 ? false : this.jwtService.getUserRole(this.getToken()) === Role.SELLER
    }

    getToken() {
        const token = localStorage.getItem(this.TOKEN_KEY)

        return token === null ? "" : token
    }

    removeToken() {
        localStorage.removeItem('user-token')
    }

    publicIpAddress() {
        return this.http.get('https://free.freeipapi.com/api/json')
    }

    prev(products: Product[], productId: string, currentIndexes: { [key: string]: number }) {
        const product = products.find(p => p.id === productId);
        if (!product || !product.images) return;

        if (currentIndexes[productId] === 0) {
            currentIndexes[productId] = product.images.length - 1;
        } else {
            currentIndexes[productId]--;
        }
    }

    next(products: Product[], productId: string, currentIndexes: { [key: string]: number }) {
        const product = products.find(p => p.id === productId);
        if (!product || !product.images) return;

        if (currentIndexes[productId] === product.images.length - 1) {
            currentIndexes[productId] = 0;
        } else {
            currentIndexes[productId]++;
        }
    }
}
