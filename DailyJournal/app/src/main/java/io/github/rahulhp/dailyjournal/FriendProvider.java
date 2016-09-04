package io.github.rahulhp.dailyjournal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tjeannin.provigen.ProviGenOpenHelper;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.helper.TableBuilder;
import com.tjeannin.provigen.model.Constraint;

/**
 * Created by rahulhp on 30/8/16.
 */
public class FriendProvider extends ProviGenProvider {
    private static Class[] contracts = new Class[]{FriendContract.class};

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return new ProviGenOpenHelper(getContext(),"dbName",null,1,contracts){
            @Override
            public void onCreate(SQLiteDatabase database) {
                new TableBuilder(FriendContract.class)
                        .addConstraint(FriendContract.ID_COLUMN, Constraint.UNIQUE, Constraint.OnConflict.IGNORE)
                        .createTable(database);
            }
        };
    }

    @Override
    public Class[] contractClasses() {
        return new Class[]{FriendContract.class};
    }
}
