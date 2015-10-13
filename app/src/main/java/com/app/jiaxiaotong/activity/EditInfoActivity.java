package com.app.jiaxiaotong.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.utils.ToolBarUtils;

/**
 * 编辑信息activity
 */
public class EditInfoActivity extends BaseActivity {

    public static final String INFO = "info";

    private EditText infoEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        ToolBarUtils.initToolBar(this,"编辑信息");
        infoEt = (EditText) findViewById(R.id.edit_info_et);

        final TextView saveTv = (TextView) findViewById(R.id.title_right_sure_tv);
        saveTv.setVisibility(View.VISIBLE);
        saveTv.setEnabled(false);
        infoEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    saveTv.setEnabled(true);
                }
            }
        });
        saveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(INFO,infoEt.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }


}
