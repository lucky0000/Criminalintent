package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.bignerdranch.android.criminalintent.base.SingleFragmentActivity;
import com.bignerdranch.android.criminalintent.model.Crime;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity implements CrimeFragment.Callbacks{

    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalinten.CrimeActivity.crimeid";

    @Override
    protected Fragment createFragment() {
        //return new CrimeFragment();
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);

    }

    public static Intent getIntent(Context context, UUID id) {
        Intent intent = new Intent(context, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, id);
        return intent;
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
//        if(crime.getUId()==0)
//        {
//            this.setContentView(R.layout.activity_empty_data);
//
//        }
    }
}
