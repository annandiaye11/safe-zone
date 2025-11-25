import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {RouterLink} from '@angular/router';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../services/auth.service';
import {Auth} from '../../../entity/Auth';
import {AuthStateService} from '../../../services/auth.state.service';
import {ToastService} from '../../../services/toast.service';
import {ToastComponent} from '../../toast/toast.component';

@Component({
    selector: 'app-login',
    imports: [
        RouterLink,
        ReactiveFormsModule,
        ToastComponent
    ],
    templateUrl: './login.html',
    styleUrl: './login.scss'
})
export class Login implements OnInit {
    @Output() isAuthenticated = new EventEmitter<boolean>()

    loginFormData;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private authState: AuthStateService,
        private toastService: ToastService
    ) {
        this.loginFormData = this.fb.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });
    }

    ngOnInit() {
        this.toastService.success("Page login chargée avec succes", 3000)
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
                this.authState.loadAuthState()
                this.toastService.success("Utilisateur connecte avec succes")
            },
            error: (_) => {
                this.toastService.error("Erreur: Ce utilisateur n'existe pas ou le mot de passe est incorrect !", 3000);

            }
        })
    }
}
