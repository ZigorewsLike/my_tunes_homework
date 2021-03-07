package com.intercoder.mytunes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    SQLiteDatabase database;
    PlayListCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.gridView);
        DBHelperWithLoader helper = new DBHelperWithLoader(this);
        database = helper.getWritableDatabase();
        Cursor c = database.rawQuery("SELECT * FROM playlists", null);

        adapter = new PlayListCursorAdapter(this, c);
        gridView.setAdapter(adapter);

    }

    public void onClickAddPlaylist(View v){
        Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
        intent.putExtra("mode", "add");
        startActivity(intent);
    }
    @Override
    public void onResume() {
        super.onResume();
        Cursor c = database.rawQuery("SELECT * FROM playlists", null);
        adapter = new PlayListCursorAdapter(this, c);
        gridView.setAdapter(adapter);
    }
}