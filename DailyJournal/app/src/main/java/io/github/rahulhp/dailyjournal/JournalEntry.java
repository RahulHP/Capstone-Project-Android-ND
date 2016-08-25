package io.github.rahulhp.dailyjournal;

import java.util.Date;

/**
 * Created by root on 24/8/16.
 */
public class JournalEntry {
    private Date mDate;
    private String mEntry;
    private Boolean mPrivate;

    public JournalEntry() {

    }

    public JournalEntry(Date mDate, String mEntry, Boolean mPrivate){
        this.setmDate(mDate);
        this.setmEntry(mEntry);
        this.setmPrivate(mPrivate);
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public String getmEntry() {
        return mEntry;
    }

    public void setmEntry(String mEntry) {
        this.mEntry = mEntry;
    }

    public Boolean getmPrivate() {
        return mPrivate;
    }

    public void setmPrivate(Boolean mPrivate) {
        this.mPrivate = mPrivate;
    }
}