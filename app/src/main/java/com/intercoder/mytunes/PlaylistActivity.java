package com.intercoder.mytunes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class PlaylistActivity extends AppCompatActivity {
    String mode = "watch";
    ImageView icon;
    TextView playListName;
    EditText playListEditName;
    ListView listView;
    Button saveOrAdd, cancelOrDelete;
    SQLiteDatabase database;
    int icon_index;
    int[] icons = {R.drawable.playlist1, R.drawable.playlist2, R.drawable.playlist3,
            R.drawable.playlist4, R.drawable.playlist5, R.drawable.playlist6};
    long idPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        icon = findViewById(R.id.iconPlaylist);
        playListName = findViewById(R.id.playlistName);
        playListEditName = findViewById(R.id.editTextName);
        listView = findViewById(R.id.listTracks);
        saveOrAdd = findViewById(R.id.saveOrAdd);
        cancelOrDelete = findViewById(R.id.cancelOrDelete);

        idPlaylist = getIntent().getLongExtra("id_playlist", -1);
        DBHelperWithLoader helper = new DBHelperWithLoader(this);
        database = helper.getWritableDatabase();
        Log.i("id playlist", String.valueOf(idPlaylist));
        updateMusicTracks();

        mode = getIntent().getStringExtra("mode");
        icon_index = getIntent().getIntExtra("image_ind", 0);
        Random r = new Random();
        if (mode.equals("add")) {
            icon_index = r.nextInt(6);
            playListName.setVisibility(View.GONE);
            playListEditName.setVisibility(View.VISIBLE);
            saveOrAdd.setText("Сохранить");
            saveOrAdd.setTag("save");
            cancelOrDelete.setText("Отмена");
            cancelOrDelete.setTag("cancel");
        }else{
            playListName.setText(getIntent().getStringExtra("pl_name"));
            saveOrAdd.setText("Добавить новый трек");
            saveOrAdd.setTag("add");
            cancelOrDelete.setText("Удалить плейлист");
            cancelOrDelete.setTag("delete");
            playListEditName.setVisibility(View.GONE);
            playListName.setVisibility(View.VISIBLE);
        }
        icon.setImageResource(icons[icon_index]);
    }

    public void onClickAddTrack(View v){
        if(v.getTag().equals("add")){
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.alert_new_track, null);
            EditText[] fields = {promptsView.findViewById(R.id.editTextTitle),
                    promptsView.findViewById(R.id.editTextArtist),
                    promptsView.findViewById(R.id.editTextYear),
                    promptsView.findViewById(R.id.editTextDuration)};
            Log.i("EditField", fields[0].toString() + " " + fields[3].toString());
            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
            mDialogBuilder.setView(promptsView);
            mDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Добавить",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    for(EditText textFiled : fields){
                                        if(textFiled.getText().toString().equals("")){
                                            return;
                                        }
                                    }
                                    ContentValues values = new ContentValues();
                                    values.put("title", fields[0].getText().toString());
                                    values.put("artist", fields[1].getText().toString());
                                    values.put("year", fields[2].getText().toString());
                                    values.put("id_playlist", idPlaylist);
                                    values.put("duration", fields[3].getText().toString());
                                    database.insert("music_track", null, values);
                                    updateMusicTracks();
                                }
                            })
                    .setNegativeButton("Отмена",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog = mDialogBuilder.create();
            alertDialog.show();
        }else{
            if(!playListEditName.getText().toString().equals("")){
                playListEditName.setVisibility(View.GONE);
                playListName.setText(playListEditName.getText().toString());
                saveOrAdd.setText("Добавить новый трек");
                saveOrAdd.setTag("add");
                playListEditName.setVisibility(View.GONE);
                playListName.setVisibility(View.VISIBLE);
                ContentValues values = new ContentValues();
                values.put("name", playListEditName.getText().toString());
                values.put("image_id", icon_index);

                idPlaylist = database.insert("playlists", null, values);
            }
        }
    }

    public void updateMusicTracks(){
        Cursor c = database.rawQuery("SELECT * FROM music_track WHERE id_playlist == " + idPlaylist, null);
        String[] playlist_fields = c.getColumnNames();
        int[] views = { R.id.id, R.id.artist, R.id.title, R.id.year, R.id.duration, R.id.id_plst };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.music_track_item, c,
                playlist_fields, views);
        listView.setAdapter(adapter);
    }

    public void changeImage(View v){
        if(mode.equals("add")){
            Random r = new Random();
            int l = r.nextInt(6);
            if(l != icon_index) icon_index = (icon_index + 1) % 6;
            icon.setImageResource(icons[icon_index]);
        }
    }

    public void onClickDeletePlaylist(View v){
        if(v.getTag().equals("delete")){
            database.delete("playlists", "_id="+idPlaylist, null);
        }
        finish();
    }
}