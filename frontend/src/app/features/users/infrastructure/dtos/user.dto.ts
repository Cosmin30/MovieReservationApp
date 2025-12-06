export interface UserDTO {
  id?: string;
  email?: string;
  fullName?: string;
  full_name?: string; // Backend sends snake_case
  createdAt?: string;
  created_at?: string; // Backend sends snake_case
  reservations?: unknown[];
}
