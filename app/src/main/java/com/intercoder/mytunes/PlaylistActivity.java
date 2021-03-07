package com.intercoder.mytunes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class PlaylistActivity extends AppCompatActivity {
    String mode = "watch";
    ImageView icon;
    TextView playListName;
    EditText playListEditName;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        icon = findViewById(R.id.iconPlaylist);
        playListName = findViewById(R.id.playlistName);
        playListEditName = findViewById(R.id.editTextName);
        listView = findViewById(R.id.listTracks);

        int[] icons = {R.drawable.playlist1, R.drawable.playlist2, R.drawable.playlist3,
                R.drawable.playlist4, R.drawable.playlist5, R.drawable.playlist6};
        mode = getIntent().getStringExtra("mode");
        Random r = new Random();
        switch (mode){
            case "add":
                icon.setImageResource(icons[r.nextInt(6)]);
                playListName.setVisibility(View.GONE);
                playListEditName.setVisibility(View.VISIBLE);
                break;
            case "watch":
                icon.setImageResource(icons[getIntent().getIntExtra("image_ind", 0)]);
                playListName.setText(getIntent().getStringExtra("pl_name"));
                playListEditName.setVisibility(View.GONE);
                playListName.setVisibility(View.VISIBLE);
            default: break;
        }
    }
}