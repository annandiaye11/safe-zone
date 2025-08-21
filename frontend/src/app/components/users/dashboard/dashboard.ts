import {Component, OnInit} from '@angular/core';
import {DecimalPipe, NgClass} from '@angular/common';
import {Product} from '../../../entity/Product';
import {FormsModule} from '@angular/forms';
import {Add} from '../../products/add/add';
import {ProductService} from '../../../services/product.service';
import {Media} from '../../../entity/Media';
import {MediaService} from '../../../services/media.service';

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
export class Dashboard implements OnInit{

    constructor(private productService: ProductService, private mediaService: MediaService) {}
    products: Product[] = [
    ];
    isFormOpen = false;
    editingProduct: Product | null = null;
    media: Media | null = null;
    selectedFiles: File[]  | null = [];
    formData: {
        id: string | null;
        name: string;
        description: string;
        price: number;
        quantity: number;
    } = {
        id: null,
        name: '',
        description: '',
        price: 0,
        quantity: 0,
    };

    ngOnInit(): void {
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
    openAddForm() {
        this.editingProduct = null;
        this.formData = {id: null ,name: '', description: '', price: 0, quantity: 0};
        this.isFormOpen = true;
    }

    onMediaFile(files: File[]) {
        console.log('Images', files);

    }
    openEditForm(product: Product) {
        console.log("produit a edit", product)
        this.editingProduct = product;
        this.formData = {
            id: product.id,
            name: product.name,
            description: product.description,
            price: product.price,
            quantity: product.quantity
        };
        this.isFormOpen = true;
    }

    deleteProduct(id: string) {
        this.productService.deleteProduct(id).subscribe({
            next: (data: any)=> {
                this.products = this.products.filter(p => p.id !== id);
            },
            error: (err) => {
                console.log("erreur lors de la suppression du produit", err)
            }
        })

    }

    handleSaveProduct(product: Product) {
        if (this.editingProduct) {
            this.productService.saveOrUpdateProduct(product).subscribe({
                next: (data: any)=> {
                  this.products = this.products.map(p => p.id === product.id ? data : p);
                  console.log(this.media);
                 },
                error: (err) => {
                    console.log("erreur lors de la modification du produit", err)
                }
            })
        } else {
            this.productService.saveOrUpdateProduct(product).subscribe({
                next: (data: any)=> {
                    console.log("data", data)
                    this.products.push(data);
                    if (this.selectedFiles) {
                        this.mediaService.saveMedia(this.selectedFiles, data.id).subscribe({
                            next: (data: any)=> {
                                console.log("Media enregistre", data)
                            },
                            error: (err) => {
                                console.log("erreur lors de l'enregistrement du media", err)
                            }
                        })
                    }
                },
                error: (err) => {
                    console.log("erreur lors de l'enregistrement du produit", err)
                }
            })
        }

        // Réinitialiser le formulaire
        this.editingProduct = null;
        this.formData = {id: null, name: '', description: '', price: 0, quantity: 0 };
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
