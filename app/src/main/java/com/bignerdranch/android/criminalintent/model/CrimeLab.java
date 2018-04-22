package com.bignerdranch.android.criminalintent.model;

import android.content.Context;

import com.bignerdranch.android.criminalintent.base.BaseApplication;
import com.bignerdranch.android.criminalintent.dao.CrimeDao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {


    private static CrimeLab sCrimeLab;
    //private List<Crime> mCrimes;

    public static CrimeLab get(Context context) {

        if (sCrimeLab == null)
            sCrimeLab = new CrimeLab(context);
        return sCrimeLab;
    }

    public List<Crime> getCrimes() {

        return BaseApplication.getDaoInstant().getCrimeDao().loadAll();
//        return mCrimes;
    }

    public void addCrime(Crime crime) {
        BaseApplication.getDaoInstant().getCrimeDao().insert(crime);
//        mCrimes.add(crime);
    }

    public void updateCrime(Crime crime) {
        BaseApplication.getDaoInstant().getCrimeDao().update(crime);
//        mCrimes.add(crime);
    }

    public void delCrime(Crime crime) {
        BaseApplication.getDaoInstant().getCrimeDao().delete(crime);

//        mCrimes.remove(crime);
    }

    public Crime getCrime(UUID id) {
        List<Crime> c = BaseApplication.getDaoInstant().getCrimeDao().queryBuilder().where(CrimeDao.Properties.Id.eq(id)).list();
        if (c.size() == 1)
            return c.get(0);
        else
            return null;

//        for (Crime item : mCrimes) {
//            if (item.getId().equals(id))
//                return item;
//        }
//        return null;

    }

    private CrimeLab(Context context) {
        //mCrimes = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            Crime item = new Crime();
//            item.setTitle("Crime #" + i);
//            item.setSolved(i % 2 == 0);
//            mCrimes.add(item);
//        }
    }


}
