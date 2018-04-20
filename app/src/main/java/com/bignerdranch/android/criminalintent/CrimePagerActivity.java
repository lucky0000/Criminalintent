package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bignerdranch.android.criminalintent.model.Crime;
import com.bignerdranch.android.criminalintent.model.CrimeLab;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {
    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalinten.CrimeActivity.crimeid";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private Button btnJumpToFirst;
    private Button btnJumpToLast;


    public static Intent getIntent(Context context, UUID id) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        mCrimes = CrimeLab.get(this).getCrimes();
        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);
        btnJumpToFirst = (Button) findViewById(R.id.btnJumpToFirst);
        btnJumpToLast = (Button) findViewById(R.id.btnJumpToLast);

//        btnJumpToFirst.setVisibility(View.VISIBLE);
//        btnJumpToLast.setVisibility(View.VISIBLE);

        btnJumpToFirst.setOnClickListener(v -> {
//            btnJumpToFirst.setVisibility(View.INVISIBLE);
//            btnJumpToLast.setVisibility(View.VISIBLE);
            mViewPager.setCurrentItem(0);
        });
        btnJumpToLast.setOnClickListener(v -> {
//            btnJumpToFirst.setVisibility(View.VISIBLE);
//            btnJumpToLast.setVisibility(View.INVISIBLE);
            mViewPager.setCurrentItem(mCrimes.size() - 1);
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            mViewPager.setOnScrollChangeListener((x, c, v, b, n) -> {
//
//
//            });
//        }


        //设置显示哪个 不设置则始终显示第一项
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);

//                if (i == 0)
//                    btnJumpToFirst.setVisibility(View.INVISIBLE);
//                else if (i == mCrimes.size() - 1)
//                    btnJumpToLast.setVisibility(View.INVISIBLE);

                break;
            }
        }

    }
}
