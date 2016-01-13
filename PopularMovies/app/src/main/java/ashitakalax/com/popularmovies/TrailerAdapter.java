package ashitakalax.com.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by levi balling on 1/1/2016.
 */
public class TrailerAdapter extends CursorAdapter {


    public TrailerAdapter(Context context, Cursor c, int flags) {
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

        View view = LayoutInflater.from(context).inflate(R.layout.trailer_detail, parent, false);
        return view;

    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView trailerTextView = (TextView) view.findViewById(R.id.trailerLabelTextView);
        String TrailerTitle = cursor.getString(MovieDetailFragment.TRAILER_COL_TITLE);
        trailerTextView.setText(TrailerTitle);//.getTitle());
        Button trailerPlayButton = (Button) view.findViewById(R.id.playButton);
        String trailerUrl = cursor.getString(MovieDetailFragment.TRAILER_COL_URL);
        trailerPlayButton.setTag(trailerUrl);

        trailerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start an intent to play a movie on youtube or chrome
                String url = (String) view.getTag();
                //TrailerItem tempItem = (TrailerItem) view.getTag();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + url));
                    context.startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + url));
                    context.startActivity(intent);
                }
            }
        });

        trailerTextView.setText(TrailerTitle);
        view.setTag(trailerUrl);

    }
}
