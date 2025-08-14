import {Component, OnInit} from '@angular/core';
import {Product} from '../../../entity/Product';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {DecimalPipe, NgClass} from '@angular/common';

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

    mockProducts: Product[] = [
        {
            id: '1',
            name: 'Smartphone Premium',
            description: 'Un smartphone haut de gamme avec écran OLED...',
            price: 899.99,
            quantity: 15,
            userId: 'user1'
        },
        {
            id: '2',
            name: 'Casque Audio Sans Fil',
            description: 'Casque audio premium avec réduction de bruit...',
            price: 249.99,
            quantity: 8,
            userId: 'user2'
        }
    ];

    constructor(private route: ActivatedRoute, private router: Router) {
    }

    get canEdit(): boolean {
        return this.product?.userId === this.currentUserId;
    }

    ngOnInit() {
        const id = this.route.snapshot.paramMap.get('id');
        const found = this.mockProducts.find(p => p.id === id);
        if (found) {
            this.product = {...found};
            this.editForm = {...found};
        }
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
