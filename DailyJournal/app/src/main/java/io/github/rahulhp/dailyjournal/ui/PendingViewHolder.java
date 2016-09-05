package io.github.rahulhp.dailyjournal.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import io.github.rahulhp.dailyjournal.R;

/**
 * Created by rahulhp on 5/9/16.
 */
public class PendingViewHolder extends RecyclerView.ViewHolder{
    public TextView requestIdView;
    public ImageButton acceptView;
    public ImageButton rejectView;
    public PendingViewHolder (View itemView) {
        super(itemView);
        requestIdView = (TextView) itemView.findViewById(R.id.pending_friend_id);
        acceptView = (ImageButton) itemView.findViewById(R.id.accept_request);
        rejectView = (ImageButton) itemView.findViewById(R.id.reject_request);
    }
}