import {Media} from './Media';

export class Product {
    id: string | null = null;
    name!: string
    description!: string
    price!: number
    quantity!: number
    userId!: string
    images: Media[] | null = null;
}
