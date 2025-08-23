import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgClass} from '@angular/common';
import {User} from '../../../entity/User';
import {Role} from '../../../entity/Role';
import {UserService} from '../../../services/user.service';
import {Router} from '@angular/router';
import {UtilsService} from '../../../services/utils.service';

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
    formData : User = {
        id: '',
        name: '',
        email: '',
        password: '',
        role: Role.CLIENT,
        avatar: null,
    }

    constructor(
        private userService: UserService,
        private utilservice: UtilsService,
        private router: Router
    ) {}

    ngOnInit() {
        if (!this.utilservice.isAuthenticated()) {
            this.router.navigate(['/login']).then();
            return;
        }

         this.userService.getProfile().subscribe({
            next: (data: User)=> {
               this.user = data;
               // this.user.role = this.userService.getRole(this.user.role)
                // console.log("user", this.user)
                //this.formData = {...this.user};
                this.formData = {...data};
               this.formData.password = "ftkkeit"
                // console.log("formData: ", this.formData)
             },
            error: (err) => {
                console.error("erreur ", err)
            }
        })

        //console.log("user agent", navigator.userAgent)
        //console.log("product", navigator.product)
        //console.log("product sub", navigator.productSub)
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
               // this.user.role = this.userService.getRole(this.user.role)
                // console.log("user updated", this.user);
                this.user.password = "ftkkeit"
                this.cancelEdit();
           },
           error: (err) => {
               console.error("erreur ", err)
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

    protected readonly Role = Role;
}
