import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../services/auth.service';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Role} from '../../../entity/Role';
import {User} from '../../../entity/User';

@Component({
    selector: 'app-register',
    imports: [
        RouterLink,
        ReactiveFormsModule
    ],
    templateUrl: './register.html',
    styleUrl: './register.scss'
})
export class Register {
    registerFormData;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private route: Router
    ) {
        this.registerFormData = this.fb.group({
            name: ['', [Validators.required]],
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]],
            role: ['', [Validators.required]],
            avatar: [''],
        })
    }

    onSubmit() {
        console.log(this.registerFormData.value)

        const data: User = {
            id: '',
            name: this.registerFormData.value.name as string,
            email: this.registerFormData.value.email as string,
            password: this.registerFormData.value.password as string,
            role: this.registerFormData.value.role as Role,
            avatar: this.registerFormData.value.avatar as string,
        }

        if (this.registerFormData.invalid) return

        this.authService.register(data)?.subscribe(
            (response: any) => {
                this.registerFormData.reset()
                this.route.navigate(['/login']).then()
            }
        )
    }
}
