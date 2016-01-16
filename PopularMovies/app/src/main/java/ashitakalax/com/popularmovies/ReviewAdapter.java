package ashitakalax.com.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by levi balling on 1/1/2016.
 */
public class ReviewAdapter extends CursorAdapter {


    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * this is an optimization that allows it to recycle the views and be more efficient
     *
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.review_detail, parent, false);
        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        TextView reviewTextView = (TextView) view.findViewById(R.id.reviewLabelTextView);

        String authorStr = cursor.getString(MovieDetailFragment.REVIEW_COL_AUTHOR);
        String reviewStr = cursor.getString(MovieDetailFragment.REVIEW_COL_CONTENT);
        reviewTextView.setText( authorStr + ": " + reviewStr );


    }
}
