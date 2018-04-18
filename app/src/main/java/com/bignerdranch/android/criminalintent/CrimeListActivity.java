package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

import com.bignerdranch.android.criminalintent.base.SingleFragmentActivity;

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
