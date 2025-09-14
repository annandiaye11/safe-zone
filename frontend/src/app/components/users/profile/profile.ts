import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgClass} from '@angular/common';
import {User} from '../../../entity/User';
import {Role} from '../../../entity/Role';
import {UserService} from '../../../services/user.service';
import {Router} from '@angular/router';
import {UtilsService} from '../../../services/utils.service';
import {ToastService} from '../../../services/toast.service';
import {AuthStateService} from '../../../services/auth.state.service';

@Component({
    selector: 'app-profile',
    imports: [
        FormsModule,
        NgClass
    ],
    templateUrl: './profile.html',
    styleUrl: './profile.scss'
})
export class Profile implements OnInit {

    user!: any
    isEditing = false;
    formData: User = {
        id: '',
        name: '',
        email: '',
        password: '',
        role: Role.CLIENT,
        avatar: null,
    }

    showPasswordForm = false;
    passwordData = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    };
    protected readonly Role = Role;

    constructor(
        private userService: UserService,
        private utilsService: UtilsService,
        private router: Router,
        private toastService: ToastService,
        private authState: AuthStateService,
    ) {
    }

    ngOnInit() {
        if (!this.utilsService.isAuthenticated()) {
            this.router.navigate(['/login']).then();
            return;
        }

        this.userService.getProfile().subscribe({
            next: (data: User) => {
                this.user = data;

                this.formData = {...data};
                this.formData.password = "ftkkeit"
                // console.log("formData: ", this.formData)
            },
            error: (err) => {
                this.toastService.error(err)
            }
        })
    }

    togglePasswordForm() {
        this.showPasswordForm = !this.showPasswordForm;
        if (this.showPasswordForm) {
            // Réinitialiser le formulaire quand on l'ouvre
            this.resetPasswordForm();
        }
    }

    cancelPasswordEdit() {
        this.showPasswordForm = false;
        this.resetPasswordForm();
    }

    resetPasswordForm() {
        this.passwordData = {
            currentPassword: '',
            newPassword: '',
            confirmPassword: ''
        };
    }

    updatePassword() {
        if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
            this.toastService.error('Les mots de passe ne correspondent pas');
            return;
        }

        if (this.passwordData.newPassword.length < 6) {
            this.toastService.error('Le nouveau mot de passe doit contenir au moins 6 caractères')
            return;
        }

        const passwordUpdateData = {
            currentPassword: this.passwordData.currentPassword,
            newPassword: this.passwordData.newPassword
        };

        this.userService.updatePassword(passwordUpdateData, this.user.id).subscribe({
            next: (response: any) => {
                this.toastService.success('Mot de passe modifié avec succès !')
                this.cancelPasswordEdit();
            },
            error: (error) => {
                this.toastService.error('Erreur lors de la modification du mot de passe : ' + error.error.message)

            }
        });
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
        this.userService.updateProfile(this.user).subscribe({
            next: (data: any) => {
                this.user = data.user;
                this.toastService.success("Utilisateur modifié")
                this.user.password = "ftkkeit"
                this.cancelEdit();

                this.authState.updateUser(this.user)
            },
            error: (err) => {
                this.toastService.error("Erreur lors de la modification de l'utilisateur" + err)
            }
        })

    }

    handleAvatarChange(event: any) {
        const file = event.target.files?.[0];
        if (file) {
            if (file.size > 5 * 1024 * 1024) {
                this.toastService.error('Le fichier doit faire moins de 5MB');
                return;
            }

            if (!file.type.startsWith('image/')) {
                this.toastService.error('Veuillez sélectionner une image')
                return;
            }

            const reader = new FileReader();
            reader.onload = (e: any) => {
                this.formData.avatar = e.target.result;

                const updatedUser = {...this.user, avatar: e.target.result};
                this.authState.updateUser(updatedUser)
            };

            reader.readAsDataURL(file);
        }
    }
}
