import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgClass} from '@angular/common';
import {User} from '../../../entity/User';
import {Role} from '../../../entity/Role';
import {UserService} from '../../../services/user.service';
import {routes} from '../../../app.routes';
import {Router} from '@angular/router';

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

    constructor(
        private userService: UserService,
        private router: Router
    ) {

    }
    formData : User = {
        id: '',
        name: '',
        email: '',
        password: '',
        role: Role.ROLE_CLIENT,
        avatar: null,
    }

    ngOnInit() {
         this.userService.getProfile().subscribe({
            next: (data: User)=> {
               this.user = data;
               this.user.role = this.userService.getRole(this.user.role)
                console.log("user", this.user)
                this.formData = {...this.user};
               this.formData.password = "ftkkeit"
             },
            error: (err) => {
                console.log("erreur ", err)
            }
        },)
    }

    getUserProfile() {

    }

    isEditing = false;



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
       this.userService.updateProfile(this.user).subscribe({
           next: (data: any) => {
               this.user = data.user;
               this.user.role = this.userService.getRole(this.user.role)
                console.log("user updated", this.user);
                this.user.password = "ftkkeit"
                this.cancelEdit();
           },
           error: (err) => {
               console.log("erreur ", err)
           }
       })

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
