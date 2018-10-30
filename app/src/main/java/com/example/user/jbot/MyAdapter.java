package com.example.user.jbot;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<String> mDatasetBot;
    private ArrayList<String> mDatasetUser;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mLayout;
        public ViewHolder(LinearLayout v) {
            super(v);
            mLayout = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<String> DatasetBot,ArrayList<String> DatasetUser) {
        mDatasetBot = DatasetBot;
        mDatasetUser = DatasetUser;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
        .inflate(R.layout.chat_text_view_bot, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView Bottext = holder.mLayout.findViewById(R.id.bot_text_view);
        TextView Usertext = holder.mLayout.findViewById(R.id.user_text_view);
        if(mDatasetBot.size()>position) {
            Bottext.setText(Html.fromHtml(mDatasetBot.get(position)));
        }else{
            Bottext.setVisibility(View.GONE);
        }
        if(mDatasetUser.size()>position&&position!=0) {
            Usertext.setText(mDatasetUser.get(position));
        }else{
            Usertext.setVisibility(View.GONE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(mDatasetUser.size()==0){
            return  1;
        }else {
            return mDatasetUser.size();
        }
    }
}