import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Product} from '../../entity/Product';
import {NgForOf, NgIf} from '@angular/common';
import {RouterLink} from '@angular/router';
import {environment} from '../../../environments/environment.development';
import {ProductService} from '../../services/product.service';

@Component({
    selector: 'app-home',
    imports: [
        FormsModule,
        RouterLink,
    ],
    templateUrl: './home.html',
    styleUrl: './home.scss'
})
export class Home implements OnInit {
    products: Product[] = [];
    constructor(private productService: ProductService) {}
    ngOnInit() {
        this.productService.getAllProducts().subscribe({
            next: (data: Product[]) => {
                this.products = data;
                console.log('Produits:', data);
            },
            error: (err) => {
                console.error('Erreur lors de la récupération des produits', err);
            }
        });
    }

}
