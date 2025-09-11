import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment.development';
import {Observable} from 'rxjs';
import {User} from '../entity/User';
import {Role} from '../entity/Role';

@Injectable({
    providedIn: 'root'
})
export class UserService {
    token = "";
    apiUrl = `${environment.apiURL}/users`

    constructor(
        private http: HttpClient
    ) {
    }

    getUserByToken(token: string) {
    }

    getRole(role: string): Role {
        return role == 'ROLE_SELLER' ? Role.SELLER : Role.CLIENT;
    }

    getProfile(): Observable<User> {
        this.token = localStorage.getItem('user-token')!;
        const headers = this.getHeaders(this.token);
        return this.http.get(`${this.apiUrl}/me`, {headers}) as Observable<User>;
    }

    updateProfile(user: User): Observable<any> {
        this.token = localStorage.getItem('user-token')!;
        const headers = this.getHeaders(this.token);
        return this.http.put(`${this.apiUrl}/${user.id}`, user, {headers}) as Observable<any>;
    }

    updateAvatar(file: File, userId: string): Observable<any> {
        this.token = localStorage.getItem('user-token')!;
        // const headers = new HttpHeaders({
        //     Authorization: `Bearer ${this.token}`
        // });
        const formData = new FormData();
        formData.append("imagePath", file);
        formData.append("userId", userId);
        return this.http.patch(`${this.apiUrl}/upload`, formData) as Observable<any>;
    }

    getHeaders(token: string) {
        return new HttpHeaders({
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        });
    }

    updatePassword(passwordData: { currentPassword: string, newPassword: string }, userId: string): Observable<any> {
        this.token = localStorage.getItem('user-token')!;
        const headers = this.getHeaders(this.token);
        return this.http.patch(`${this.apiUrl}/${userId}/changePassword`, passwordData, {headers});
    }
}
