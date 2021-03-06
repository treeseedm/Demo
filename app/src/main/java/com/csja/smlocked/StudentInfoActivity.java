package com.csja.smlocked;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.csja.smlocked.daemon.MyApplication1;
import com.csja.smlocked.entity.TimeConfigResponse;
import com.csja.smlocked.log.MLog;
import com.csja.smlocked.volley.JsonObjectReqeustWrapper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

public class StudentInfoActivity extends Activity {
    private static String TAG = "StudentInfoActivity";
    private String studentId;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);
        String info = Constant.getStudentInfo(getApplicationContext());
        NotifyMessageController.updateVersion(this);

        if (TextUtils.isEmpty(info)) {
            startActivity(new Intent(this, StudentModifyActivity.class));
            finish();
            return;
        }
        if ("main".equals(getIntent().getAction())) {
            LockedWindow.show(getApplicationContext(), false);
            finish();
            return;
        }
        JSONObject jsonObject = null;
        try {
            TextView grade = (TextView) findViewById(R.id.grade);
            TextView name = (TextView) findViewById(R.id.name);
            TextView phone = (TextView) findViewById(R.id.sp_phone);
            jsonObject = new JSONObject(info);
            phone.setText(jsonObject.optString("tel"));
            name.setText(jsonObject.optString("fullname"));
            grade.setText(jsonObject.optString("className"));
            studentId = jsonObject.optString("studentId");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView title = (TextView) findViewById(R.id.title);
        title.setText("信息确认");
        findViewById(R.id.btn_modify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentInfoActivity.this, StudentModifyActivity.class));
                finish();
            }
        });
        findViewById(R.id.btn_OK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    query(StudentInfoActivity.this, studentId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static void query(final Context context, String studentId) throws JSONException {

        String path = "setting/next7days?studentId=" + URLEncoder.encode("" + studentId);
        JsonObjectReqeustWrapper jsonObjectRequest = new JsonObjectReqeustWrapper(JsonObjectRequest.Method.GET, path, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            Gson gson = new Gson();
                            TimeConfigResponse timeConfigResponse = gson.fromJson(response.toString(), TimeConfigResponse.class);
                            MLog.i(TAG, response.toString());
                            if (Constant.SUCCESS.equals(timeConfigResponse.code)) {
                                Toast.makeText(context, "更新成功", Toast.LENGTH_SHORT).show();
                                ConfigUtil.clearTime(context);
                                List<ConfigEntity> configEntityList = timeConfigResponse.lockTime;
                                for (ConfigEntity configEntity : configEntityList) {
                                    ConfigUtil.addTime(context, configEntity.startTime, configEntity.endTime);
                                }
                                if (timeConfigResponse.motto != null) {
                                    Constant.saveMotto(context, timeConfigResponse.motto.creator, timeConfigResponse.motto.content);
                                }
                                if (context instanceof StudentInfoActivity) {
                                    ((StudentInfoActivity) context).finish();
                                }
                                LockedWindow.initView(context);
                            } else {
                                Toast.makeText(context, "更新失败", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(context, "更新失败", Toast.LENGTH_SHORT).show();
                            MLog.i(TAG, "获取数据失败");
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "更新失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, error.getMessage(), error);
                //Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
        MyApplication1.mRequestQueue.add(jsonObjectRequest);
    }


    /**
     * 用户返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "权限授予成功！", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onBackPressed() {
        LockedWindow.hide();
        super.onBackPressed();
    }
}
