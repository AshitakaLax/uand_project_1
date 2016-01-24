package ashitakalax.com.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ashitakalax.com.popularmovies.movie.MovieContract;

/**
 * Created by Levi Balling on 1/2/2016.
 */
public class MainMovieActivity extends AppCompatActivity {

    private final String LOG_TAG = MainMovieActivity.class.getSimpleName();

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View containerView = findViewById(R.id.container);

        //check if this is 2 pane or 1
//by default the movieFragment should be setup on launch

        if(containerView.findViewById(R.id.movie_detail_container)!= null)
        {
            //two pane
            if (savedInstanceState == null)
            {
                long temp = Utility.getSelectedMovie(this);
                if(temp != -1) {

                    //load the fragment into the other container
                    this.getIntent().setData(MovieContract.MovieEntry.buildMovieUri(temp));
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                    return;
                }
            }
        }

        if (savedInstanceState == null) {
            //don't load into container
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new MovieFragment()).commit();
        }
    }




}
