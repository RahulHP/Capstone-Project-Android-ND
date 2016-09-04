package io.github.rahulhp.dailyjournal;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by rahulhp on 4/9/16.
 */
public class EntryViewHolder extends RecyclerView.ViewHolder{
    public TextView entryView;
    public TextView dateView;
    public EntryViewHolder(View itemView) {
        super(itemView);
        dateView = (TextView) itemView.findViewById(R.id.journal_entry_date);
        entryView = (TextView) itemView.findViewById(R.id.journal_entry_text);
    }
}