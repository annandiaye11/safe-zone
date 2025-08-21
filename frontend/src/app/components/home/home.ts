import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Product} from '../../entity/Product';
import {NgForOf, NgIf} from '@angular/common';
import {RouterLink} from '@angular/router';
import {environment} from '../../../environments/environment.development';
import {ProductService} from '../../services/product.service';
import {MediaService} from '../../services/media.service';

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
    constructor(private productService: ProductService, private mediaService : MediaService) {}
    currentIndexes: { [key: string]: number } = {};
    ngOnInit() {
        this.productService.getAllProducts().subscribe({
            next: (data: Product[]) => {
                this.products = data;
                this.products.forEach(product => {
                    this.mediaService.getMediaByProduitId(product.id!).subscribe({
                        next: (data: any) => {
                            console.log(`Media for produit ${product.name}`, data)
                            product.images = data.media;
                            console.log('Produit:', product);
                        },
                        error: (err) => {
                            console.log("erreur lors de la recuperation des media")
                        }
                    })
                })
                this.products.forEach(p => {
                    if (p.id !== null) {
                        this.currentIndexes[p.id] = 0;                    }
                });

            },
            error: (err) => {
                console.error('Erreur lors de la récupération des produits', err);
            }
        });
    }




    prevImage(productId: string) {
        const product = this.products.find(p => p.id === productId);
        if (!product) return;

        if (this.currentIndexes[productId] === 0) {
           this.currentIndexes[productId] = product.images!.length - 1;
        } else {
            this.currentIndexes[productId]--;
        }
    }

    nextImage(productId: string) {
        const product = this.products.find(p => p.id === productId);
        if (!product) return;

        if (this.currentIndexes[productId] === product.images!.length - 1) {
            this.currentIndexes[productId] = 0;
        } else {
            this.currentIndexes[productId]++;
        }
    }


}
