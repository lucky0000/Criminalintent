package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.bignerdranch.android.criminalintent.base.SingleFragmentActivity;
import com.bignerdranch.android.criminalintent.model.Crime;

public class CrimeListActivity extends SingleFragmentActivity

        implements CrimeListFragment.Callbacks ,CrimeFragment.Callbacks{
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.getIntent(this, crime.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container,newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment crimeListFragment = (CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        crimeListFragment.updateUI();

        //删除的时候 详细则不显示了
        if(crime==null)
        {
            Fragment crimeFragment = (CrimeFragment) getSupportFragmentManager().findFragmentById(R.id.detail_fragment_container);
//            crimeFragment.getLayoutInflater().inflate()
            if(crimeFragment!=null){

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_fragment_container,new Fragment())
                        .commit();
            }

        }

    }
}
