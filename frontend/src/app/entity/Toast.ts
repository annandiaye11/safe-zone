export class Toast {
    id!: string;
    message!: string;
    type!: TypeMessage
    duration!: number;
}

export type TypeMessage = 'success' | 'error' | 'info' | 'warning'
