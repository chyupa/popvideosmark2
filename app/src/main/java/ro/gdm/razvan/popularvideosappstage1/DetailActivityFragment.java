package ro.gdm.razvan.popularvideosappstage1;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {


    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        /**
         * get the movie id from the intent or from the fragment
         */

        Intent intent = getActivity().getIntent();
        String movie_id = intent.getStringExtra("movie_id");
        if (movie_id == null) {
            movie_id = getArguments().getString("movie_id");
        }
        new GetMovieTask().execute(movie_id);

        new GetMovieReviewsTask().execute(movie_id);

        new GetMovieTrailersTask().execute(movie_id);

        final Button mark_as_favorite = (Button)rootView.findViewById(R.id.mark_as_favorite);
        final String finalMovie_id = movie_id;
        mark_as_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPopularMovies myPopularMovies = new MyPopularMovies(getActivity());
                SQLiteDatabase db = myPopularMovies.getWritableDatabase();



                ContentValues values_insert = new ContentValues();
                values_insert.put(MyPopularMovies.MyPopluarMoviesTables.KEY_MOVIE_MOVIE_ID, finalMovie_id);
                long new_row_id;
                new_row_id = db.insert(
                        MyPopularMovies.MyPopluarMoviesTables.TABLE_MOVIES_NAME,
                        null,
                        values_insert
                );

                if(new_row_id > 0){
                    Toast.makeText(getActivity(), "Movie added to favorites", Toast.LENGTH_SHORT).show();
                }
                String tag = mark_as_favorite.getTag().toString();
                if(tag.equals("1")){
                    mark_as_favorite.setTag(0);
                    mark_as_favorite.setText("Mark as favorite");
                }else if(tag.equals("0")){
                    mark_as_favorite.setTag(1);
                    mark_as_favorite.setText("Remove from favorites");
                }
            }
        });

        return rootView;
    }

    /**
     * populate the detail activity
     *
     * @param movie
     */
    public void setData(Map movie) {
        TextView title = (TextView) getActivity().findViewById(R.id.original_title_top);
        title.setText(movie.get("original_title").toString());

        TextView year = (TextView) getActivity().findViewById(R.id.year);
        year.setText(movie.get("release_date").toString());

        TextView runtime = (TextView) getActivity().findViewById(R.id.runtime);
        runtime.setText(movie.get("runtime").toString() + "min");

        TextView rating = (TextView) getActivity().findViewById(R.id.rating);
        rating.setText(movie.get("vote_average").toString() + "/10");

        TextView overview = (TextView) getActivity().findViewById(R.id.overview);
        overview.setText(movie.get("overview").toString());

        ImageView imageView = (ImageView) getActivity().findViewById(R.id.poster);
        String poster_path = "http://image.tmdb.org/t/p/w342" + movie.get("poster_path").toString();
        Picasso.with(getActivity()).load(poster_path).into(imageView);
    }

    /**
     * set reviews
     *
     * @param reviews
     */
    public void setReviews(String[][] reviews) {
        LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.reviews_list);
        int review_count = reviews.length;
        for (int i = 0; i < review_count; i++) {
            View view;
            view = LayoutInflater.from(getActivity()).inflate(R.layout.review_list_item, null);

            TextView author_name = (TextView) view.findViewById(R.id.review_author_name);
            author_name.setText(reviews[i][0]);

            TextView review_content = (TextView) view.findViewById(R.id.review_review_content);
            review_content.setText(reviews[i][1]);

            linearLayout.addView(view);
        }
    }

    /**
     * get movie json from themoviedb.org
     */
    public class GetMovieTask extends AsyncTask<String, Void, Map> {

        private final String LOG_TAG = this.getClass().getSimpleName();

        /**
         * parse the json and get what we need from it
         *
         * @param movie_json
         * @return
         * @throws JSONException
         */
        private Map getMovieDataFromJson(String movie_json) throws JSONException {

            final String TMDB_ORIG_TITLE = "original_title";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_POSTER = "poster_path";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_RUNTIME = "runtime";
            final String TMDB_RATING = "vote_average";

            Map<String, String> movie_map = new HashMap<>();
            JSONObject movie_object = new JSONObject(movie_json);

            movie_map.put(TMDB_ORIG_TITLE, movie_object.getString(TMDB_ORIG_TITLE));
            movie_map.put(TMDB_OVERVIEW, movie_object.getString(TMDB_OVERVIEW));
            movie_map.put(TMDB_POSTER, movie_object.getString(TMDB_POSTER));
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            Date date = null;
            try {
                date = dateFormat.parse("2015-05-05");
            } catch (ParseException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            movie_map.put(TMDB_RELEASE_DATE, year);
            movie_map.put(TMDB_RUNTIME, movie_object.getString(TMDB_RUNTIME));
            movie_map.put(TMDB_RATING, movie_object.getString(TMDB_RATING));

            return movie_map;
        }

        /**
         * call the method to populate detail activity after json is loaded and parsed
         *
         * @param map
         */
        @Override
        protected void onPostExecute(Map map) {
            super.onPostExecute(map);
            setData(map);
        }

        @Override
        protected Map doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            final String api_key = "99c5cb232d21a974d814071ef8c9800c";
            String movie_json = null;
            try {
                Uri.Builder uri = new Uri.Builder();
                uri.scheme("http");
                uri.authority("api.themoviedb.org");
                uri.appendPath("3");
                uri.appendPath("movie");
                uri.appendPath(params[0]);
                uri.appendQueryParameter("api_key", api_key);

                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }

                StringBuffer stringBuffer = new StringBuffer();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer.length() == 0) {
                    return null;
                }

                movie_json = stringBuffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }
            }

            try {
                return getMovieDataFromJson(movie_json);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            return null;
        }
    }

    public class GetMovieReviewsTask extends AsyncTask<String, Void, String[][]> {

        private final String LOG_TAG = this.getClass().getSimpleName();

        private String[][] getMovieReviewsFromJson(String movie_reviews_json) throws JSONException {

            final String TMDB_REVIEWS = "results";
            final String TMDB_AUTHOR = "author";
            final String TMDB_REVIEW = "content";

            JSONObject movie_reviews = new JSONObject(movie_reviews_json);
            JSONArray reviews = new JSONArray(movie_reviews.getString(TMDB_REVIEWS));
            int reviews_count = reviews.length();
            String[][] return_reviews = new String[reviews_count][];
            for (int i = 0; i < reviews_count; i++) {
                String[] review = new String[2];
                JSONObject review_object = reviews.getJSONObject(i);
                review[0] = review_object.getString(TMDB_AUTHOR);
                review[1] = review_object.getString(TMDB_REVIEW);
                return_reviews[i] = review;
            }

            return return_reviews;
        }

        @Override
        protected void onPostExecute(String[][] strings) {
            super.onPostExecute(strings);
            setReviews(strings);
        }

        @Override
        protected String[][] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            final String api_key = "99c5cb232d21a974d814071ef8c9800c";
            String movie_reviews_json = null;
            try {
                Uri.Builder uri = new Uri.Builder();
                uri.scheme("http");
                uri.authority("api.themoviedb.org");
                uri.path("3");
                uri.appendPath("movie");
                uri.appendPath(params[0]);
                uri.appendPath("reviews");
                uri.appendQueryParameter("api_key", api_key);

                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }
                if (stringBuffer.length() == 0) {
                    return null;
                }

                movie_reviews_json = stringBuffer.toString();
            } catch (IOException e) {
                Log.d(LOG_TAG, e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.d(LOG_TAG, e.getMessage());
                    }
                }
            }

            try {
                return getMovieReviewsFromJson(movie_reviews_json);
            } catch (JSONException e) {
                Log.d(LOG_TAG, e.getMessage());
            }

            return null;
        }
    }

    public class GetMovieTrailersTask extends AsyncTask<String, Void, String[][]> {

        private final String LOG_TAG = this.getClass().getSimpleName();

        @Override
        protected void onPostExecute(String[][] strings) {
            super.onPostExecute(strings);
            setTrailers(strings);
        }

        @Override
        protected String[][] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            final String api_key = "99c5cb232d21a974d814071ef8c9800c";

            String movie_trailers_json = null;
            try {
                Uri.Builder uri = new Uri.Builder();
                uri.scheme("http");
                uri.authority("api.themoviedb.org");
                uri.path("3");
                uri.appendPath("movie");
                uri.appendPath(params[0]);
                uri.appendPath("videos");
                uri.appendQueryParameter("api_key", api_key);

                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }
                if (stringBuffer.length() == 0) {
                    return null;
                }

                movie_trailers_json = stringBuffer.toString();
            } catch (IOException e) {
                Log.d(LOG_TAG, e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.d(LOG_TAG, e.getMessage());
                    }
                }
            }

            try {
                return getMovieTrailersFromJson(movie_trailers_json);
            } catch (JSONException e) {
                Log.d(LOG_TAG, e.getMessage());
            }

            return null;
        }

        private String[][] getMovieTrailersFromJson(String movie_trailers_json) throws JSONException {
            final String TMDB_TRAILERS = "results";
            final String TMDB_TRAILER_KEY = "key";
            final String TMDB_TRAILER_NAME = "name";

            JSONObject movie_reviews = new JSONObject(movie_trailers_json);
            JSONArray trailers = new JSONArray(movie_reviews.getString(TMDB_TRAILERS));
            int trailers_count = trailers.length();
            String[][] return_trailers = new String[trailers_count][];
            for (int i = 0; i < trailers_count; i++) {
                String[] trailer = new String[2];
                JSONObject trailer_object = trailers.getJSONObject(i);
                trailer[0] = trailer_object.getString(TMDB_TRAILER_NAME);
                trailer[1] = trailer_object.getString(TMDB_TRAILER_KEY);
                return_trailers[i] = trailer;
            }

            return return_trailers;
        }
    }

    private void setTrailers(final String[][] trailers) {
        LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.trailers_list);
        int trailer_count = trailers.length;
        for (int i = 0; i < trailer_count; i++) {
            View view;
            view = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_list_item, null);

            TextView trailer_title = (TextView) view.findViewById(R.id.trailer_title);
            trailer_title.setText(trailers[i][0]);

            linearLayout.setTag(trailers[i][1]);

            linearLayout.addView(view);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://youtube.com/watch?v="+v.getTag())));
                }
            });
        }
    }
}
