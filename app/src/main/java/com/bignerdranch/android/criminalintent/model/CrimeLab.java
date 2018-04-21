package com.bignerdranch.android.criminalintent.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {


    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null)
            sCrimeLab = new CrimeLab(context);
        return sCrimeLab;
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }
    public void addCrime(Crime crime) {
        mCrimes.add(crime);
    }
    public void delCrime(Crime crime) {
        mCrimes.remove(crime);
    }
    public Crime getCrime(UUID id) {

        for (Crime item : mCrimes) {
            if (item.getId().equals(id))
                return item;
        }
        return null;

    }

    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            Crime item = new Crime();
//            item.setTitle("Crime #" + i);
//            item.setSolved(i % 2 == 0);
//            mCrimes.add(item);
//        }
    }


}
