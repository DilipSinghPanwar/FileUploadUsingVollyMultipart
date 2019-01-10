package com.androiddevs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String POST_JOB = "http://votivelaravel.in/lms/webservicesJob/job_add";
    private TextView mTvFilePath;
    private Button mBtnPostFile;
    private Button mBtnAddFile;
    private static final int PICKFILE_RESULT_CODE = 1;
    private String selectedFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTvFilePath = findViewById(R.id.tvFilePath);
        mBtnAddFile = findViewById(R.id.btnAddFile);
        mBtnAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFile();
            }
        });
        mBtnPostFile = findViewById(R.id.btnPostFile);
        mBtnPostFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postFileWithMetaData();
            }
        });
    }

    public void OpenFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(Intent.createChooser(intent, "Open"), PICKFILE_RESULT_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK) {
            Uri picUri = data.getData();
            selectedFilePath = Utils.getPath(this, picUri);
            Log.e(TAG, "picUri: >>" + picUri);
            Log.e(TAG, "filePath: >>" + selectedFilePath);
            mTvFilePath.setText(selectedFilePath + "");
        }
    }

    public void postFileWithMetaData() {
        final VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, POST_JOB, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.e(TAG, "onResponse: >>" + resultResponse);
                Toast.makeText(MainActivity.this, resultResponse, Toast.LENGTH_SHORT).show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: >>" + error.toString());
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_title", "LMS JOB TITLE");
                params.put("post_description", "LMS DETAILS DESCRIPTIONS");
                params.put("salary", "10000");
                params.put("price_type", "fixed");
                params.put("recruiter_id", "1");
                params.put("job_category_id", "1");
                params.put("status", "1");
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                byte[] image = Utils.readBytesFromFile(selectedFilePath);
                String str = selectedFilePath.substring(selectedFilePath.lastIndexOf("/") + 1);
                params.put("file", new DataPart(str, image));
                return params;
            }
        };
        multipartRequest.setShouldCache(false);
        multipartRequest.setTag(TAG);
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VollyController.getInstance().addToRequestQueue(multipartRequest);
    }
}