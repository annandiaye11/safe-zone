import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment.development';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Media} from '../entity/Media';
import {UtilsService} from './utils.service';

@Injectable({
    providedIn: 'root'
})
export class MediaService {
    token = "";
    apiUrl = environment.apiURL + '/media';

    constructor(
        private http: HttpClient,
        private utilService: UtilsService
    ) {
    }

    getHeaders(token: string) {
        return new HttpHeaders({
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        });
    }

    saveMedia(file: File[], productId: string): Observable<Media> {
        this.token = localStorage.getItem('user-token')!;
        const headers = new HttpHeaders({
            Authorization: `Bearer ${this.token}`
        });
        const formData = new FormData();
        file.forEach(f => formData.append("imagePath", f));
        formData.append("productId", productId);
        return this.http.post<Media>(`${this.apiUrl}`, formData, {headers});
    }

    getMediaByProduitId(productId: string): Observable<any> {
        this.token = localStorage.getItem('user-token')!;
        const headers = new HttpHeaders({
            Authorization: `Bearer ${this.token}`
        });
        return this.http.get<any>(`${this.apiUrl}/product/${productId}`, {headers});
    }

    deleteMedia(id: string): Observable<any> {
        this.token = localStorage.getItem('user-token')!;
        const headers = new HttpHeaders({
            Authorization: `Bearer ${this.token}`
        });
        return this.http.delete<any>(`${this.apiUrl}/${id}`, {headers});
    }


}
