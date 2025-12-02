import { Component, OnInit } from '@angular/core';
import { GetCurrentUserService } from '../../../application/use-cases/get-current-user-service';
import { UpdateUserProfileService } from '../../../application/use-cases/update-user-profile-service';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.html',
  styleUrls: ['./profile-page.css']
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
