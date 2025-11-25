import {TestBed} from '@angular/core/testing';
import {Router} from '@angular/router';

import {AuthorizationGuard} from './authorization.guard';
import {UtilsService} from '../services/utils.service';

describe('AuthorizationGuard', () => {
    let guard: AuthorizationGuard;
    let utilsService: jasmine.SpyObj<UtilsService>;
    let router: jasmine.SpyObj<Router>;

    beforeEach(() => {
        const utilsServiceSpy = jasmine.createSpyObj('UtilsService', ['isAuthenticated', 'isSeller']);
        const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

        TestBed.configureTestingModule({
            providers: [
                AuthorizationGuard,
                { provide: UtilsService, useValue: utilsServiceSpy },
                { provide: Router, useValue: routerSpy }
            ]
        });

        guard = TestBed.inject(AuthorizationGuard);
        utilsService = TestBed.inject(UtilsService) as jasmine.SpyObj<UtilsService>;
        router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
        router.navigate.and.returnValue(Promise.resolve(true));
    });

    it('should be created', () => {
        expect(guard).toBeTruthy();
    });

    it('should allow access when user is authenticated and is seller', () => {
        utilsService.isAuthenticated.and.returnValue(true);
        utilsService.isSeller.and.returnValue(true);

        const result = guard.canActivate();

        expect(result).toBe(true);
        expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should deny access when user is not authenticated', () => {
        utilsService.isAuthenticated.and.returnValue(false);
        utilsService.isSeller.and.returnValue(true);

        const result = guard.canActivate();

        expect(result).toBe(false);
        expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should deny access when user is authenticated but not seller', () => {
        utilsService.isAuthenticated.and.returnValue(true);
        utilsService.isSeller.and.returnValue(false);

        const result = guard.canActivate();

        expect(result).toBe(false);
        expect(router.navigate).not.toHaveBeenCalled();
    });
});
