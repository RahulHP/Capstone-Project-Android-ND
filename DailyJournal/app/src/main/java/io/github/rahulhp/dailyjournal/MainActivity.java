package io.github.rahulhp.dailyjournal;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class MainActivity extends FirebaseActivity{


    public static class EntryViewHolder extends RecyclerView.ViewHolder{
        public TextView entryView;
        public TextView dateView;
        public EntryViewHolder(View itemView) {
            super(itemView);
            dateView = (TextView) itemView.findViewById(R.id.journal_entry_date);
            entryView = (TextView) itemView.findViewById(R.id.journal_entry_text);
        }
    }
    private static final String TAG="MainActivity";

    public static final String ANONYMOUS = "anonymous";

    Calendar cal = Calendar.getInstance();
    int cYear = cal.get(Calendar.YEAR);
    int cMonth = cal.get(Calendar.MONTH);
    int cDay = cal.get(Calendar.DAY_OF_MONTH);

    private RecyclerView mEntryRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<JournalEntry,EntryViewHolder> mFirebaseAdapter;

    private ProgressBar mProgressBar;

    private DatabaseReference mFirebaseDatabaseReference;



    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        String data_string = "user-data"+"/"+mUID+"/"+cYear+"/"+"7";
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(data_string);
        Log.e(TAG, "onCreate: "+data_string );
        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                //Integer test = dataSnapshot.getValue(Integer.class);
                if (!dataSnapshot.exists()){
                    Log.e(TAG, "onDataChange: No entries");
                    TextView emptylist = (TextView) findViewById(R.id.empty_list_textview);
                    emptylist.setVisibility(TextView.VISIBLE);
                    Toast.makeText(getApplication(), "No Entries Present", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "onDataChange: Creating View");
                    createRecyclerView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void newEntry(View view){
        startActivity(new Intent(this,NewEntryActivity.class));
    }


    void createRecyclerView(){
        mEntryRecyclerView = (RecyclerView) findViewById(R.id.entryRecyclerView);
        mEntryRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mEntryRecyclerView.setLayoutManager(mLinearLayoutManager);
        mFirebaseAdapter = new FirebaseRecyclerAdapter<JournalEntry, EntryViewHolder>(
                JournalEntry.class,
                R.layout.journal_entry,
                EntryViewHolder.class,
                mFirebaseDatabaseReference
        ) {

            @Override
            protected void populateViewHolder(EntryViewHolder viewHolder, JournalEntry model, int position) {
                Calendar mDate = Calendar.getInstance();
                mDate.setTime(model.getmDate());
                String t = new SimpleDateFormat("dd-MMM-yy").format(mDate.getTime());
                viewHolder.dateView.setText(t);
                viewHolder.entryView.setText(model.getmEntry());
            }

        };

        mEntryRecyclerView.setAdapter(mFirebaseAdapter);
    }
    /*
    void createTempData(){
        Calendar cal = Calendar.getInstance();
        int cYear = cal.get(Calendar.YEAR);
        int cMonth = cal.get(Calendar.MONTH);
        int cDay = cal.get(Calendar.DAY_OF_MONTH);
        String data_string = "user-data"+"/"+mUID+"/"+cYear+"/"+cMonth+"/"+cDay;
        Log.e(TAG, "createTempData: DOING" );
        DatabaseReference myRef = mFirebaseDatabase.getReference(data_string);
        myRef.setValue("DONE");
        Log.e(TAG, "createTempData: DONE" );
    }

    void seedJson(){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = formatter.parse("2016-08-12");
            Date endDate = formatter.parse("2016-08-23");
            Calendar start = Calendar.getInstance();
            start.setTime(startDate);
            Calendar end = Calendar.getInstance();
            end.setTime(endDate);

            for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
                // Do your job here with `date`.
                int year = 1900+date.getYear();
                String data_string= "user-data"+"/"+mUID+"/"+year+"/"+date.getMonth()+"/"+
                        date.getDate();
                JournalEntry jEntry = new JournalEntry(date,"YAY",Boolean.TRUE);
                mFirebaseDatabase.getReference(data_string).setValue(jEntry);
                Log.e(TAG, "seedJson: "+date );
            }
        } catch (Exception e){
            Log.e(TAG, "seedJson: Oops" );
        }

    }*/


}
