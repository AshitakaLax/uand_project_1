package ashitakalax.com.popularmovies;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import ashitakalax.com.popularmovies.movie.MovieContract;
import ashitakalax.com.popularmovies.movie.MovieItem;


/**
 * A fragment representing a single movie detail screen.
 * This fragment is either contained in a {@link MovieGridActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MOVIE_BUNDLE_ID = "SelectedMovieItem";
    static final int COL_ID = 0;

    //todo add the sharing later
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_POSTER = 2;
    static final int COL_MOVIE_TITLE = 3;
    static final int COL_MOVIE_OVERVIEW = 4;
    static final int COL_MOVIE_VOTE = 5;
    static final int COL_MOVIE_RELEASE_DATE = 6;
    static final int REVIEW_COL_ID = 0;
    static final int REVIEW_COL_AUTHOR = 1;
    static final int REVIEW_COL_CONTENT = 2;
    static final int TRAILER_COL_ID = 0;
    static final int TRAILER_COL_TITLE = 1;
    static final int TRAILER_COL_URL = 2;
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    private static final int REVIEW_LOADER = 1;
    private static final int TRAILER_LOADER = 2;
    private static final String[] MOVIE_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
    };
    private static final String[] REVIEW_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT,
    };
    private static final String[] TRAILER_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE,
            MovieContract.TrailerEntry.COLUMN_TRAILER_URL,
    };
    /**
     * The dummy content this fragment is presenting.
     */
    private int mMovieId;
    private MovieItem mItem;
    private Button favoriteButton;
    private ListView mTrailerListView;
    private ListView mReviewListView;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

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
        return;
//        this.mItem  = this.getArguments().getParcelable(ARG_MOVIE_BUNDLE_ID);
//
//        if(this.mItem == null && savedInstanceState == null) {
//            mItem = new MovieItem();
//        }
//
//        Activity activity = this.getActivity();
//        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//        if (appBarLayout != null) {
//            appBarLayout.setTitle("Movie Detail");
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        mTrailerListView = (ListView)rootView.findViewById(R.id.TrailersListView);
        mReviewListView = (ListView)rootView.findViewById(R.id.ReviewListView);

        this.mTrailerAdapter = new TrailerAdapter(getActivity(), null, 0);
        mTrailerListView.setAdapter(mTrailerAdapter);

        this.mReviewAdapter = new ReviewAdapter(getActivity(), null, 0);
        mReviewListView.setAdapter(this.mReviewAdapter);

