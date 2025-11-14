import {Routes} from '@angular/router';
import {Login} from './components/auth/login/login';
import {Home} from './components/home/home';
import {Register} from './components/auth/register/register';
import {Details} from './components/products/details/details';
import {Oops} from './components/errors/oops/oops';
import {Dashboard} from './components/users/dashboard/dashboard';
import {Profile} from './components/users/profile/profile';
import {AuthGuard} from './guards/auth.guard';
import {AuthorizationGuard} from './guards/authorization.guard';

export const routes: Routes = [
    {
        path: '',
        component: Home,
        title: "Catalogue"
    },
    {
        path: 'login',
        component: Login,
        title: "Sign In",
        canActivate: [AuthGuard]
    },
    {
        path: 'register',
        component: Register,
        title: "Sign Up",
        canActivate: [AuthGuard]
    },
    {
        path: 'dashboard',
        component: Dashboard,
        title: "Dashboard",
        canActivate: [AuthorizationGuard]
    },
    {
        path: 'details/:id',
        component: Details,
    },
    {
        path: 'profile',
        component: Profile,
        title: "Profile"
    },
    {
        // Redirect any other route to the Oops page
        path: "**",
        component: Oops,
        title: "Oops - Something went wrong"
    }
];
