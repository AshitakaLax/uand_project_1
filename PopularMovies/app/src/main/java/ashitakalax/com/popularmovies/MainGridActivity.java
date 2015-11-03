package ashitakalax.com.popularmovies;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ClientInfoStatus;


public class MainGridActivity extends AppCompatActivity {

    private ImageView testImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_grid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        testImageView = (ImageView) findViewById(R.id.testImageView);
        setSupportActionBar(toolbar);
        //Picasso.with(this.getApplicationContext()).load(R.drawable.temp).into(testImageView);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String movieJsonStr = null;


                try
                {
                    URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/city?id=524901&APPID=");

                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return;
                    }
                    movieJsonStr = buffer.toString();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w185/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg").into(testImageView);
                        }
                    });

                }
                catch (IOException ioex)
                {

                    Log.e("PlaceholderFragment", "Error ", ioex);
                }finally{
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e("PlaceholderFragment", "Error closing stream", e);
                        }
                    }
                }
            }
        });
        thread.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_grid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
