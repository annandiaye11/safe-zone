import {TestBed} from '@angular/core/testing';
import {Router} from '@angular/router';

import {AuthGuard} from './auth.guard';
import {UtilsService} from '../services/utils.service';

describe('AuthGuard', () => {
    let guard: AuthGuard;
    let utilsService: jasmine.SpyObj<UtilsService>;
    let router: jasmine.SpyObj<Router>;

    beforeEach(() => {
        const utilsServiceSpy = jasmine.createSpyObj('UtilsService', ['isAuthenticated']);
        const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

        TestBed.configureTestingModule({
            providers: [
                AuthGuard,
                { provide: UtilsService, useValue: utilsServiceSpy },
                { provide: Router, useValue: routerSpy }
            ]
        });

        guard = TestBed.inject(AuthGuard);
        utilsService = TestBed.inject(UtilsService) as jasmine.SpyObj<UtilsService>;
        router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
        router.navigate.and.returnValue(Promise.resolve(true));
    });

    it('should be created', () => {
        expect(guard).toBeTruthy();
    });

    it('should allow access when user is authenticated', () => {
        utilsService.isAuthenticated.and.returnValue(true);

        const result = guard.canActivate();

        expect(result).toBe(true);
        expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should deny access and redirect to login when user is not authenticated', () => {
        utilsService.isAuthenticated.and.returnValue(false);

        const result = guard.canActivate();

        expect(result).toBe(false);
        expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
});
