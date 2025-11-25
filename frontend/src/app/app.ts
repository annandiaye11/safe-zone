import {Component, OnDestroy, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Sidebar} from './components/layouts/sidebar/sidebar';
import {UtilsService} from './services/utils.service';
import {Header} from './components/layouts/header/header';
import {Subscription} from 'rxjs';
import {AuthStateService} from './services/auth.state.service';
import {ToastComponent} from './components/toast/toast.component';

@Component({
    selector: 'app-root',
    imports: [RouterOutlet, Sidebar, Header, ToastComponent],
    templateUrl: './app.html',
    styleUrl: './app.scss'
})
export class App implements OnInit, OnDestroy {
    protected title = 'frontend';
    protected isAuthenticated: boolean = false;
    private routerSubscription: Subscription = new Subscription()

    constructor(
        private jwtService: UtilsService,
        private authState: AuthStateService
    ) {
    }

    ngOnInit() {
        this.authState.loadAuthState()
    }

    ngOnDestroy() {
        this.routerSubscription.unsubscribe()
    }

    showSidebar() {
        return this.jwtService.isSeller()
    }

    showHeader() {
        return !this.jwtService.isSeller()
    }
}
