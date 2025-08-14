import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Product} from '../../entity/Product';
import {NgForOf, NgIf} from '@angular/common';
import {RouterLink} from '@angular/router';

@Component({
    selector: 'app-home',
    imports: [
        FormsModule,
        RouterLink,
    ],
    templateUrl: './home.html',
    styleUrl: './home.scss'
})
export class Home implements OnInit {

    products: Product[] = [
        {
            id: "1",
            name: 'podium',
            description: 'Ma description',
            price: 299.99,
            quantity: 15,
            userId: "123"
        },
        {
            id: "2",
            name: 'podium',
            description: 'Ma description',
            price: 1299.99,
            quantity: 3,
            userId: "12334"
        },
        {
            id: "1",
            name: 'podium',
            description: 'Ma description',
            price: 299.99,
            quantity: 15,
            userId: "123"
        },
        {
            id: "2",
            name: 'podium',
            description: 'Ma description',
            price: 1299.99,
            quantity: 3,
            userId: "12334"
        },
        {
            id: "1",
            name: 'podium',
            description: 'Ma description',
            price: 299.99,
            quantity: 15,
            userId: "123"
        },
        {
            id: "2",
            name: 'podium',
            description: 'Ma description',
            price: 1299.99,
            quantity: 3,
            userId: "12334"
        },


    ];

    ngOnInit(): void {
        // Initialisation du composant
    }

}
