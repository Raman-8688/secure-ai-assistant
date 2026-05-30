// src/app/core/services/network.service.ts

import { Injectable } from '@angular/core';
import { BehaviorSubject, fromEvent, merge, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class NetworkService {
  private _isOnline = new BehaviorSubject<boolean>(navigator.onLine);
  isOnline$ = this._isOnline.asObservable();

  constructor() {
    merge(
      fromEvent(window, 'online').pipe(map(() => true)),
      fromEvent(window, 'offline').pipe(map(() => false))
    ).subscribe(status => this._isOnline.next(status));
  }

  get isOnline(): boolean {
    return this._isOnline.getValue();
  }
}