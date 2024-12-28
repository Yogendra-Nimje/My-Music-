package com.example.mymusic;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView;
    ImageView play, pause, next, back;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;

    Thread updateSeek;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_song);

        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        songs = (ArrayList) bundle.getParcelableArrayList("SongList");

        textContent = intent.getStringExtra("CurrentSong");
        textView.setText(textContent);
        textView.setSelected(true);

        position = intent.getIntExtra("position", 0);
        playSong();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        updateSeek = new Thread() {
            @Override
            public void run() {
                try {
                    while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        sleep(800);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        play.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                play.setImageResource(R.drawable.playbutton);
                mediaPlayer.pause();
            } else {
                play.setImageResource(R.drawable.pause);
                mediaPlayer.start();
            }
        });

        back.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                position = position > 0 ? position - 1 : songs.size() - 1;
                playSong();
            }
        });

        next.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                position = (position + 1) % songs.size();
                playSong();
            }
        });
    }

    private void playSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();


        textContent = songs.get(position).getName().replace(".mp3","");
        textView.setText(textContent);

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(0);

        play.setImageResource(R.drawable.pause);

        mediaPlayer.setOnCompletionListener(mp -> next.performClick());
    }
}
