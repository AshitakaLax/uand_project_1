package ashitakalax.com.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ashitakalax.com.popularmovies.movie.MovieContract;

/**
 * Created by levi balling on 1/1/2016.
 */
public class MovieAdapter extends CursorAdapter {


    public MovieAdapter(Context context, Cursor c, int flags)
    {
        super(context,c,flags);
    }

    /**
     * this is an optimization that allows it to recycle the views and be more efficient
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

//        int movieImgIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL);

        ImageView movieImageView = (ImageView) view.findViewById(R.id.movie_item_image);
        //use the cursor to get the image url of the movie
        String imageUrl = "http://image.tmdb.org/t/p/w185" + cursor.getString(MovieFragment.COL_MOVIE_POSTER);
        Picasso.with(context).load(imageUrl).into(movieImageView);
        //don't need tags to hold the items now with the cursors
        //view.setTag(item);
    }
}
