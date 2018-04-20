package com.bignerdranch.android.criminalintent;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.bignerdranch.android.criminalintent.model.Crime;
import com.bignerdranch.android.criminalintent.model.CrimeLab;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeFragment extends Fragment {

    public static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private Crime crime;

    private EditText edCrimeTitle;
    private Button btnCrimeDate;
    private CheckBox cbCrimeSolved;
    private static final String TAG = "CrimeFragment";

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public CrimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

//        Intent intent = getActivity().getIntent();
//        UUID id = (UUID) intent.getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);

        UUID id = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        crime = CrimeLab.get(getActivity()).getCrime(id);

        //返回修改的数据
        Intent intent = new Intent();
        intent.putExtra(ARG_CRIME_ID, crime.getId());
        getActivity().setResult(Activity.RESULT_OK, intent);


    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        edCrimeTitle = (EditText) v.findViewById(R.id.et_crime_title);
        btnCrimeDate = (Button) v.findViewById(R.id.btn_crime_date);
        cbCrimeSolved = (CheckBox) v.findViewById(R.id.cb_crime_solved);

        edCrimeTitle.setText(crime.getTitle());
        edCrimeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnCrimeDate.setText(crime.getDate().toString());
//        btnCrimeDate.setEnabled(false);
        btnCrimeDate.setOnClickListener((v1) -> {
            android.support.v4.app.FragmentManager manager = getFragmentManager();
            DatePickerFragment dialog = new DatePickerFragment();
            dialog.show(manager, DIALOG_DATE);
        });

        cbCrimeSolved.setChecked(crime.isSolved());
        cbCrimeSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
            }
        });

        return v;
    }

}
