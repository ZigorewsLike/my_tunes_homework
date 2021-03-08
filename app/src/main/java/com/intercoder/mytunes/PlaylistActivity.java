package com.intercoder.mytunes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class PlaylistActivity extends AppCompatActivity {
    String mode = "watch", stringSort = "";
    ImageView icon;
    TextView playListName, musicDuration, musicCount;
    EditText playListEditName;
    ListView listView;
    Button saveOrAdd, cancelOrDelete;
    SQLiteDatabase database;
    int icon_index;
    Resources res;
    int[] icons = {R.drawable.playlist1, R.drawable.playlist2, R.drawable.playlist3,
            R.drawable.playlist4, R.drawable.playlist5, R.drawable.playlist6};
    long idPlaylist;
    Spinner spinner;

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
        musicCount = findViewById(R.id.labelCount);
        musicDuration = findViewById(R.id.labelDuration);
        spinner = findViewById(R.id.spinner);
        res = getResources();

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
            saveOrAdd.setText(res.getString(R.string.button_save));
            saveOrAdd.setTag("save");
            cancelOrDelete.setText(res.getString(R.string.button_cancel));
            cancelOrDelete.setTag("cancel");
        }else{
            playListName.setText(getIntent().getStringExtra("pl_name"));
            saveOrAdd.setText(res.getString(R.string.button_add_new_track));
            saveOrAdd.setTag("add");
            cancelOrDelete.setText(res.getString(R.string.button_delete_playlist));
            cancelOrDelete.setTag("delete");
            playListEditName.setVisibility(View.GONE);
            playListName.setVisibility(View.VISIBLE);
        }
        icon.setImageResource(icons[icon_index]);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0: stringSort = ""; break;
                    case 1: stringSort = " ORDER BY title"; break;
                    case 2: stringSort = " ORDER BY title DESC"; break;
                    case 3: stringSort = " ORDER BY artist"; break;
                    case 4: stringSort = " ORDER BY artist DESC"; break;
                    case 5: stringSort = " ORDER BY duration"; break;
                    case 6: stringSort = " ORDER BY duration DESC"; break;
                }
                updateMusicTracks();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
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
                saveOrAdd.setText(res.getString(R.string.button_add_new_track));
                saveOrAdd.setTag("add");
                cancelOrDelete.setText(res.getString(R.string.button_delete_playlist));
                cancelOrDelete.setTag("delete");
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
        Cursor c = database.rawQuery("SELECT * FROM music_track WHERE id_playlist == " + idPlaylist + stringSort, null);
        MusicTrackCursorAdapter adapter = new MusicTrackCursorAdapter(this, c);
        listView.setAdapter(adapter);
        int duration = 0;
        int count = 0;
        if(c.moveToFirst()){
            do{
                count++;
                duration += c.getInt(c.getColumnIndexOrThrow("duration"));
            }while(c.moveToNext());
        }
        musicDuration.setText(String.format("%02d:%02d:%02d", (duration / 3600), ((duration / 60) % 60), (duration % 60)));
        musicCount.setText((String)(count + " треков"));
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

    public void onClickDeleteMusic(View v){
        long id_music = (long) v.getTag();
        database.delete("music_track", "_id="+id_music, null);
        updateMusicTracks();
    }
}