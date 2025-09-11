import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Product} from '../../entity/Product';
import {RouterLink} from '@angular/router';
import {ProductService} from '../../services/product.service';
import {MediaService} from '../../services/media.service';
import {JwtService} from '../../services/jwt.service';
import {UtilsService} from '../../services/utils.service';
import {AuthStateService} from '../../services/auth.state.service';

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
    currentIndexes: { [key: string]: number } = {};

    isAuthenticated = false;
    user: any

    constructor(
        private productService: ProductService,
        private mediaService: MediaService,
        private jwtService: JwtService,
        private utilsService: UtilsService,
        private authState: AuthStateService
    ) {
    }


    ngOnInit() {
        // 🔹 écouter l’état d’authentification
        this.authState.isAuthenticated$.subscribe(value => {
            this.isAuthenticated = value;
        });
        // 🔹 écouter les infos utilisateur (utile si tu veux filtrer les produits par rôle plus tard)
        this.authState.user$.subscribe(user => {
            this.user = user;
        });

        // 🔹 charger les produits
        this.loadProducts();
    }

    prevImage(productId: string) {
        this.utilsService.prev(this.products, productId, this.currentIndexes)
    }

    nextImage(productId: string) {
        this.utilsService.next(this.products, productId, this.currentIndexes)
    }

    private loadProducts() {
        this.productService.getAllProducts().subscribe({
            next: (data: Product[]) => {
                this.products = data;

                // Récupérer les images pour chaque produit
                this.products.forEach(product => {
                    this.mediaService.getMediaByProduitId(product.id!).subscribe({
                        next: (data: any) => {
                            product.images = data.media;
                        },
                        error: (err) => {

                            console.error("Erreur lors de la récupération des media", err);
                        }
                    });
                });

                // Initialiser les index d’images
                this.products.forEach(p => {
                    if (p.id !== null) {
                        this.currentIndexes[p.id] = 0;
                    }
                });
            },
            error: (err) => {
                console.error('Erreur lors de la récupération des produits', err);
            }
        });
    }

}