//        this.mTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                //start an intent to play a movie on youtube or chrome
//                    String url = (String) view.getTag();
//                    try {
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + url));
//                        startActivity(intent);
//                    } catch (ActivityNotFoundException ex) {
//                        Intent intent = new Intent(Intent.ACTION_VIEW,
//                                Uri.parse("http://www.youtube.com/watch?v=" + url));
//                        startActivity(intent);
//                    }
//            }
//        });

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);


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
            favoriteMovies.add(this.mItem.getId() + "");

            SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(MovieGridActivity.MOVIE_SHARE_PREF_FILE, Activity.MODE_PRIVATE).edit();
            editor.putStringSet(MovieGridActivity.FAVORITE_MOVIES_SHARE_PREF_FILE, favoriteMovies);
            editor.commit();
            this.updateFavoriteButtonText(true);
        }
        else
        {
//            String[] favoriteIdStrs = new String[];
//            favoriteMovies.toArray(favoriteIdStrs);
            List<String> favoriteMovieArrayList =  new ArrayList<String>();
            String[] favoriteIdStrs = favoriteMovies.toArray(new String[favoriteMovies.size()]);
            favoriteMovieArrayList.addAll(Arrays.asList(favoriteIdStrs));
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
            editor.commit();

            this.updateFavoriteButtonText(false);

        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(LOG_TAG, "In OnCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }
        if (id == DETAIL_LOADER) {
            return new CursorLoader(getActivity(),
                    intent.getData(),
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        } else if (id == REVIEW_LOADER) {
            Uri movieUri = intent.getData();
            String movieId = MovieContract.MovieEntry.getMovieIdFromUri(movieUri);
            Uri ReviewUri = MovieContract.ReviewEntry.buildMovieReview(movieId);
            return new CursorLoader(getActivity(),
                    ReviewUri,
                    REVIEW_COLUMNS,
                    null,
                    null,
                    null
            );
        } else if (id == TRAILER_LOADER) {
            Uri movieUri = intent.getData();
            String movieId = MovieContract.MovieEntry.getMovieIdFromUri(movieUri);
            Uri TrailerUri = MovieContract.TrailerEntry.buildMovieTrailer(movieId);
            return new CursorLoader(getActivity(),
                    TrailerUri,
                    TRAILER_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In OnLoadFinished");

        if (loader.getId() == DETAIL_LOADER) {
            loadMovieDetail(data);
        } else if (loader.getId() == REVIEW_LOADER) {
            loadReviewDetail(data);
        } else if (loader.getId() == TRAILER_LOADER) {
            loadTrailerDetail(data);
        }
        return;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void loadTrailerDetail(Cursor data) {
        this.mTrailerAdapter.swapCursor(data);
        return;
//        if (!data.moveToFirst()) {
//            return;
//        }
//        LinearLayout trailerLayout = (LinearLayout) getView().findViewById(R.id.TrailersLayout);
//
//        View custom = null;
//        do {
//
//            //TODO create an instance of the trailer Adapter should be the same as the movie adapter setup
//            custom = LayoutInflater.from(getContext()).inflate(R.layout.trailer_detail, trailerLayout, false);
//
//            TextView trailerTextView = (TextView) custom.findViewById(R.id.trailerLabelTextView);
//            String TrailerTitle = data.getString(TRAILER_COL_TITLE);
//            trailerTextView.setText(TrailerTitle);//.getTitle());
//            Button trailerPlayButton = (Button) custom.findViewById(R.id.playButton);
//            String trailerUrl = data.getString(TRAILER_COL_URL);
//            trailerPlayButton.setTag(trailerUrl);
//            trailerPlayButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    //start an intent to play a movie on youtube or chrome
//                    String url = (String) view.getTag();
//                    //TrailerItem tempItem = (TrailerItem) view.getTag();
//                    try {
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + url));
//                        startActivity(intent);
//                    } catch (ActivityNotFoundException ex) {
//                        Intent intent = new Intent(Intent.ACTION_VIEW,
//                                Uri.parse("http://www.youtube.com/watch?v=" + url));
//                        startActivity(intent);
//                    }
//                }
//            });
//            trailerTextView.setText(TrailerTitle);
//            custom.setTag(trailerUrl);
//            trailerLayout.addView(custom);
//        }
//        while (data.moveToNext());

    }

    private void loadReviewDetail(Cursor data) {
        this.mReviewAdapter.swapCursor(data);
        return;
//        if (!data.moveToFirst()) {
//            return;
//        }
//
//        LinearLayout reviewLayout = (LinearLayout) getView().findViewById(R.id.reviewsLayout);
//                    int i = 0;
//        do {
//            View  custom = LayoutInflater.from(getContext()).inflate(R.layout.review_detail,reviewLayout, false);
//            TextView reviewTextView = (TextView) custom.findViewById(R.id.reviewLabelTextView);
//
//            String reviewStr = data.getString(0);//todo update index
//            reviewTextView.setText("Review: " + reviewStr );
////            custom.setTag(review);
//            reviewLayout.addView(custom);
//        }while(data.moveToNext());

    }

    private void loadMovieDetail(Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String movieTitle = data.getString(COL_MOVIE_TITLE);
        String movieReleaseDate = data.getString(COL_MOVIE_RELEASE_DATE);
        String movieOverview = data.getString(COL_MOVIE_OVERVIEW);

        String moviePoster = data.getString(COL_MOVIE_POSTER);

        double movieRating = data.getDouble(COL_MOVIE_VOTE);

        //get the data from the cursor
        TextView titleTextView = (TextView) getView().findViewById(R.id.titleTextView);
        TextView releaseDateTextView = (TextView) getView().findViewById(R.id.releaseDateTextView);
        TextView movieTimeLengthTextView = (TextView) getView().findViewById(R.id.movieTimeLengthTextView);
        TextView movieRatingTextView = (TextView) getView().findViewById(R.id.movieRatingTextView);
        TextView movieOverviewTextView = (TextView) getView().findViewById(R.id.movieOverviewTextView);
        ImageView posterImageView = (ImageView) getView().findViewById(R.id.moviePosterImageView);
        this.favoriteButton = (Button) getView().findViewById(R.id.favoriteButton);
        LinearLayout trailerLayout = (LinearLayout) getView().findViewById(R.id.TrailersLayout);
        LinearLayout reviewLayout = (LinearLayout) getView().findViewById(R.id.reviewsLayout);

        if (titleTextView != null) {
            titleTextView.setText(movieTitle);
        }

        if (releaseDateTextView != null) {
            releaseDateTextView.setText(movieReleaseDate);
        }

        if (movieRatingTextView != null) {
            movieRatingTextView.setText(movieRating + "/10");
        }

        if (movieOverviewTextView != null) {
            movieOverviewTextView.setText(movieOverview);
        }

        if (posterImageView != null) {

            String imageUrl = "http://image.tmdb.org/t/p/w185" + moviePoster;
            Picasso.with(getContext()).load(imageUrl).into(posterImageView);
        }
    }

}
