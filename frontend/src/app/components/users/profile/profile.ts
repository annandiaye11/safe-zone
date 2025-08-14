import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgClass} from '@angular/common';
import {RouterLink} from '@angular/router';
import {User} from '../../../entity/User';
import {Role} from '../../../entity/Role';

@Component({
    selector: 'app-profile',
    imports: [
        FormsModule,
        NgClass
    ],
    templateUrl: './profile.html',
    styleUrl: './profile.scss'
})
export class Profile {
    user: User = {
        id: '1',
        name: 'Marie Dubois',
        email: 'marie.dubois@email.com',
        password: '',
        role: Role.SELLER,
        avatar: '/assets/podium.jpg'
    };

    isEditing = false;
    formData: User = {...this.user};


    goBack() {
        // this.location.origin();
    }

    editProfile() {
        this.isEditing = true;
    }

    cancelEdit() {
        this.formData = {...this.user};
        this.isEditing = false;
    }

    saveProfile() {
        this.user = {...this.formData};
        this.isEditing = false;
        console.log('Profil sauvegardé', this.user);
        // Ajoute ici la logique d'API
    }

    handleAvatarChange(event: any) {
        const file = event.target.files?.[0];
        if (file) {
            if (file.size > 5 * 1024 * 1024) {
                alert('Le fichier doit faire moins de 5MB');
                return;
            }
            if (!file.type.startsWith('image/')) {
                alert('Veuillez sélectionner une image');
                return;
            }
            const reader = new FileReader();
            reader.onload = (e: any) => {
                this.formData.avatar = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    }
}
