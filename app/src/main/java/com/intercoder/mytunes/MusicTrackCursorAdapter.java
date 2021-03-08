package com.intercoder.mytunes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicTrackCursorAdapter extends CursorAdapter {
    public MusicTrackCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.music_track_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvName = view.findViewById(R.id.title);
        TextView tvArtist = view.findViewById(R.id.artist);
        TextView tvDuration = view.findViewById(R.id.duration);
        ImageView iconDelete = view.findViewById(R.id.imageDelete);

        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        String artist = cursor.getString(cursor.getColumnIndexOrThrow("artist"));
        String year = cursor.getString(cursor.getColumnIndexOrThrow("year"));
        int duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
        tvDuration.setText(String.format("%2d:%02d", (duration / 60), (duration % 60)));

        long id_music = cursor.getLong(cursor.getColumnIndex("_id"));
        iconDelete.setTag(id_music);
        tvName.setText(title);
        tvArtist.setText((String)(artist + " \u2022 " + year));
    }

}
