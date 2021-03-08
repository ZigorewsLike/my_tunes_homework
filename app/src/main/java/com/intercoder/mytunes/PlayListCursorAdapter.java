package com.intercoder.mytunes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayListCursorAdapter extends CursorAdapter {
    public PlayListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvName = view.findViewById(R.id.titlePlaylist);
        ImageView icon = view.findViewById(R.id.iconPlaylist);

        int[] icons = {R.drawable.playlist1, R.drawable.playlist2, R.drawable.playlist3,
                R.drawable.playlist4, R.drawable.playlist5, R.drawable.playlist6};
        String title = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        int index = cursor.getInt(cursor.getColumnIndexOrThrow("image_id"));
        long id_plst = cursor.getLong(cursor.getColumnIndex("_id"));
        tvName.setText(title);
        icon.setImageResource(icons[index]);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlaylistActivity.class);
                intent.putExtra("mode", "watch");
                intent.putExtra("image_ind", index);
                intent.putExtra("pl_name", title);
                intent.putExtra("id_playlist", id_plst);
                context.startActivity(intent);
            }
        });
    }
}
