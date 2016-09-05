package io.github.rahulhp.dailyjournal.data;

import android.net.Uri;

import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;

/**
 * Created by root on 30/8/16.
 */
public interface FriendContract extends ProviGenBaseContract{

    @Column(Column.Type.TEXT)
    public static final String ID_COLUMN = "user";

    @ContentUri
    public static final Uri Content_Uri = Uri.parse("content://io.github.rahulhp.dailyjournal/friends");

}
