import {Component, OnInit} from '@angular/core';
import {Product} from '../../../entity/Product';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {DecimalPipe, NgClass} from '@angular/common';
import {ProductService} from '../../../services/product.service';
import {MediaService} from '../../../services/media.service';
import {Media} from '../../../entity/Media';

@Component({
    selector: 'app-details',
    imports: [
        FormsModule,
        DecimalPipe,
        NgClass,
        RouterLink
    ],
    templateUrl: './details.html',
    styleUrl: './details.scss'
})
export class Details implements OnInit {
    product: Product | null = null;
    isEditing = false;
    editForm: Product | null = null;
    quantity: number = 1;
    currentImage: Media | null = null;

    constructor(private route: ActivatedRoute, private router: Router, private productService: ProductService, private mediaService: MediaService) {
    }

    ngOnInit() {
        const id = this.route.snapshot.paramMap.get('id');
        this.productService.getProductById(id!).subscribe({
            next: (data: Product) => {
                this.product = data;
                this.mediaService.getMediaByProduitId(this.product.id!).subscribe({
                    next: (data: any) => {
                        console.log(`Media for produit ${this.product!.name}`, data)
                        this.product!.images = data.media;
                        this.currentImage = data.media[0];
                        console.log('Produit:', this.product);
                    },
                    error: (err) => {
                        console.log("error lors de la recuperation des media")
                    }
                })
                this.editForm = {...this.product};
            },
            error: (err) => {
                console.error('Erreur lors de la récupération du produit', err);
            }
        })

    }

    selectImage(media: Media) {
        this.currentImage = media;
    }

    handleQuantityChange(event: any) {
        this.quantity = event.target.value;
    }

    handleEditFormSubmit() {
        this.product!.quantity = this.quantity;
        this.productService.saveOrUpdateProduct(this.product!).subscribe({
            next: (data: any) => {
            }
        })
    }
}
