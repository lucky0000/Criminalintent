package com.bignerdranch.android.criminalintent;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bignerdranch.android.criminalintent.model.Crime;
import com.bignerdranch.android.criminalintent.model.CrimeLab;
import com.bignerdranch.android.criminalintent.util.PictureUtils;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

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
    //拍照
    private static final int REQUEST_PHOTO = 2;

    //权限
    private static final int ASK_READ_CONTACTS_PERMISSION = 2;

    private final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

    private Crime crime;
    private File mPhotoFile;

    private EditText edCrimeTitle;
    private Button btnCrimeDate;
    private ImageButton btnCrimeCamera;
    private ImageView ivCrimePhoto;
    private Button btnCrimeDelete;
    private Button btnReport;
    private Button btnSuspect;
    private Button btnCall;
    private CheckBox cbCrimeSolved;
    private String phonenum;
    private String phoneId;
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
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            try {
                if (c.getCount() == 0)
                    return;
                c.moveToFirst();
                String suspect = c.getString(0);
                phoneId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                crime.setSuspect(suspect);
                showCrime();

                //获取电话


                //SDK >= 23时，定义了Activity.checkSelfPermission方法
                //为了避免对SDK版本的判断，兼容低版本，一般都是使用兼容库中的方法
                //此处使用的是android.support.v4.app.Fragment中的checkSelfPermission
                //对于Activity，可以使用ActivityCompat中的方法
                int hasReadContactsPermission = checkSelfPermission(getActivity(),
                        android.Manifest.permission.READ_CONTACTS);

                //判断是否已有对应权限
                //用户主动赋予过一次后，该应用就一直具有该权限，除非在应用管理中撤销
                if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
                    //没有权限，则需要申请权限

                    //当用户选择“拒绝权限申请，并不再提示”后，仍可能点击该按键
                    //因此需要弹出提示框，提醒用户该功能需要权限
                    //这就要用到shouldShowRequestPermissionRationale方法
                    if (!shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS)) {
                        showMessageOKCancel("You need to allow access to Contacts",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //仍调用兼容库中的方法，申请权限
                                        requestPermissions(
                                                new String[]{Manifest.permission.READ_CONTACTS},
                                                ASK_READ_CONTACTS_PERMISSION);
                                    }
                                });
                        return;
                    }

                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                            ASK_READ_CONTACTS_PERMISSION);

                    return;
                }


                getPhoneNumber(phoneId);


            } finally {
                c.close();

            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.bignerdranch.android.criminalintent.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }

        //==

    }

    private void getPhoneNumber(String id) {
        Cursor c2 = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
        c2.moveToFirst();
        phonenum = c2.getString(c2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));   //通过Cursor c2获得联系人电话
        //crime.setPhonenum(phonenum);
        c2.close();
        showCrime();
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
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(crime);

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
        btnCall = (Button) v.findViewById(R.id.btn_call);
        cbCrimeSolved = (CheckBox) v.findViewById(R.id.cb_crime_solved);
        ivCrimePhoto = (ImageView) v.findViewById(R.id.crime_photo);
        btnCrimeCamera = (ImageButton) v.findViewById(R.id.crime_camera);

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


        btnSuspect.setOnClickListener(v1 -> {
            startActivityForResult(pickContact, REQUEST_CONTACT);
        });


//        if (TextUtils.isEmpty(phonenum))
//            btnCall.setEnabled(false);

        btnCall.setOnClickListener(v1 -> {
            Uri number = Uri.parse("tel:" + phonenum);
            Intent i = new Intent(Intent.ACTION_DIAL, number);               //创建新的隐式Intent，拨打电话
            startActivity(i);
        });

        PackageManager packageManager = getActivity().getPackageManager();
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        btnCrimeCamera.setEnabled(canTakePhoto);

        btnCrimeCamera.setOnClickListener(v1 -> {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.bignerdranch.android.criminalintent.fileprovider", mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            List<ResolveInfo> cameraActivityes = getActivity().getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo activity : cameraActivityes) {
                getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            startActivityForResult(captureImage, REQUEST_PHOTO);

        });

        updatePhotoView();

        return v;
    }

    private void showCrime() {
        cbCrimeSolved.setChecked(crime.isSolved());
        edCrimeTitle.setText(crime.getTitle());
        updateDate();
        if (!TextUtils.isEmpty(crime.getSuspect()))
            btnSuspect.setText(crime.getSuspect());

        if (!TextUtils.isEmpty(phonenum)) {
            btnCall.setText("CALL:" + phonenum);
            btnCall.setEnabled(true);
        } else {
            btnCall.setEnabled(false);
        }

        //如果不存在指定的activity 则禁用按钮
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null)
            btnSuspect.setEnabled(false);
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


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ASK_READ_CONTACTS_PERMISSION:
                //由于只申请了一个权限，因此grantResults[0]就是对应权限的申请结果
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //权限申请成功，则可以获取电话号码并拨号
                    getPhoneNumber(phoneId);
                } else {
                    Toast.makeText(getActivity(), "必须同意权限才可拨打电话", Toast.LENGTH_SHORT).show();
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            ivCrimePhoto.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            ivCrimePhoto.setImageBitmap(bitmap);
        }

    }
}
