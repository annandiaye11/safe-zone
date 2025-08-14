import { Component } from '@angular/core';
import {DecimalPipe} from '@angular/common';
import {Product} from '../../../entity/Product';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-dashboard',
    imports: [
        DecimalPipe,
        FormsModule
    ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard {
    products: Product[] = [
        {
            id: '1',
            name: 'Veste Premium',
            description: 'Veste élégante en laine mélangée',
            price: 189.99,
            quantity: 15,
            userId: 'seller1',
        },
        {
            id: '2',
            name: 'Chemise Classique',
            description: 'Chemise en coton premium',
            price: 79.99,
            quantity: 8,
            userId: 'seller1',
        },
    ];

    isFormOpen = false;
    editingProduct: Product | null = null;

    formData = {
        name: '',
        description: '',
        price: 0,
        quantity: 0
    };

    openAddForm() {
        this.editingProduct = null;
        this.formData = { name: '', description: '', price: 0, quantity: 0 };
        this.isFormOpen = true;
    }

    openEditForm(product: Product) {
        this.editingProduct = product;
        this.formData = { ...product };
        this.isFormOpen = true;
    }

    closeForm() {
        this.isFormOpen = false;
    }


    deleteProduct(id: string) {
        this.products = this.products.filter(p => p.id !== id);
    }
}
