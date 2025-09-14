import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {UtilsService} from './utils.service';
import {UserService} from './user.service';

@Injectable({
    providedIn: 'root'
})
export class AuthStateService {

    private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
    isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

    private userSubject = new BehaviorSubject<any>(null);
    user$ = this.userSubject.asObservable();

    constructor(
        private utilsService: UtilsService,
        private userService: UserService,
    ) {
        this.loadAuthState();
    }

    updateUser(user: any) {
        this.userSubject.next(user);
    }

    loadAuthState() {
        const authenticated = this.utilsService.isAuthenticated();
        this.isAuthenticatedSubject.next(authenticated);

        if (authenticated) {
            this.userService.getProfile().subscribe({
                next: (data: any) => this.userSubject.next(data),
                error: (err) => this.userSubject.next(null)
            })
        } else {
            this.userSubject.next(null);
        }
    }

    logout() {
        this.utilsService.logout();
        this.isAuthenticatedSubject.next(false);
        this.userSubject.next(null);
    }
}
