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
public class FetchMovieDetailsTask extends AsyncTask<List<Integer>, Void, Integer>
{
    private Context mContext;

    private FetchMovieTask.FetchComplete completed;

    public FetchMovieDetailsTask(Context context)
    {
        this.mContext = context;
    }

    public void setFetchMovieCompleted(FetchMovieTask.FetchComplete completeHandler)
    {
        this.completed = completeHandler;
    }

    @Override
    protected Integer doInBackground(List<Integer>... movieIds) {

        //this will query the movie info

        //this is a temp approach till we get the GCM working
        for(int i = 0; i < movieIds[0].size(); i++)
        {
            MovieItem.queryMovieDetails(this.mContext, movieIds[0].get(i));
        }

        return 0;
    }
    protected void onPostExecute(Integer valid) {
        this.completed.FetchComplete();

    }
}