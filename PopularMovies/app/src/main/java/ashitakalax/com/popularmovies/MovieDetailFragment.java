package ashitakalax.com.popularmovies;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ashitakalax.com.popularmovies.movie.MovieItem;
import ashitakalax.com.popularmovies.movie.ReviewArrayAdapter;
import ashitakalax.com.popularmovies.movie.ReviewItem;
import ashitakalax.com.popularmovies.movie.TrailerArrayAdapter;
import ashitakalax.com.popularmovies.movie.TrailerItem;


/**
 * A fragment representing a single movie detail screen.
 * This fragment is either contained in a {@link MovieGridActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment implements View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MOVIE_BUNDLE_ID = "SelectedMovieItem";

    /**
     * The dummy content this fragment is presenting.
     */
    private MovieItem mItem;
    private Button favoriteButton;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movies", mItem);
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
            this.favoriteButton = (Button) rootView.findViewById(R.id.favoriteButton);
            LinearLayout trailerLayout = (LinearLayout) rootView.findViewById(R.id.TrailersLayout);
            LinearLayout reviewLayout = (LinearLayout) rootView.findViewById(R.id.reviewsLayout);

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
            if(favoriteButton != null)
            {
                //determine whether we want to display this button as a remove from favorites
                //or save to favorites
                this.updateFavoriteButtonText(isMovieFavorite());
                favoriteButton.setOnClickListener(this);
            }

            View custom = null;
            for (TrailerItem trailer : mItem.getTrailers())
            {
                custom = LayoutInflater.from(getContext()).inflate(R.layout.trailer_detail,trailerLayout, false);

                TextView trailerTextView = (TextView) custom.findViewById(R.id.trailerLabelTextView);
                trailerTextView.setText(trailer.getTitle());
                Button trailerPlayButton = (Button) custom.findViewById(R.id.playButton);

                trailerPlayButton.setTag(trailer);
                trailerPlayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //start an intent to play a movie on youtube or chrome
                        TrailerItem tempItem = (TrailerItem)view.getTag();
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + tempItem.getUrl()));
                            startActivity(intent);
                        }
                    catch(ActivityNotFoundException ex)
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + tempItem.getUrl()));
                        startActivity(intent);
                    }
                }
            });
                trailerTextView.setText(trailer.getTitle());
                custom.setTag(trailer);
                trailerLayout.addView(custom);

            }
            custom = null;
            int i = 0;
            for (ReviewItem review : mItem.getReviews())
            {
                custom = LayoutInflater.from(getContext()).inflate(R.layout.review_detail,reviewLayout, false);

                TextView reviewTextView = (TextView) custom.findViewById(R.id.reviewLabelTextView);
                reviewTextView.setText("Review "+ (i++) + ": " +review.getReview());
                custom.setTag(review);
                reviewLayout.addView(custom);
            }

        }

        return rootView;
    }

    private void updateFavoriteButtonText(boolean isFavorite)
    {
        if(isFavorite)
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    favoriteButton.setText("Remove from Favorites");
                }
            });
        }
        else
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    favoriteButton.setText("Save to Favorites");
                }
            });
        }
    }

    public boolean isMovieFavorite()
    {
        SharedPreferences prefs = this.getActivity().getSharedPreferences(MovieGridActivity.MOVIE_SHARE_PREF_FILE, Activity.MODE_PRIVATE);

        //this is a string set of movie id's only not of anything else

        Set<String> favoriteMovies = prefs.getStringSet(MovieGridActivity.FAVORITE_MOVIES_SHARE_PREF_FILE, new TreeSet<String>());

        //check by movie id
        for (String item: favoriteMovies) {
            int movieId = Integer.parseInt(item);
            if(this.mItem.getId() == movieId)
            {
                return  true;
            }
        }
        return  false;
    }




    @Override
    public void onClick(View view) {

        SharedPreferences prefs = this.getActivity().getSharedPreferences(MovieGridActivity.MOVIE_SHARE_PREF_FILE, Activity.MODE_PRIVATE);
        //this is a string set of movie id's only not of anything else
        Set<String> favoriteMovies = prefs.getStringSet(MovieGridActivity.FAVORITE_MOVIES_SHARE_PREF_FILE, new TreeSet<String>());

        if(!isMovieFavorite())
        {
            //add to list of favorites and update shared preferences
            favoriteMovies.add(this.mItem.getId()+"");

            SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(MovieGridActivity.MOVIE_SHARE_PREF_FILE, Activity.MODE_PRIVATE).edit();
            editor.putStringSet(MovieGridActivity.FAVORITE_MOVIES_SHARE_PREF_FILE, favoriteMovies);
            editor.apply();
            this.updateFavoriteButtonText(true);
        }
        else
        {
//            String[] favoriteIdStrs = new String[];
//            favoriteMovies.toArray(favoriteIdStrs);
            List<String> favoriteMovieArrayList =  Arrays.asList((String[])favoriteMovies.toArray());  //Arrays.asList(favoriteIdStrs);// new ArrayList<String>(favoriteIdStrs);
            //favoriteMovieArrayList.addAll(favoriteIdStrs);
            for(int i = 0; i < favoriteMovieArrayList.size(); i++)
            {
                int movieId = Integer.parseInt(favoriteMovieArrayList.get(i));

                if(this.mItem.getId() == movieId)
                {
                    //remove the item from the favorites
                    favoriteMovieArrayList.remove(i);
                    break;
                }
            }

            //convert the list back into favorite moves set
            favoriteMovies.clear();
            favoriteMovies.addAll(favoriteMovieArrayList);
            //save back into shared preferences
            SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(MovieGridActivity.MOVIE_SHARE_PREF_FILE, Activity.MODE_PRIVATE).edit();
            editor.putStringSet(MovieGridActivity.FAVORITE_MOVIES_SHARE_PREF_FILE, favoriteMovies);
            editor.apply();

            this.updateFavoriteButtonText(false);

        }

    }
}
