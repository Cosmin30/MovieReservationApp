import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { tap, shareReplay, catchError } from 'rxjs/operators';

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
    // Return cached observable if exists and it has data
    const cached = this.cache.get(key);
    
    if (cached && cached.data !== null) {
      return cached.observable;
    }
    
    // Fetch and cache new data
    const request$ = fetcher().pipe(
      tap(data => {
        // Update cache ONLY after successful data fetch
        this.cache.set(key, { data, observable: request$ });
      }),
      catchError(error => {
        // Don't cache errors - remove from cache so next request tries again
        this.cache.delete(key);
        throw error;
      }),
      shareReplay(1) // Share the result between multiple subscribers
    );

    // Store the observable in cache (with null data initially)
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
