import {Injectable} from '@angular/core';
import {jwtDecode} from 'jwt-decode';
import {JwtPayLoad} from '../entity/JwtPayLoad';

@Injectable({
    providedIn: 'root'
})
export class JwtService {

    decodeToken(token: string) {
        try {
            return jwtDecode<JwtPayLoad>(token);
        } catch (error) {
            console.error("Invalid token", error);
            return jwtDecode<JwtPayLoad>("");
        }
    }

    getUserId(token: string) {
        const decoded: JwtPayLoad = this.decodeToken(token);
        return decoded.userId
    }

    getUserRole(token: string) {
        const decoded: JwtPayLoad = this.decodeToken(token);
        return decoded.role
    }

    getSubject(token: string) {
        const decoded: JwtPayLoad = this.decodeToken(token);
        return decoded.sub
    }

    getExpirationTime(token: string) {
        const decoded: JwtPayLoad = this.decodeToken(token);
        return decoded.exp * 1000
    }

    getIssuedAt(token: string) {
        const decoded: JwtPayLoad = this.decodeToken(token);
        return decoded.iat
    }
}
