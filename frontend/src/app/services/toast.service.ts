import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {Toast} from '../entity/Toast';

@Injectable({
    providedIn: 'root'
})
export class ToastService {
    private toastsSubject = new BehaviorSubject<Toast[]>([]);
    public toasts$: Observable<Toast[]> = this.toastsSubject.asObservable()

    show(message: string, type: Toast['type'] = 'info', duration: number = 5000) {
        const toast: Toast = {
            id: this.generateId(),
            message,
            type,
            duration
        }

        const currentToasts = this.toastsSubject.value
        this.toastsSubject.next([...currentToasts, toast])

        if (duration > 0) {
            setTimeout(() => this.remove(toast.id), duration)
        }
    }

    remove(id: string) {
        const currentToasts = this.toastsSubject.value
        this.toastsSubject.next(currentToasts.filter(toast => toast.id !== id))
    }

    clear() {
        this.toastsSubject.next([])
    }

    success(message: string, duration?: number) {
        this.show(message, 'success', duration)
    }

    error(message: string, duration?: number) {
        this.show(message, 'error', duration)
    }

    info(message: string, duration?: number) {
        this.show(message, 'info', duration)
    }

    warning(message: string, duration?: number) {
        this.show(message, 'warning', duration)
    }

    private generateId(): string {
        return Math.random().toString(36).substring(2, 9)
    }
}
