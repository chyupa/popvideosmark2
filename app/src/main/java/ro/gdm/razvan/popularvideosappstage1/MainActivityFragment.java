package ro.gdm.razvan.popularvideosappstage1;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public GridView gridView;
    private JSONObject movie_results;

    public boolean mTwoPane;

    /**
     * get the movies from themoviedb.org and populate the gridview
     */
    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView)rootView.findViewById(R.id.grid_view);

        View movie_detail = rootView.findViewById(R.id.movie_detail_container);

        /**
         * check to see if it's a tablet or a phone
         * movie_detail View will only show up on a tablet
         */
        if( movie_detail != null ){
            mTwoPane = true;

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle arguments = new Bundle();
                    /**
                     * kinda hacky. Don't know if this is the way to do it.
                     * we need movie_id in DetailActivity to get the information we need to populate all the fields
                     */
                    String movie_id = view.getTag().toString();
                    arguments.putString("movie_id", movie_id);
                    /**
                     * we use fragment here and no Intent because the Detail Activity needs to be in the same layout as the grid view
                     */
                    DetailActivityFragment fragment = new DetailActivityFragment();
                    fragment.setArguments(arguments);
                    getFragmentManager().beginTransaction().replace(R.id.movie_detail_container, fragment).commit();
                }
            });
        }else {
            /**
             * start a new intent on phones
             */
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    String movie_id = view.getTag().toString();

                    /**
                     * something odd here. the debugger says that view.getTag() returns a String but I need to use toString to make it work ??
                     */
                    intent.putExtra("movie_id", movie_id);
                    startActivity(intent);
                }
            });
        }
        return rootView;
    }

    /**
     * method used to execute the async task
     */
    public void updateMovies(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = sharedPreferences.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));
        if( sort_by.equals("favorites.desc") )
        {
            getMoviesFromSharedPrefs();
        }
        else
        {
            new GetMovies().execute(sort_by);
        }
    }

    public void getMoviesFromSharedPrefs(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("favorite_movies", Context.MODE_PRIVATE);
        Map<String, ?> all_prefs = sharedPreferences.getAll();
        String[][] movies = new String[all_prefs.size()][];
        int counter = 0;
        for(Map.Entry<String, ?> entry : all_prefs.entrySet()){
            String[] fav_movie = new String[2];
            try {
                String entry_string = entry.getValue().toString();
                JSONArray jsonArray = new JSONArray(entry_string);
                fav_movie[0] = jsonArray.getString(0);
                fav_movie[1] = jsonArray.getString(1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            movies[counter++] = fav_movie;
        }

        gridView.setAdapter(new PicassoImageAdapter(getActivity(), movies));
        gridView.setOnScrollListener(new PicassoScrollListener(getActivity()));
    }

    /**
     * async task to get the most popular or most voted movies
     */
    public class GetMovies extends AsyncTask<String, Void, String[][]>{

        private final String LOG_TAG = this.getClass().getSimpleName();

        private String[][] getMoviesFromJson( String moviesJson ) throws JSONException{
            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_MOVIE_ID = "id";

            movie_results = new JSONObject(moviesJson);
            JSONArray results = movie_results.getJSONArray(TMDB_RESULTS);

            int results_length = results.length();
            String[][] posters = new String[results_length][];
            for( int i = 0; i < results_length; i++ ){
                String[] poster_and_id = new String[2];
                JSONObject movie = results.getJSONObject(i);
                poster_and_id[0] = movie.getString(TMDB_POSTER_PATH);
                poster_and_id[1] = movie.getString(TMDB_MOVIE_ID);
                posters[i] = poster_and_id;
            }

            return posters;
        }

        /**
         * update the grid view with movie posters after json is parsed
         * @param strings
         */
        @Override
        protected void onPostExecute(String[][] strings) {
            super.onPostExecute(strings);

            gridView.setAdapter(new PicassoImageAdapter(getActivity(), strings));
            gridView.setOnScrollListener(new PicassoScrollListener(getActivity()));

        }

        /**
         * get json and parse it
         * @param params
         * @return
         */
        @Override
        protected String[][] doInBackground(String... params) {
            final String sort_by = params[0];
            final String TMDB_DISCOVER = "discover";
            final String TMDB_MOVIE = "movie";
            final String TMDB_SORT = "sort_by";
            final String TMDB_API = "api_key";
            final String TMDB_API_KEY = "99c5cb232d21a974d814071ef8c9800c";

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            String moviesJson = null;

            try {
                Uri.Builder uri = new Uri.Builder();
                uri.scheme("http");
                uri.authority("api.themoviedb.org");
                uri.appendPath("3");
                uri.appendPath(TMDB_DISCOVER);
                uri.appendPath(TMDB_MOVIE);
                uri.appendQueryParameter(TMDB_SORT, sort_by);
                uri.appendQueryParameter(TMDB_API, TMDB_API_KEY);

                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream == null){
                    return null;
                }

                StringBuffer stringBuffer = new StringBuffer();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = bufferedReader.readLine()) != null){
                    stringBuffer.append(line + "\n");
                }
                if(stringBuffer.length() == 0){
                    return null;
                }

                moviesJson = stringBuffer.toString();

            } catch (IOException e) {
                Log.i(LOG_TAG, e.getMessage());
            }finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(bufferedReader != null){
                    try{
                        bufferedReader.close();
                    }catch (final IOException e){
                        Log.i(LOG_TAG, e.getMessage());
                    }
                }
            }

            try{
                return getMoviesFromJson(moviesJson);
            }catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            return null;
        }
    }
}