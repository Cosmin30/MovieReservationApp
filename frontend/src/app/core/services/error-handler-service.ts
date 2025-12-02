import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ErrorHandlerService {

  handle(error: any) {
    console.error("API Error:", error);
    alert("A apărut o eroare!"); 
  }
}
