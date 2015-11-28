package ashitakalax.com.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ashitakalax.com.popularmovies.movie.MovieItem;


/**
 * A fragment representing a single movie detail screen.
 * This fragment is either contained in a {@link MovieGridActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MOVIE_BUNDLE_ID = "SelectedMovieItem";

    /**
     * The dummy content this fragment is presenting.
     */
    private MovieItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movies",mItem);
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mItem  = this.getArguments().getParcelable(ARG_MOVIE_BUNDLE_ID);

        if(this.mItem == null && savedInstanceState == null) {
            mItem = new MovieItem();
        }

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle("Movie Detail");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            TextView titleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
            TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.releaseDateTextView);
            TextView movieTimeLengthTextView = (TextView) rootView.findViewById(R.id.movieTimeLengthTextView);
            TextView movieRatingTextView = (TextView) rootView.findViewById(R.id.movieRatingTextView);
            TextView movieOverviewTextView = (TextView) rootView.findViewById(R.id.movieOverviewTextView);
            ImageView posterImageView = (ImageView) rootView.findViewById(R.id.moviePosterImageView);

            if (titleTextView != null) {
                titleTextView.setText(mItem.getOriginalTitle());
            }
            if (releaseDateTextView != null) {
                releaseDateTextView.setText(mItem.getReleaseDate());
            }
            if (movieRatingTextView != null) {
                movieRatingTextView.setText(mItem.getUserRating() + "/10");
            }
            if (movieOverviewTextView != null) {
                movieOverviewTextView.setText(mItem.getPlotSynopsis());
            }
            if (posterImageView != null) {

                String imageUrl = "http://image.tmdb.org/t/p/w185" + mItem.getImageUrl();
                Picasso.with(getContext()).load(imageUrl).into(posterImageView);
            }
        }

        return rootView;
    }
}
