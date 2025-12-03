import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MovieImageService {
  private imageCache = new Map<string, string>();

  constructor() {}

  /**
   * Get movie image URL by title
   * Returns a working placeholder with gradient background
   */
  getMovieImage(movieTitle: string): Observable<string> {
    // Check memory cache first
    if (this.imageCache.has(movieTitle)) {
      return of(this.imageCache.get(movieTitle)!);
    }

    // Generate URL synchronously - no API calls needed
    const imageUrl = this.generateImageUrl(movieTitle);
    this.imageCache.set(movieTitle, imageUrl);
    
    return of(imageUrl);
  }

  /**
   * Generate a working image URL using a SVG data URL with gradient background
   * This provides a reliable fallback that always works
   */
  private generateImageUrl(movieTitle: string): string {
    const colors = this.getColorPairByTitle(movieTitle);
    const displayTitle = movieTitle.substring(0, 20);
    
    // Create SVG with gradient background
    const svgData = this.createMoviePosterSVG(displayTitle, colors.color1, colors.color2);
    const encodedSvg = encodeURIComponent(svgData);
    
    return `data:image/svg+xml,${encodedSvg}`;
  }

  /**
   * Create an SVG poster-style image
   */
  private createMoviePosterSVG(title: string, color1: string, color2: string): string {
    const lines = this.wrapText(title, 15);
    let yPos = 220;
    const lineHeight = 40;
    
    let textElements = '';
    for (const line of lines) {
      textElements += `<text x="171" y="${yPos}" text-anchor="middle" font-family="Arial, sans-serif" font-size="20" font-weight="bold" fill="white">${this.escapeXml(line)}</text>`;
      yPos += lineHeight;
    }
    
    return `<svg width="342" height="513" xmlns="http://www.w3.org/2000/svg"><defs><linearGradient id="grad" x1="0%" y1="0%" x2="100%" y2="100%"><stop offset="0%" style="stop-color:${color1};stop-opacity:1" /><stop offset="100%" style="stop-color:${color2};stop-opacity:1" /></linearGradient></defs><rect width="342" height="513" fill="url(#grad)"/><circle cx="171" cy="256" r="80" fill="rgba(255,255,255,0.1)"/>${textElements}</svg>`;
  }

  /**
   * Wrap text to fit in a certain width
   */
  private wrapText(text: string, charsPerLine: number): string[] {
    const words = text.split(' ');
    const lines: string[] = [];
    let currentLine = '';

    for (const word of words) {
      if ((currentLine + word).length > charsPerLine) {
        if (currentLine) lines.push(currentLine);
        currentLine = word;
      } else {
        currentLine = currentLine ? currentLine + ' ' + word : word;
      }
    }
    if (currentLine) lines.push(currentLine);
    return lines;
  }

  /**
   * Escape XML special characters
   */
  private escapeXml(text: string): string {
    return text
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&apos;');
  }

  /**
   * Generate consistent colors based on movie title hash
   */
  private getColorPairByTitle(title: string): { color1: string; color2: string } {
    const colors = [
      { color1: '#667eea', color2: '#764ba2' },
      { color1: '#f093fb', color2: '#f5576c' },
      { color1: '#4facfe', color2: '#00f2fe' },
      { color1: '#43e97b', color2: '#38f9d7' },
      { color1: '#fa709a', color2: '#fee140' },
      { color1: '#30cfd0', color2: '#330867' },
      { color1: '#a8edea', color2: '#fed6e3' },
      { color1: '#ff9a56', color2: '#ff6a88' },
      { color1: '#2e2e78', color2: '#1a472a' },
      { color1: '#7f00ff', color2: '#e100ff' },
      { color1: '#5f0a87', color2: '#ea00ff' },
      { color1: '#004e89', color2: '#1a96f0' }
    ];
    
    // Simple hash function
    let hash = 0;
    for (let i = 0; i < title.length; i++) {
      hash = ((hash << 5) - hash) + title.charCodeAt(i);
      hash = hash & hash;
    }
    
    return colors[Math.abs(hash) % colors.length];
  }
}
