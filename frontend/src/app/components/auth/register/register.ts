import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../services/auth.service';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Role} from '../../../entity/Role';
import {User} from '../../../entity/User';
import {UserService} from '../../../services/user.service';
import {ToastService} from '../../../services/toast.service';

@Component({
    selector: 'app-register',
    imports: [
        RouterLink,
        ReactiveFormsModule,
        // ToastComponent
    ],
    templateUrl: './register.html',
    styleUrl: './register.scss'
})
export class Register implements OnInit {
    registerFormData;
    selectedFile: File | null = null;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private route: Router,
        private userService: UserService,
        private toastService: ToastService,
    ) {
        this.registerFormData = this.fb.group({
            name: ['', [Validators.required]],
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]],
            role: ['', [Validators.required]],
            avatar: [null],
        })
    }

    onFileSelected(event: Event) {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length > 0) {
            this.selectedFile = input.files[0];
            console.log("Fichier sélectionné :", this.selectedFile);
        }
    }

    ngOnInit() {
        this.toastService.success("Page register chargée avec succes", 3000)
    }

    onSubmit() {
        console.log(this.registerFormData.value)
        const data: User = {
            id: '',
            name: this.registerFormData.value.name as string,
            email: this.registerFormData.value.email as string,
            password: this.registerFormData.value.password as string,
            role: this.registerFormData.value.role as Role,
            avatar: this.registerFormData.value.avatar ?? null,
        }

        if (this.registerFormData.invalid) return

        this.authService.register(data)?.subscribe(
            {
                next: (responses: any) => {
                    console.log(responses.response.id);
                    if (this.selectedFile != null) {
                        this.userService.updateAvatar(this.selectedFile, responses.response.id).subscribe({
                            next: (data: any) => {
                                this.toastService.success("Enregistre avec succes")
                                console.log(data)
                            },
                            error: (_) => {
                                this.toastService.error("Erreur lors de l'enregistrement de l'avatar: ")
                            }
                        })
                    }
                    this.registerFormData.reset()
                    this.route.navigate(['/login']).then()
                },
                error: (error) => {
                    this.toastService.error("Erreur lors de l'enregistrement: " + error.error.message)
                    console.log(error)
                }
            }
        )
    }
}
