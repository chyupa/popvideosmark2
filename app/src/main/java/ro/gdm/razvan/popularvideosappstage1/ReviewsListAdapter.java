package ro.gdm.razvan.popularvideosappstage1;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Created by Razvan on 29-Jun-15.
 */
public class ReviewsListAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater mInflater;
    private final String[][] reviews;

    public ReviewsListAdapter(Context context, String[][] reviews) {
        this.context = context;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        return reviews.length;
    }

    @Override
    public String[] getItem(int position) {
        return reviews[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String[] review = getItem(position);
        View view;
        if(convertView == null){
            view = mInflater.inflate(R.layout.review_list_item, null);
        }else{
            view = convertView;
        }

        TextView author = (TextView)view.findViewById(R.id.review_author_name);
        author.setText(review[0]);
        TextView review_content = (TextView)view.findViewById(R.id.review_review_content);
        review_content.setText(review[1]);

        return view;
    }
}
