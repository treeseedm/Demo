package com.csja.smlocked;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.csja.smlocked.View.CustomListView;
import com.csja.smlocked.daemon.MyApplication1;
import com.csja.smlocked.log.MLog;
import com.csja.smlocked.volley.JsonObjectReqeustWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class StudentModifyActivity extends Activity {
    private static String TAG = "StudentModifyActivity";
    private EditText mPhone, name, contact, et_phonename;
    private EditText grade;
    private Button mBtnAdd;
    private CustomListView mListView;
    private List<String> listContact = new ArrayList<>();
    private ArrayAdapter adapter;
    //    private String className;
//    private String classId = "10";
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_modify);
        mPhone = (EditText) findViewById(R.id.et_phone);
        name = (EditText) findViewById(R.id.name);
        grade = (EditText) findViewById(R.id.grade);
        et_phonename = (EditText) findViewById(R.id.et_phonename);


        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//        mPhone.setText(tm.getLine1Number());
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText("学生注册");

        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(StudentModifyActivity.this, TestScanActivity.class), 1);
            }
        });
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    save();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        String info = Constant.getStudentInfo(getApplicationContext());
        if (!TextUtils.isEmpty(info)) {
            try {
                JSONObject jsonObject = new JSONObject(info);
                mPhone.setText(jsonObject.optString("tel"));
                name.setText(jsonObject.optString("fullname"));
//                className = jsonObject.optString("className");
//                classId = jsonObject.optString("classesId");

                grade.setText(jsonObject.optString("classesId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mBtnAdd = (Button) findViewById(R.id.btn_add);
        mListView = (CustomListView) findViewById(R.id.list_contact);
        contact = (EditText) findViewById(R.id.et_contact);


        adapter = new ArrayAdapter(this, android.R.layout.test_list_item, listContact);
        TextView textView = new TextView(this);
        textView.setText("已添加联系人");
        mListView.addHeaderView(textView);
        mListView.setAdapter(adapter);


        String hasContact = Constant.getContact(this);
        if (!TextUtils.isEmpty(hasContact)) {
            String[] tempCon = hasContact.split(";");
            for (String c : tempCon) {
                listContact.add(c);
            }
            adapter.notifyDataSetChanged();
        }

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = contact.getText().toString();
                String etName = et_phonename.getText().toString();
                contact.setText("");
                et_phonename.setText("");
                if (!TextUtils.isEmpty(temp) && !TextUtils.isEmpty(etName)) {
                    listContact.add(etName + "|" + temp);
                    Constant.saveContact(StudentModifyActivity.this, etName + "|" + temp);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(StudentModifyActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        askForPermission();


    }

    private void save() throws JSONException {

        final String tel = mPhone.getText().toString();
        final String fullName = name.getText().toString();

//        if (TextUtils.isEmpty(et_phonename.getText())) {
//            Toast.makeText(this, "请输入联系人姓名", Toast.LENGTH_SHORT).show();
//            return;
//        }
        final String tempClassId = grade.getText().toString();

        final String path = "user/regist?fullname=" + URLEncoder.encode(fullName) + "&tel=" + URLEncoder.encode(tel) + "&code=" + URLEncoder.encode(tempClassId) + "&channelId=" + URLEncoder.encode(Constant.getChannelId(this));
        MLog.i(TAG, "path=" + path);

        JsonObjectReqeustWrapper jsonObjectRequest = new JsonObjectReqeustWrapper(JsonObjectRequest.Method.GET, path, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            MLog.i(TAG, response.toString());
                            Toast.makeText(StudentModifyActivity.this, response.optString("text"), Toast.LENGTH_SHORT).show();
                            if (Constant.SUCCESS.equals(response.optString("code"))) {
                                Intent intent = new Intent(StudentModifyActivity.this, StudentInfoActivity.class);
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("fullname", fullName);
                                    jsonObject.put("tel", tel);
                                    jsonObject.put("classesId", tempClassId);
                                    jsonObject.put("className", response.optString("className"));
                                    jsonObject.put("channelId", Constant.getChannelId(StudentModifyActivity.this));
                                    jsonObject.putOpt("studentId", response.optString("studentId"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent);
                                Constant.saveStudentInfo(getApplicationContext(), jsonObject.toString());
                                finish();
                            } else {
                                MLog.i(TAG, getString(R.string.server_error));
                                Toast.makeText(StudentModifyActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                            }

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentModifyActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                MLog.i(TAG, "onErrorResponse" + error.getMessage() + error.getLocalizedMessage() + error.getNetworkTimeMs());

            }
        });
        MyApplication1.mRequestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            String classes = Uri.decode(data.getStringExtra("classes"));

//            className = classes.split("#")[1];
//            classId = classes.split("#")[0];
            grade.setText("" + classes.split("#")[0]);
        }
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {

                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "权限授予成功！", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    /**
     * 请求用户给予悬浮窗的权限
     */
    public void askForPermission() {
        boolean isPermission = getAppOps(this);
        MLog.i(TAG, "isPermission ->" + isPermission);
        if (!getAppOps(this) && Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("系统需要打开悬浮框权限");
            builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Uri packageURI = Uri.parse("package:" + getPackageName());
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        startActivity(intent);
                    } catch (Exception e) {
                        MLog.i(TAG, "askForPermission>" + e.getMessage() + "==" + e);
                    }

                }
            });
            builder.create().show();
        }

    }

    protected boolean isPermissionGranted(String permissionName, int questCode) {
        try {
            MLog.i(TAG, "hasPermision=>" + permissionName);
            //6.0以下系统，取消请求权限
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true;
            }
            //判断是否需要请求允许权限，否则请求用户授权
            int hasPermision = checkSelfPermission(permissionName);
            MLog.i(TAG, "hasPermision=>" + hasPermision);
            if (hasPermision != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permissionName}, questCode);
                return false;
            }
        } catch (Exception e) {
            MLog.i(TAG, "isPermissionGranted->" + e.getMessage() + "==" + e);
        }

        return true;
    }

    public static boolean getAppOps(Context context) {
        try {
            Object object = context.getSystemService("appops");
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(24);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {

        }
        return false;
    }
}
