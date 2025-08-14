import { Component } from '@angular/core';
import {DecimalPipe, NgClass} from '@angular/common';
import {Product} from '../../../entity/Product';
import {FormsModule} from '@angular/forms';
import {Add} from '../../products/add/add';

@Component({
    selector: 'app-dashboard',
    imports: [
        DecimalPipe,
        NgClass,
        FormsModule,
        Add
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
        this.formData = {name: '', description: '', price: 0, quantity: 0};
        this.isFormOpen = true;
    }

    openEditForm(product: Product) {
        this.editingProduct = product;
        this.formData = {...product};
        this.isFormOpen = true;
    }

    deleteProduct(id: string) {
        this.products = this.products.filter(p => p.id !== id);
    }

    handleSaveProduct(product: Product) {
        if (this.editingProduct) {
            // Modification d'un produit existant
            const index = this.products.findIndex(p => p.id === product.id);
            if (index !== -1) {
                this.products[index] = product;
            }
        } else {
            // Ajout d'un nouveau produit
            this.products.push(product);
        }
        
        // Réinitialiser le formulaire
        this.editingProduct = null;
        this.formData = { name: '', description: '', price: 0, quantity: 0 };
        this.isFormOpen = false;
        
        console.log('Produit sauvegardé:', product);
    }

    getTotalValue(): number {
        return this.products.reduce((total, product) => total + (product.price * product.quantity), 0);
    }

    getLowStockCount(): number {
        return this.products.filter(product => product.quantity > 0 && product.quantity < 10).length;
    }
}
