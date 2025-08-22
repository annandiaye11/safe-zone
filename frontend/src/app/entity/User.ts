import {Role} from './Role';

export class User {
    id!: string
    name!: string
    email!: string
    password!: string
    role!: Role
    avatar: File | null = null;
}

