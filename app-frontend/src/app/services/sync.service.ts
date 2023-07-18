import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  EMPTY,
  Observable,
  TimeoutError,
  catchError,
  retry,
  throwError,
  timeout,
} from 'rxjs';
import { SyncTask } from '../sync-task';

@Injectable()
export class SyncService {
  constructor(private http: HttpClient) {}

  tryPostPayload<T>(url: string, payload: T) {
    return this.http.post(url, payload).pipe(
      timeout(5000),
      retry(2),
      catchError((err: HttpErrorResponse) =>
        this.handleError(err, url, payload)
      )
    );
  }

  sync() {
    const syncTasks = this.getExistingSyncTasks();
    const requests: Observable<any>[] = [];

    // syncTasks.forEach((task) => {});
  }

  private handleError<T>(
    err: HttpErrorResponse,
    url: string,
    payload: T
  ): Observable<any> {
    if (this.offlineOrBadConnection(err)) {
      this.addOrUpdateSyncTask<T>(url, payload);
      return EMPTY;
    } else {
      console.info('A backend error occured: ', err);
      return throwError(() => err);
    }
  }

  private offlineOrBadConnection(err: HttpErrorResponse): boolean {
    return (
      err instanceof TimeoutError ||
      err.error instanceof ErrorEvent ||
      !window.navigator.onLine
    );
  }
  private addOrUpdateSyncTask<T>(url: string, payload: T): void {
    const tasks = this.getExistingSyncTasks();
    const syncTask = new SyncTask(url, payload);
    tasks.push(syncTask);
    localStorage.setItem('syncQueue', JSON.stringify(tasks));
  }
  private getExistingSyncTasks() {
    const serializedTasks = localStorage.getItem('syncQueue');
    return serializedTasks ? JSON.parse(serializedTasks) : [];
  }
}
