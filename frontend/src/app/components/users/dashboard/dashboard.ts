import {Component, OnInit} from '@angular/core';
import {DecimalPipe, NgClass} from '@angular/common';
import {Product} from '../../../entity/Product';
import {FormsModule} from '@angular/forms';
import {Add} from '../../products/add/add';
import {ProductService} from '../../../services/product.service';
import {Media} from '../../../entity/Media';
import {MediaService} from '../../../services/media.service';
import {UtilsService} from '../../../services/utils.service';
import {JwtService} from '../../../services/jwt.service';
import {ToastService} from '../../../services/toast.service';
import {ToastComponent} from '../../toast/toast.component';

@Component({
    selector: 'app-dashboard',
    imports: [
        DecimalPipe,
        NgClass,
        FormsModule,
        Add,
    ],
    templateUrl: './dashboard.html',
    styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit{
    constructor(private productService: ProductService,
                private mediaService: MediaService,
                private utilsService: UtilsService,
                private jwtService: JwtService,
                private toastService: ToastService) {}
    products: Product[] = [
    ];
    currentIndexes: { [key: string]: number } = {};
    isFormOpen = false;
    editingProduct: Product | null = null;
    media: Media | null = null;
    selectedFiles: File[]  | null = [];
    mediaForSave: File[]  | null = [];
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
    userId: string | null = null;

    ngOnInit() {
        this.userId = this.jwtService.getUserId(this.utilsService.getToken());
        if (this.userId != null) this.loadProducts();
    }

    private loadProducts() {
        this.productService.getAllProducts().subscribe({
            next: (data: Product[]) => {
                data.forEach(p => {
                    if (p.userId == this.userId) {
                        this.products.push(p);
                    }
                })
                this.products.forEach(product => {
                    this.mediaService.getMediaByProduitId(product.id!).subscribe({
                        next: (data: any) => {
                            console.log(`Media for produit ${product.name}`, data)
                            product.images = data.media;
                        },
                        error: (err) => {
                            console.error("Erreur lors de la récupération des media", err);
                        }
                    });
                });
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
        this.selectedFiles = product.images!.map(p => p.imagePath)
    }

    deleteProduct(id: string) {
        this.productService.deleteProduct(id).subscribe({
            next: (data: any)=> {
                this.products = this.products.filter(p => p.id !== id);
                this.toastService.info("Produit supprimé")
            },
            error: (err) => {
                console.log("erreur lors de la suppression du produit", err)
            }
        })

    }

    handleSaveProduct(product: Product) {
        if (this.selectedFiles?.length == 0) {
            this.toastService.warning("Veuillez mettre au moins une image")
            return
        }

        if (this.formData.name.trim() == "") {
            this.toastService.warning("Veuillez renseigner le nom du produit")
            return
        }
        if (this.formData.description.trim() == "") {
            this.toastService.warning("Veuillez renseigner la description du produit")
            return
        }
        if (this.formData.quantity <= 0 || this.formData.quantity <= 0) {
            this.toastService.warning("Le prix et la quantite doivent etre positifs")
            return
        }
        for (let p of this.products) {
            if (p.name == product.name.trim()) {
                this.toastService.warning("Veuillez choisir un autre nom pour le produit, le nom du produit existe déjà");
                return;
            }
        }
        if (this.editingProduct) {
            this.productService.saveOrUpdateProduct(product).subscribe({
                next: (data: any)=> {
                  this.products = this.products.map(p => p.id === product.id ? data : p);
                    if (this.selectedFiles) this.mediaForSave = this.selectedFiles;
                    this.selectedFiles = [];
                    this.saveMedia(this.mediaForSave, data.id!);                },
                error: (err) => {
                    this.toastService.warning("Le nom du produit existe deja, veuillez choisir un autre nom")
                    console.log("erreur lors de la modification du produit", err)
                }
            })
        } else {
            this.productService.saveOrUpdateProduct(product).subscribe({
                next: (data: any)=> {
                    if (this.selectedFiles) this.mediaForSave = this.selectedFiles;
                    this.selectedFiles = [];
                    this.saveMedia(this.mediaForSave, data.id!);
                },
                error: (err) => {
                    this.selectedFiles = []
                    console.log("erreur lors de l'enregistrement du produit", err)
                }
            })
        }
        this.editingProduct = null;
        this.formData = {id: null, name: '', description: '', price: 0, quantity: 0 };
    }

    getTotalValue(): number {
        return this.products.reduce((total, product) => total + (product.price * product.quantity), 0);
    }

    getLowStockCount(): number {
        return this.products.filter(product => product.quantity > 0 && product.quantity < 10).length;
    }
    prevImage(productId: string) {
        this.utilsService.prev(this.products, productId, this.currentIndexes)
    }

    nextImage(productId: string) {
        this.utilsService.next(this.products, productId, this.currentIndexes)
    }

    saveMedia(files: File[] | null, productId: string) {
        if (files == null) return;
        this.mediaService.saveMedia(files, productId).subscribe({
            next: (data: any)=> {
                this.toastService.success("Produit enregistré")
                this.products = []
                this.loadProducts()
            },
            error: (err) => {
                console.log("erreur lors de l'enregistrement du media", err)
            }
        })
    }
}
