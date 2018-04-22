package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.criminalintent.model.Crime;
import com.bignerdranch.android.criminalintent.model.CrimeLab;

import java.util.List;
import java.util.UUID;

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private static final String TAG = "CrimeListFragment";

    private boolean mSubtitleVisible;

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        //从详情页返回后 需要更新列表显示新的内容
        updateUI();
    }

    /**
     * 创建工具栏上的菜单
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem item = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible)
            item.setTitle(R.string.hide_subtitle);
        else
            item.setTitle(R.string.show_subtitle);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {

            if (resultCode == Activity.RESULT_OK) {
                UUID id = (UUID) data.getSerializableExtra(CrimeFragment.ARG_CRIME_ID);
                //mAdapter.notifyDataSetChanged(0);
//                Toast.makeText(getActivity().getApplicationContext(), id.toString(), Toast.LENGTH_SHORT);
//                Toast.makeText(getActivity().getApplicationContext(), "a", Toast.LENGTH_SHORT);
//                Toast.makeText(getActivity(), "b", Toast.LENGTH_SHORT);
                Log.d(TAG, "onActivityResult: " + id.toString());
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //启动菜单
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (crimes.size() < 1) {
            //没有数据时 显示一个提示
            getActivity().setContentView(R.layout.activity_empty_data);

        } else {

            if (mAdapter == null) {
                mAdapter = new CrimeAdapter(crimes);
                mCrimeRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setCrimes(crimes);
                mAdapter.notifyDataSetChanged();
            }

        }
        updateSubtitle();
    }

    private void updateSubtitle() {
        String subtitle;
        if (mSubtitleVisible) {
            int count = CrimeLab.get(getActivity()).getCrimes().size();
            subtitle = getString(R.string.subtitle_format, count);
        } else
            subtitle = null;
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    /**
     * 菜单项选择事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                crime.setTitle("title:" + Math.round(Math.random() * 1000));
                CrimeLab.get(getActivity()).addCrime(crime);

                Intent intent = CrimePagerActivity.getIntent(getActivity(), crime.getId());
                startActivityForResult(intent, 0);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }


    }

    private class CrimeHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle;
        private TextView txtDate;
        private ImageView ivSolved;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));

            //单项点击事件
            itemView.setOnClickListener(v -> {
//                Toast.makeText(getActivity(), mCrime.getTitle() + " click", Toast.LENGTH_SHORT).show();
//                Intent intent = CrimeActivity.getIntent(getActivity(), mCrime.getId());
                Intent intent = CrimePagerActivity.getIntent(getActivity(), mCrime.getId());
//                startActivity(intent);
                startActivityForResult(intent, 0);

            });

            txtTitle = (TextView) itemView.findViewById(R.id.crime_title);
            txtDate = (TextView) itemView.findViewById(R.id.crime_date);
            ivSolved = (ImageView) itemView.findViewById(R.id.crime_solved);

        }


        private Crime mCrime;

        public void bind(Crime crime) {
            mCrime = crime;
            txtTitle.setText(mCrime.getTitle());
            txtDate.setText(DateFormat.format("yyyy-MM-dd kk:mm:ss", mCrime.getDate()).toString());
            ivSolved.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }


        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }


}
