package io.github.rahulhp.dailyjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

import io.github.rahulhp.dailyjournal.BaseActivities.FirebaseActivity;
import io.github.rahulhp.dailyjournal.ui.JournalEntry;

public class NewEntryActivity extends FirebaseActivity {

    TextInputEditText newEntryText;
    TextView sharingText;
    Switch mPrivacySwitch;
    Button mSaveEntryButton;
    String data_string;
    String entry_text;
    JournalEntry jEntry;

    Calendar cal = Calendar.getInstance();
    int cYear = cal.get(Calendar.YEAR);
    int cMonth = cal.get(Calendar.MONTH);
    int cDay = cal.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.activity_title_new_entry));
        sharingText = (TextView) findViewById(R.id.shared_boolean_text);
        mPrivacySwitch = (Switch) findViewById(R.id.privacy_switch);
        mPrivacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    sharingText.setText(getString(R.string.new_entry_private));
                    sharingText.setContentDescription(getString(R.string.new_entry_private));
                } else {
                    sharingText.setText(getString(R.string.new_entry_public));
                    sharingText.setContentDescription(getString(R.string.new_entry_public));
                }
            }
        });

        newEntryText = (TextInputEditText) findViewById(R.id.new_entry_textbox);
        mSaveEntryButton = (Button) findViewById(R.id.save_entry_button);
        mSaveEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date mDate = new Date();
                entry_text = newEntryText.getText().toString();
                JournalEntry jEntry = new JournalEntry(mDate,entry_text,mPrivacySwitch.isChecked());
                mFirebaseDatabase.getReference(data_string).setValue(jEntry);
                Toast.makeText(getBaseContext(), getString(R.string.toast_entry_saved), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getBaseContext(),MainActivity.class));
            }
        });


        data_string= getString(R.string.database_user_posts_url)+"/"+mUID+"/"+cYear+"/"+cMonth+"/"+cDay;
        getCurrentSavedEntry();
    }

    void getCurrentSavedEntry(){
        mFirebaseDatabase.getReference(data_string).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        jEntry = dataSnapshot.getValue(JournalEntry.class);
                        if (jEntry!=null){
                            newEntryText.setText(jEntry.getmEntry(), TextView.BufferType.EDITABLE);
                            mPrivacySwitch.setChecked(jEntry.getmPrivate());
                        } else {
                            newEntryText.setText("", TextView.BufferType.EDITABLE);
                            mPrivacySwitch.setChecked(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

    }


}
