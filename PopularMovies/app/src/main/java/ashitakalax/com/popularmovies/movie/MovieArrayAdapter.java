package ashitakalax.com.popularmovies.movie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ashitakalax.com.popularmovies.MovieDetailActivity;
import ashitakalax.com.popularmovies.MovieDetailFragment;
import ashitakalax.com.popularmovies.R;

/**
 * todo add file Description
 *
 * @author Levi.Balling
 * @version 1
 * @since 10/31/2015
 */
public class MovieArrayAdapter extends ArrayAdapter<MovieItem> {

    private View.OnClickListener mImageOnClickListener;

    public MovieArrayAdapter(Context context, int resource, List<MovieItem> objects, View.OnClickListener listener) {
        super(context, resource, objects);
        //set the twoPane variable from parameter
        this.mImageOnClickListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieItem item = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }

        ImageView movieImageView = (ImageView) convertView.findViewById(R.id.movie_item_image);

        //todo change the url to be from the movie Item

        String imageUrl = "http://image.tmdb.org/t/p/w185" + item.getmImageUrl();
        Picasso.with(getContext()).load(imageUrl).into(movieImageView);

        movieImageView.setOnClickListener(this.mImageOnClickListener);

        return convertView;
    }
}
