package com.ut.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * author : zhouyubin
 * time   : 2018/12/24
 * desc   :
 * version: 1.0
 */
@Entity(tableName = "search_record")
public class SearchRecord {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "word")
    private String word;

    public SearchRecord(@NonNull String word) {
        this.word = word;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return "SearchRecord{" +
                "id=" + id +
                ", word='" + word + '\'' +
                '}';
    }
}
