import {FormsModule} from "@angular/forms";
import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Product} from '../../../entity/Product';
import {User} from '../../../entity/User';
import {UserService} from '../../../services/user.service';
import {NgStyle} from '@angular/common';
import {Media} from '../../../entity/Media';
import {ToastService} from '../../../services/toast.service';
import {MediaService} from '../../../services/media.service';

@Component({
    selector: 'app-add',
    templateUrl: './add.html',
    imports: [
        FormsModule,
        NgStyle
    ],
    styleUrls: ['./add.scss']
})
export class Add implements OnInit {
    @Input() formData: {
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

    @Input() editingProduct: Product | null = null;
    @Input() isFormOpen = true;
    @Input() media: Media | null = null;
    @Output() selectedFilesChange = new EventEmitter<File[]>();
    @Output() isFormOpenChange = new EventEmitter<boolean>();
    @Output() saveProduct = new EventEmitter<Product>();
    @Input() selectedFiles: File[] | null = [];
    @Input() mediaForDelete: string[] | null = [];
    @Output() mediaForDeleteChange = new EventEmitter<string[]>();
    user!: any
    protected readonly console = console;

    constructor(private userService: UserService, private toastService: ToastService, private mediaService: MediaService) {
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

    ngOnInit(): void {
        console.log('Données reçues du parent :', this.formData);
        console.log('Produit en édition :', this.editingProduct);
        console.log("Données reçues de selectedFiles", typeof this.selectedFiles![0])
        this.userService.getProfile().subscribe({
            next: (data: User) => {
                this.user = data;
            },
            error: (err) => {
                this.toastService.error("erreur lors de la recuperation des infos du user " + err)
            }
        },)
    }

    closeForm() {
        console.log('Fermeture du formulaire');
        this.selectedFiles = null;
        this.selectedFilesChange.emit([])
        this.isFormOpenChange.emit(false);
        this.editingProduct = null;
        this.formData = {id: null, name: '', description: '', price: 0, quantity: 0};
    }

    onSubmit() {
        console.log('product à enregitré', this.editingProduct);
        if (this.validateForm()) {
            const productData: Product = {
                id: this.editingProduct?.id ?? null,
                name: this.formData.name,
                description: this.formData.description,
                price: this.formData.price,
                quantity: this.formData.quantity,
                userId: this.user.id,
                images: null
            };

            console.log('Sauvegarde du produit :', productData);
            this.saveProduct.emit(productData);
            // this.closeForm();
        }
    }

    // Gestion de la sélection de fichiers
    onFileSelect(event: any) {
        const files = Array.from(event.target.files) as File[];

        // Limite à 3 fichiers max
        const maxFiles = 3;
        const remainingSlots = maxFiles - this.selectedFiles!.length;
        console.log("selectedFiles", this.selectedFiles)
        const filesToAdd = files.slice(0, remainingSlots);

        // Validation des types d'images
        const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
        const validFiles = filesToAdd.filter(file => {
            if (!allowedTypes.includes(file.type)) {
                this.toastService.warning(`${file.name} n'est pas un type d'image valide`);
                return false;
            }
            if (file.size > 5 * 1024 * 1024) {
                this.toastService.warning(`${file.name} est trop volumineux (max 5MB)`);
                return false;
            }
            return true;
        });

        this.selectedFiles = [...this.selectedFiles!, ...validFiles];
        const updatedMedia = {
            id: null,
            imagePath: this.selectedFiles[0],
            productId: ''
        };

        this.media = updatedMedia;
        console.log('Émission depuis enfant :', updatedMedia);
        this.selectedFilesChange.emit(this.selectedFiles);
        event.target.value = '';
    }

    // Supprimer un fichier
    removeFile(index: number): void {
        if (this.selectedFiles && this.selectedFiles.length > index) {
            if (typeof this.selectedFiles[index] != "object") {
                this.mediaForDelete?.push(this.editingProduct!.images![index]!.id!)
            }
            this.selectedFiles.splice(index, 1);
        }
    }

    // Générer aperçu du fichier
    getFilePreview(file: any): string | null {
        if (file instanceof File) {
            return URL.createObjectURL(file);
        }
        if (typeof file === 'string') {
            return file;
        }
        console.error("Type de fichier non supporté :", file);
        return null;
    }

    // Formater la taille du fichier
    formatFileSize(bytes: number): string {
        if (bytes === 0) return '0 B';
        const k = 1024;
        const sizes = ['B', 'KB', 'MB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
    }

    private validateForm(): boolean {
        if (!this.formData.name.trim()) {
            this.toastService.warning("Veuillez renseigner le nom du produit")
            return false;
        }
        if (!this.formData.description.trim()) {
            this.toastService.warning("Veuillez renseigner la description du produit")
            return false;
        }
        if (this.formData.price <= 0) {
            this.toastService.warning('Le prix doit être supérieur à 0')
            return false;
        }
        if (this.formData.quantity < 0) {
            this.toastService.warning('La quantité ne peut pas être négative')
            return false;
        }
        return true;
    }
}
