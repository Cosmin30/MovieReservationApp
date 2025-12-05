export interface MovieDTO {
  id?: string;
  title?: string;
  description?: string;
  genre?: string;
  duration?: number;
  releaseDate?: string;
  // Backend sends release_date (snake_case)
  release_date?: string;
}
