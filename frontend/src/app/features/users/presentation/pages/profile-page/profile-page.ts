import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GetCurrentUserService } from '../../../application/use-cases/get-current-user-service';
import { UpdateUserProfileService } from '../../../application/use-cases/update-user-profile-service';
import { ProfileFormComponent } from '../../components/profile-form/profile-form'; // import componenta

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.html',
  standalone: true,
  imports: [CommonModule, FormsModule, ProfileFormComponent]  // <-- import componenta
})
export class ProfilePage implements OnInit {
  profile: any = {};

  constructor(
    private getUser: GetCurrentUserService,
    private updateUser: UpdateUserProfileService
  ) {}

  ngOnInit() {
    this.getUser.execute().subscribe(u => {
      this.profile = { ...u };
    });
  }

  update(form: any) {
    this.updateUser.execute(form).subscribe();
  }
}
