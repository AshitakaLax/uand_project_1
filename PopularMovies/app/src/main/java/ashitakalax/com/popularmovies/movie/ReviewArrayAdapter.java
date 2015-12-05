package ashitakalax.com.popularmovies.movie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ashitakalax.com.popularmovies.R;

/**
 * todo add file Description
 *
 * @author Levi.Balling
 * @version 1
 * @since 10/31/2015
 */
public class ReviewArrayAdapter extends ArrayAdapter<ReviewItem> {

    public ReviewArrayAdapter(Context context, int resource, List<ReviewItem> objects) {
        super(context, resource, objects);
        //set the twoPane variable from parameter
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReviewItem item = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_detail, parent, false);
        }
        TextView trailerTextView = (TextView) convertView.findViewById(R.id.reviewLabelTextView);
        trailerTextView.setText("Review "+ (position + 1) + ": " +item.getReview());
        convertView.setTag(item);

        return convertView;
    }
}
