package com.bignerdranch.android.criminalintent.model;

import com.bignerdranch.android.criminalintent.util.UUID2BytesConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import java.util.UUID;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class Crime {
    @Id(autoincrement = true)
    private Long uId;

    @Unique
    @NotNull
    @Convert(converter = UUID2BytesConverter.class, columnType = String.class)
    private UUID id;

    @NotNull
    private String title;
    @NotNull
    private Date date;
    private boolean solved;

    public Crime() {
        this.id = UUID.randomUUID();
        this.date = new Date();
    }

    private String suspect;

    @Generated(hash = 103115614)
    public Crime(Long uId, @NotNull UUID id, @NotNull String title,
                 @NotNull Date date, boolean solved, String suspect) {
        this.uId = uId;
        this.id = id;
        this.title = title;
        this.date = date;
        this.solved = solved;
        this.suspect = suspect;
    }

    public UUID getId() {
        return id;

    }

//    public void setId(UUID id) {
//        this.id = id;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean getSolved() {
        return this.solved;
    }

    public Long getUId() {
        return this.uId;
    }

    public void setUId(Long uId) {
        this.uId = uId;
    }

    public String getSuspect() {
        return this.suspect;
    }

    public void setSuspect(String suspect) {
        this.suspect = suspect;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
