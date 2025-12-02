import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GetMovieByIdService } from '../../../application/use-cases/get-movie-by-id-service';

@Component({
  selector: 'app-movie-details-page',
  templateUrl: './movie-details-page.html',
  styleUrls: ['./movie-details-page.css']
})
export class MovieDetailsPage implements OnInit {

  movie: any;

  constructor(
    private route: ActivatedRoute,
    private getMovie: GetMovieByIdService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.getMovie.execute(id).subscribe(m => this.movie = m);
  }

  goBack() {
    history.back();
  }
}
