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
import {Router} from '@angular/router';

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
export class Dashboard implements OnInit {
    products: Product[] = [];
    currentIndexes: { [key: string]: number } = {};
    isFormOpen = false;
    editingProduct: Product | null = null;
    media: Media | null = null;
    selectedFiles: File[] | null = [];
    mediaForDelete: string[] | null = [];
    mediaForSave: File[] | null = [];
    formData: {
        id: string | null;
        name: string;
        description: string;
        price: any;
        quantity: any;
    } = {
        id: null,
        name: '',
        description: '',
        price: 0,
        quantity: 0,
    };
    userId: string | null = null;

    constructor(private productService: ProductService,
                private router: Router,
                private mediaService: MediaService,
                private utilsService: UtilsService,
                private jwtService: JwtService,
                private toastService: ToastService) {
    }

    ngOnInit() {
        if (!this.utilsService.isAuthenticated()) {
            this.router.navigate(['/login']).then();
            return;
        }

        this.userId = this.jwtService.getUserId(this.utilsService.getToken());
        if (this.userId != null) this.loadProducts();
    }

    openAddForm() {
        this.editingProduct = null;
        this.formData = {id: null, name: '', description: '', price: 0, quantity: 0};
        this.isFormOpen = true;
    }

    onMediaForDelete(mediaIds: string[]) {
        console.log("")
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
            next: (data: any) => {
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
        if (typeof this.formData.quantity != "number" || typeof this.formData.price != "number") {
            this.toastService.warning("Le prix et la quantite doivent etre des nombres")
            return
        }
        if (this.formData.quantity <= 0 || this.formData.price <= 0) {
            this.toastService.warning("Le prix et la quantite doivent etre positifs")
            return
        }
        if (this.formData.quantity > 9999) {
            this.toastService.warning("La quantité ne doit pas être superieurs à 9999  ")
            return
        }
        if (this.formData.price > 9999999) {
            this.toastService.warning("Le prix ne doit pas etre superieurs à 9999999 ")
            return
        }
        if (this.editingProduct) {
            this.productService.saveOrUpdateProduct(product).subscribe({
                next: (data: any) => {
                    this.products = this.products.map(p => p.id === product.id ? data : p);
                    if (this.selectedFiles) this.mediaForSave = this.selectedFiles.filter(f => typeof f === "object");
                    this.selectedFiles = [];
                    this.saveMedia(this.mediaForSave, data.id!);
                    if (this.mediaForDelete) this.deleteMedia(this.mediaForDelete);
                    this.isFormOpen = false;
                },
                error: (err) => {
                    this.toastService.warning("erreur lors de la modification du produit: Veuillez verifier les informations")
                }
            })
        } else {
            for (let p of this.products) {
                if (p.name == product.name.trim()) {
                    this.toastService.error("Veuillez choisir un autre nom pour le produit, le nom du produit existe déjà");
                    return;
                }
            }
            this.productService.saveOrUpdateProduct(product).subscribe({
                next: (data: any) => {
                    if (this.selectedFiles) this.mediaForSave = this.selectedFiles;
                    this.selectedFiles = [];
                    this.saveMedia(this.mediaForSave, data.id!);
                },
                error: (err) => {
                    this.selectedFiles = []
                    this.toastService.error("erreur lors de l'enregistrement du produit")
                }
            })
        }
        this.editingProduct = null;
        this.formData = {id: null, name: '', description: '', price: 0, quantity: 0};
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
        if (files == null || files.length == 0) return;
        console.log("files recu", files)
        this.mediaService.saveMedia(files, productId).subscribe({
            next: (_) => {
                this.toastService.success("Produit enregistré")
                this.products = []
                this.loadProducts()
            },
            error: (_) => {
                this.toastService.error("erreur lors de l'enregistrement du produit")
            }
        })
    }

    deleteMedia(mediaIds: string[]) {
        for (let mediaId of mediaIds) {
            this.mediaService.deleteMedia(mediaId).subscribe({
                next: (_) => {
                    this.toastService.success("Media supprimé")
                    this.products = []
                    this.loadProducts()
                },
                error: (_) => {
                    this.toastService.error("erreur lors de la suppression du media")
                }

            })
        }


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
}
