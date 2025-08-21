import {Component, OnInit} from '@angular/core';
import {Product} from '../../../entity/Product';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {DecimalPipe, NgClass} from '@angular/common';
import {ProductService} from '../../../services/product.service';

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
    currentUserId = 'user1'; // Mock user
    quantity: number = 1;

    mockProducts: Product[] = [
        {
            id: '1',
            name: 'Smartphone Premium',
            description: 'Un smartphone haut de gamme avec écran OLED...',
            price: 899.99,
            quantity: 15,
            userId: 'user1',
            images: null,
        },
        {
            id: '2',
            name: 'Casque Audio Sans Fil',
            description: 'Casque audio premium avec réduction de bruit...',
            price: 249.99,
            quantity: 8,
            userId: 'user2',
            images: null
        }
    ];

    constructor(private route: ActivatedRoute, private router: Router, private productService: ProductService) {
    }

    get canEdit(): boolean {
        return this.product?.userId === this.currentUserId;
    }
    ngOnInit() {
        const id = this.route.snapshot.paramMap.get('id');
        this.productService.getProductById(id!).subscribe({
            next: (data: Product) => {
                this.product = data;
                this.editForm = {...this.product};
            },
            error: (err) => {
                console.error('Erreur lors de la récupération du produit', err);
            }
        })

    }

    enableEdit() {
        this.isEditing = true;
    }

    save() {
        if (this.editForm) {
            this.product = {...this.editForm};
            this.isEditing = false;
            console.log('Produit sauvegardé:', this.product);
        }
    }

    cancel() {
        this.editForm = this.product ? {...this.product} : null;
        this.isEditing = false;
    }

}
