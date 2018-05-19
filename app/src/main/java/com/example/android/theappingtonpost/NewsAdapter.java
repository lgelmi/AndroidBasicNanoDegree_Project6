package com.example.android.theappingtonpost;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsAdapter extends ArrayAdapter<News> {


    NewsAdapter(@NonNull Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @SuppressLint("SimpleDateFormat")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent,
                    false);
        }
        final News currentNews = getItem(position);
        if (currentNews == null)
            // Exit before doing anything
            return listItemView;
        // Get View parts
        TextView sectionView = listItemView.findViewById(R.id.sectionName);
        TextView headlineView = listItemView.findViewById(R.id.headline);
        TextView trailTextView = listItemView.findViewById(R.id.trailText);
        TextView authorView = listItemView.findViewById(R.id.author);
        TextView dateView = listItemView.findViewById(R.id.date);
        ImageView thumbnailView = listItemView.findViewById(R.id.thumbnail);
        // Set Values
        setTextAndVisibility(sectionView, currentNews.getSection());
        setTextAndVisibility(headlineView, currentNews.getHeadline());
        setTextAndVisibility(trailTextView, currentNews.getTrailText());
        setTextAndVisibility(authorView, currentNews.getAuthor());
        Date date = currentNews.getDate();
        String dateText;
        if (date == null)
            dateText = "";
        else
            dateText = new SimpleDateFormat(getContext().getString(R.string.dateTimeFormat)).format(date);
        setTextAndVisibility(dateView, dateText);
        if (currentNews.getThumbnail() != null){
            Picasso.get().load(currentNews.getThumbnail()).into(thumbnailView);
        }
        return listItemView;
    }

    /**
     * Simplify the settings of the text fields: empty text will be completely hidden.
     */
    private void setTextAndVisibility(TextView textview, String text){
        if (text.equals(""))
            textview.setVisibility(View.GONE);
        else{
            textview.setVisibility(View.VISIBLE);
            textview.setText(Html.fromHtml(text));
        }
    }


}