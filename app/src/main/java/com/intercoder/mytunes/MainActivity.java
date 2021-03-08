package com.intercoder.mytunes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;

import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    SQLiteDatabase database;
    PlayListCursorAdapter adapter;
    String stringSort = "";
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.gridView);
        spinner = findViewById(R.id.spinner);
        DBHelperWithLoader helper = new DBHelperWithLoader(this);
        database = helper.getWritableDatabase();
        Cursor c = database.rawQuery("SELECT * FROM playlists", null);

        adapter = new PlayListCursorAdapter(this, c);
        gridView.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0: stringSort = ""; break;
                    case 1: stringSort = " ORDER BY name"; break;
                    case 2: stringSort = " ORDER BY name DESC"; break;
                }
                onResume();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    public void onClickAddPlaylist(View v){
        Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
        intent.putExtra("mode", "add");
        startActivity(intent);
    }
    @Override
    public void onResume() {
        super.onResume();
        Cursor c = database.rawQuery("SELECT * FROM playlists" + stringSort, null);
        adapter = new PlayListCursorAdapter(this, c);
        gridView.setAdapter(adapter);
    }
}