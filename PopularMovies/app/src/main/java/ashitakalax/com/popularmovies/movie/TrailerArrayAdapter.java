package ashitakalax.com.popularmovies.movie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ashitakalax.com.popularmovies.R;

/**
 * todo add file Description
 *
 * @author Levi.Balling
 * @version 1
 * @since 10/31/2015
 */
public class TrailerArrayAdapter extends ArrayAdapter<TrailerItem> {

    public TrailerArrayAdapter(Context context, int resource, List<TrailerItem> objects) {
        super(context, resource, objects);
        //set the twoPane variable from parameter
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TrailerItem item = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_detail, parent, false);
        }


        TextView trailerTextView = (TextView) convertView.findViewById(R.id.trailerLabelTextView);
        trailerTextView.setText(item.getTitle());
        convertView.setTag(item);

        return convertView;
    }
}
