import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment.development';
import {Observable} from 'rxjs';
import {User} from '../entity/User';

@Injectable({
  providedIn: 'root'
})
export class UserService {

    apiUrl = `${environment.apiURL}/users`

    constructor(
        private http: HttpClient
    ) {}

    getUserByToken(token: string) {}

    getProfile() {
        return this.http.get(`${this.apiUrl}/me`);
    }
}
