import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subject, takeUntil} from 'rxjs';
import {ToastService} from '../../services/toast.service';
import {Toast} from '../../entity/Toast';

@Component({
    selector: 'app-toast',
    imports: [],
    templateUrl: './toast.component.html',
    styleUrl: './toast.component.scss'
})
export class ToastComponent implements OnInit, OnDestroy {

    toasts: Toast[] = [];
    private destroy$ = new Subject<void>();

    constructor(
        private toastService: ToastService,
    ) {
    }

    ngOnInit() {
        this.toastService.toasts$
            .pipe(takeUntil(this.destroy$))
            .subscribe((toasts: any) => {
                this.toasts = toasts;
            })
    }

    ngOnDestroy() {
        this.destroy$.next();
        this.destroy$.complete();
    }

    getIcon(type: Toast['type']): string {
        switch (type) {
            case 'success':
                return '✔️';
            case 'error':
                return '❌';
            case 'info':
                return 'ℹ️';
            case 'warning':
                return '⚠️';
            default:
                return 'ℹ️';
        }
    }

    trackByFn(_: number, toast: Toast) {
        return toast.id
    }

    removeToast(id: string) {
        this.toastService.remove(id)
    }
}
