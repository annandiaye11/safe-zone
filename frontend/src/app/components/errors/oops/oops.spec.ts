import {ComponentFixture, TestBed} from '@angular/core/testing';

import {Oops} from './oops';

describe('Oops', () => {
    let component: Oops;
    let fixture: ComponentFixture<Oops>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [Oops]
        })
            .compileComponents();

        fixture = TestBed.createComponent(Oops);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
