package com.bignerdranch.android.criminalintent;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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

import java.util.Date;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeFragment extends Fragment {

    public static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    //时间
    private static final int REQUEST_DATA = 0;
    //联系人
    private static final int REQUEST_CONTACT = 1;

    private Crime crime;

    private EditText edCrimeTitle;
    private Button btnCrimeDate;
    private Button btnCrimeDelete;
    private Button btnReport;
    private Button btnSuspect;
    private CheckBox cbCrimeSolved;
    private static final String TAG = "CrimeFragment";

    /**
     * 实例化本身 用于其他activity传递数据进来
     * 父activity接收到intent的数据后 封装到这里传给fragment
     *
     * @param crimeId
     * @return
     */
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

    /**
     * 回调函数 用于接收时间弹框返回的数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + " " + resultCode);
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DATA && resultCode == Activity.RESULT_OK) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            try {
                if (c.getCount() == 0)
                    return;
                c.moveToFirst();
                String suspect = c.getString(0);
                crime.setSuspect(suspect);
                showCrime();

            } finally {
                c.close();
            }
        }


    }

    /**
     * 退出的时候保存修改的数据
     */
    @Override
    public void onPause() {
        super.onPause();

        //保存数据
        CrimeLab.get(getActivity()).updateCrime(crime);
    }

    /**
     * 显示时间
     */
    private void updateDate() {
        btnCrimeDate.setText(DateFormat.format("yyyy-MM-dd kk:mm:ss", crime.getDate()).toString());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

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
        btnCrimeDelete = (Button) v.findViewById(R.id.btn_crime_delete);
        btnReport = (Button) v.findViewById(R.id.crime_report);
        btnSuspect = (Button) v.findViewById(R.id.crime_suspect);
        cbCrimeSolved = (CheckBox) v.findViewById(R.id.cb_crime_solved);

        showCrime();

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

//        btnCrimeDate.setText(crime.getDate().toString());
//        updateDate();
//        btnCrimeDate.setEnabled(false);
        btnCrimeDate.setOnClickListener((v1) -> {
            android.support.v4.app.FragmentManager manager = getFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATA);
            dialog.show(manager, DIALOG_DATE);
        });

        cbCrimeSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
            }
        });

        btnCrimeDelete.setOnClickListener(v2 -> {
            CrimeLab.get(getContext()).delCrime(crime);
            getActivity().finish();
        });

        btnReport.setOnClickListener(v3 -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));

            i = Intent.createChooser(i, getString(R.string.send_report));

            startActivity(i);
        });

        btnSuspect.setOnClickListener(v3 -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));

            i = Intent.createChooser(i, getString(R.string.send_report));

            startActivity(i);
        });


        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        btnSuspect.setOnClickListener(v1 -> {
            startActivityForResult(pickContact, REQUEST_CONTACT);
        });

        //如果不存在指定的activity 则禁用按钮
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null)
            btnSuspect.setEnabled(false);


        return v;
    }

    private void showCrime() {
        cbCrimeSolved.setChecked(crime.isSolved());
        edCrimeTitle.setText(crime.getTitle());
        updateDate();
        if (!TextUtils.isEmpty(crime.getSuspect()))
            btnSuspect.setText(crime.getSuspect());
    }

    /**
     * 获取发送信息的模板文字
     *
     * @return
     */
    private String getCrimeReport() {
        String solvedString = null;
        if (crime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateString = DateFormat.format("yyyy-MM-dd", crime.getDate()).toString();

        String suspect = crime.getSuspect();
        if (TextUtils.isEmpty(suspect)) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, crime.getSuspect());
        }

        String report = getString(R.string.crime_report, crime.getTitle(), dateString, solvedString, suspect);
        return report;
    }
}
