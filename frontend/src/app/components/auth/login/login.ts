import {Component, EventEmitter, Output} from '@angular/core';
import {RouterLink} from '@angular/router';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../services/auth.service';
import {Auth} from '../../../entity/Auth';

@Component({
    selector: 'app-login',
    imports: [
        RouterLink,
        ReactiveFormsModule
    ],
    templateUrl: './login.html',
    styleUrl: './login.scss'
})
export class Login {
    @Output() isAuthenticated = new EventEmitter<boolean>()

    loginFormData;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService
    ) {
        this.loginFormData = this.fb.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });
    }

    onSubmit() {
        const data: Auth = {
            email: this.loginFormData.value.email as string,
            password: this.loginFormData.value.password as string,
        }
        if (!data) return
        this.authService.login(data)?.subscribe({
            next: (response: any) => {
                this.authService.saveToken(response.token)
                this.isAuthenticated.emit(this.authService.isAuthenticated());
            },
            error: (error) => {
                console.log(error)
            }
        })
    }
}
