export interface UserModel {
  id: string;
  email: string;
  fullName: string; 
  createdAt: string | null;
  reservations: any[] | null;
}