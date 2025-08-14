import {FormsModule} from "@angular/forms";
import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Product} from '../../../entity/Product';

@Component({
    selector: 'app-add',
    templateUrl: './add.html',
    imports: [
        FormsModule
    ],
    styleUrls: ['./add.scss']
})
export class Add implements OnInit {

    @Input() formData = {
        name: '',
        description: '',
        price: 0,
        quantity: 0
    };

    @Input() editingProduct: Product | null = null;
    @Input() isFormOpen = true;
    @Output() isFormOpenChange = new EventEmitter<boolean>();
    @Output() saveProduct = new EventEmitter<Product>();

    ngOnInit(): void {
        console.log('Données reçues du parent :', this.formData);
        console.log('Produit en édition :', this.editingProduct);
    }

    closeForm() {
        console.log('Fermeture du formulaire');
        this.isFormOpenChange.emit(false);
    }

    onSubmit() {
        if (this.validateForm()) {
            const productData: Product = {
                id: this.editingProduct?.id || this.generateId(),
                name: this.formData.name,
                description: this.formData.description,
                price: this.formData.price,
                quantity: this.formData.quantity,
                userId: 'seller1' // Mock user ID
            };

            console.log('Sauvegarde du produit :', productData);
            this.saveProduct.emit(productData);
            this.closeForm();
        }
    }

    private validateForm(): boolean {
        if (!this.formData.name.trim()) {
            alert('Le nom du produit est requis');
            return false;
        }
        if (!this.formData.description.trim()) {
            alert('La description est requise');
            return false;
        }
        if (this.formData.price <= 0) {
            alert('Le prix doit être supérieur à 0');
            return false;
        }
        if (this.formData.quantity < 0) {
            alert('La quantité ne peut pas être négative');
            return false;
        }
        return true;
    }

    private generateId(): string {
        return Date.now().toString() + Math.random().toString(36).substr(2, 9);
    }

    get isEditing(): boolean {
        return this.editingProduct !== null;
    }

    get modalTitle(): string {
        return this.isEditing ? 'Modifier le produit' : 'Ajouter un produit';
    }

    get submitButtonText(): string {
        return this.isEditing ? 'Modifier' : 'Ajouter';
    }
}
