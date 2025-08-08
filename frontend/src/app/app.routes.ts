import {Routes} from '@angular/router';
import {Login} from './components/auth/login/login';
import {Home} from './components/home/home';
import {Register} from './components/auth/register/register';
import {Details} from './components/products/details/details';
import {Oops} from './components/errors/oops/oops';

export const routes: Routes = [
    {
        path: '',
        component: Home,
        title: "Catalogue"
    },
    {
        path: 'login',
        component: Login,
        title: "Sign In"
    },
    {
        path: 'register',
        component: Register,
        title: "Sign Up"
    },
    {
        path: 'details/:id',
        component: Details,
    },
    {
        // Redirect any other route to the Oops page
        path: "**",
        component: Oops,
        title: "Oops - Something went wrong"
    }
];
