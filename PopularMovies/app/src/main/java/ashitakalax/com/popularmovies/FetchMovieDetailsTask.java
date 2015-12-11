package ashitakalax.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import ashitakalax.com.popularmovies.movie.MovieItem;

/**
 * todo add file Description
 *
 * @author Levi.Balling
 * @version 1
 * @since 12/10/2015
 */
public class FetchMovieDetailsTask extends AsyncTask<MovieItem, Void, MovieItem>
{
    @Override
    protected MovieItem doInBackground(MovieItem... movie) {

        //this will query the movie info
        MovieItem.queryMovieDetails(movie[0].getId()+"");
        //here is where we load the data


        return movie[0];
    }
    protected void onPostExecute(MovieItem movie) {
        //to be decided
        //here is where we would load the data into the db

//
//
//        Bundle arguments = new Bundle();
//
//        arguments.putParcelable(MovieDetailFragment.ARG_MOVIE_BUNDLE_ID, movie);//this.mMovieItemList.get(0));//go with the first as default
//
//
//
//        if (mTwoPane) {
//            //arguments.putString(MovieDetailFragment.ARG_MOVIE_ID, "1");
//            MovieDetailFragment fragment = new MovieDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.movie_detail_container, fragment)
//                    .commit();
//        } else {
//            Context context = getApplicationContext();//.getContext();
//
//            Intent intent = new Intent(context, MovieDetailActivity.class);
//            //intent.putExtra(MovieDetailFragment.ARG_MOVIE_ID, "0");//holder.mItem.id);
//            intent.putExtras(arguments);
//
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
        }
//            //maybe update the data
//            //we will want to begin the query for the movie details, then go to the fragment
//            Bundle arguments = new Bundle();
//
//            arguments.putParcelable(MovieDetailFragment.ARG_MOVIE_BUNDLE_ID, movie);//this.mMovieItemList.get(0));//go with the first as default
//
//            MovieDetailFragment fragment = new MovieDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.movie_detail_container, fragment)
//                    .commit();

    }
}