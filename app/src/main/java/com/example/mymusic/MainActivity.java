package com.example.mymusic;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        // Request storage permission
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                        if (mySongs.isEmpty()) {
                            Toast.makeText(MainActivity.this, "No songs found!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Extract song names
                        ArrayList<String> songTitles = new ArrayList<>();
                        for (File song : mySongs) {
                            songTitles.add(song.getName().replace(".mp3", ""));
                        }

                        // Set adapter
                        MusicListAdapter adapter = new MusicListAdapter(MainActivity.this, songTitles);
                        listView.setAdapter(adapter);

                        // Set item click listener
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            Intent intent = new Intent(MainActivity.this, PlaySong.class);
                            intent.putExtra("SongList", mySongs);
                            intent.putExtra("CurrentSong", songTitles.get(position));
                            intent.putExtra("position", position);
                            startActivity(intent);
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest request, PermissionToken token) {
                        Toast.makeText(MainActivity.this, "Please grant the required permission", Toast.LENGTH_SHORT).show();
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * Recursively fetches all .mp3 files from the given directory.
     *
     * @param file The root directory to search.
     * @return A list of .mp3 files.
     */
    private ArrayList<File> fetchSongs(File file) {
        ArrayList<File> songList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File currentFile : files) {
                if (currentFile.isDirectory() && !currentFile.isHidden()) {
                    songList.addAll(fetchSongs(currentFile)); // Recurse into directories
                } else if (currentFile.getName().endsWith(".mp3") && !currentFile.getName().startsWith(".")) {
                    songList.add(currentFile);
                }
            }
        }
        return songList;
    }
}
