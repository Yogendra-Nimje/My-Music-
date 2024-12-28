package com.example.mymusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> songTitles;

    public MusicListAdapter(Context context, ArrayList<String> songTitles) {
        this.context = context;
        this.songTitles = songTitles;
    }

    @Override
    public int getCount() {
        return songTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return songTitles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        ImageView musicImage = convertView.findViewById(R.id.music_image);
        TextView musicTitle = convertView.findViewById(R.id.music_title);

        // Set image and text
        musicImage.setImageResource(R.drawable.music); // Default music icon
        musicTitle.setText(songTitles.get(position));

        return convertView;
    }
}
