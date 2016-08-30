package io.github.rahulhp.dailyjournal;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.tjeannin.provigen.ProviGenOpenHelper;
import com.tjeannin.provigen.ProviGenProvider;

import io.github.rahulhp.dailyjournal.FriendContract;

/**
 * Created by root on 30/8/16.
 */
public class FriendProvider extends ProviGenProvider {
    private static Class[] contracts = new Class[]{FriendContract.class};

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return new ProviGenOpenHelper(getContext(),"dbName",null,1,contracts);
    }

    @Override
    public Class[] contractClasses() {
        return new Class[]{FriendContract.class};
    }
}
