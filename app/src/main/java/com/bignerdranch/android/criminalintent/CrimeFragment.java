package com.bignerdranch.android.criminalintent;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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

    private Crime crime;

    private EditText edCrimeTitle;
    private Button btnCrimeDate;
    private CheckBox cbCrimeSolved;

    public CrimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        UUID id = (UUID) intent.getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
        crime = CrimeLab.get(getActivity()).getCrime(id);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        btnCrimeDate.setEnabled(false);
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
