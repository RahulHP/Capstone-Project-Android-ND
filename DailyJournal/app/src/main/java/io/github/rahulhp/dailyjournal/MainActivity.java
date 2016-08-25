package io.github.rahulhp.dailyjournal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    public static class EntryViewHolder extends RecyclerView.ViewHolder{
        public TextView entryView;

        public EntryViewHolder(View itemView) {
            super(itemView);
            entryView = (TextView) itemView.findViewById(R.id.journal_entry_text);
        }
    }
    private static final String TAG="MainActivity";

    private String mUsername;
    private String mUID;
    public static final String ANONYMOUS = "anonymous";

    Calendar cal = Calendar.getInstance();
    int cYear = cal.get(Calendar.YEAR);
    int cMonth = cal.get(Calendar.MONTH);
    int cDay = cal.get(Calendar.DAY_OF_MONTH);

    private RecyclerView mEntryRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<JournalEntry,EntryViewHolder> mFirebaseAdapter;

    private ProgressBar mProgressBar;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mUsername=ANONYMOUS;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        if (mFirebaseUser == null){
            startActivity(new Intent(this,SignInActivity.class));
            finish();
            return;
        } else {
            Log.d(TAG, "onCreate: "+mFirebaseUser.getDisplayName());
            mUID = mFirebaseUser.getUid();
            checkNewUser();
            //createTempData();
        }





        String data_string = "user-data"+"/"+mUID+"/"+cYear+"/"+cMonth;
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(data_string);

        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Integer test = dataSnapshot.getValue(Integer.class);
                if (!dataSnapshot.exists()){
                    Log.e(TAG, "onDataChange: No entries");
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
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
        mFirebaseDatabaseReference.keepSynced(true);



        //seedJson();
    }

    void newEntry(View view){
        startActivity(new Intent(this,NewEntry.class));
    }

    void checkNewUser(){
        final String user_string = "users/"+mUID;
        mFirebaseDatabase.getReference(user_string).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Integer userExists = dataSnapshot.getValue(Integer.class);
                        if (userExists==null){
                            mFirebaseDatabase.getReference(user_string).setValue(1);
                        } else {
                            Log.e(TAG, "on1DataChange: User exists" );
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
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
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.entryView.setText(model.getmEntry());
            }
        };

        mEntryRecyclerView.setAdapter(mFirebaseAdapter);
    }
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
        
    }


}
