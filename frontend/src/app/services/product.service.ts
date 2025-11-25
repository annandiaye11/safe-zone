import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment.development';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Product} from '../entity/Product';

@Injectable({
    providedIn: 'root'
})
export class ProductService {
    token = "";
    apiUrl = environment.apiURL + '/products';

    constructor(private http: HttpClient) {
    }

    getAllProducts(): Observable<Product[]> {
        return this.http.get<Product[]>(this.apiUrl);
    }

    getAllProductsByUserId(userId: string): Observable<Product[]> {
        return this.http.get<Product[]>(`${this.apiUrl}/${userId}/user`);
    }

    getProductById(id: string): Observable<Product> {
        return this.http.get<Product>(`${this.apiUrl}/${id}`);
    }

    getHeaders(token: string) {
        return new HttpHeaders({
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        });
    }

    saveOrUpdateProduct(product: Product): Observable<any> {
        this.token = localStorage.getItem('user-token')!;
        const headers = this.getHeaders(this.token);
        if (!product.id) {
            return this.http.post(`${this.apiUrl}`, product, {headers}) as Observable<any>;
        }
        console.log("enregistrement avec l'id", product.id)
        return this.http.put(`${this.apiUrl}/${product.id}`, product, {headers}) as Observable<any>;
    }

    deleteProduct(id: string): Observable<any> {
        this.token = localStorage.getItem('user-token')!;
        const headers = this.getHeaders(this.token);
        return this.http.delete(`${this.apiUrl}/${id}`, {headers}) as Observable<any>;
    }


}
