import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { tap, shareReplay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CacheService {
  private cache: Map<string, { data: any; observable: Observable<any> }> = new Map();

  /**
   * Get or fetch data with caching
   * @param key Cache key
   * @param fetcher Function that fetches the data
   * @returns Observable of the cached or fetched data
   */
  getOrFetch<T>(
    key: string,
    fetcher: () => Observable<T>
  ): Observable<T> {
    // Return cached observable if exists
    const cached = this.cache.get(key);
    if (cached) {
      return cached.observable;
    }

    // Fetch and cache new data
    const request$ = fetcher().pipe(
      tap(data => {
        // Update cache with the fetched data
        this.cache.set(key, { data, observable: request$ });
      }),
      shareReplay(1) // Share the result between multiple subscribers
    );

    // Store the observable in cache
    this.cache.set(key, { data: null, observable: request$ });
    return request$;
  }

  /**
   * Clear specific cache entry
   */
  clear(key: string): void {
    this.cache.delete(key);
  }

  /**
   * Clear all cache
   */
  clearAll(): void {
    this.cache.clear();
  }
}
