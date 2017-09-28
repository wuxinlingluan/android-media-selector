package com.ice.picker.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ice.picker.R;
import com.ice.picker.fragment.ImagePreviewFragment;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class PreviewActivity extends AppCompatActivity {
    private TextView mTvTitle;
    private Button mBtnNext;
    private ImagePreviewFragment fragment;
    private List<String> paths;
    private List<String> selectedPaths;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_activity);

        Intent getIntent = getIntent();
        Bundle bundle = getIntent.getBundleExtra("params");
        paths = ((List<String>) bundle.getSerializable("paths"));
        selectedPaths = ((List<String>) bundle.getSerializable("selectedPaths"));
        initView(bundle);
    }

    private void initView(Bundle bundle) {
        mTvTitle = (TextView) findViewById(R.id.tv_top_bar_title);
        mBtnNext = (Button) findViewById(R.id.next_btn);
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPaths.size() > 0) {
                    goBack();
                }
            }
        });
        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        mTvTitle.setText(String.format(Locale.CHINA, "%d/%d", paths.indexOf(bundle.getString("currentPath")) + 1, paths.size()));

        fragment = ImagePreviewFragment.newInstance(bundle);
        fragment.setImageSelectedListener(new ImagePreviewFragment.onImageSelectChangedListener() {
            @Override
            public void onImageSelectChanged(List<String> paths) {
                selectedPaths = paths;
                updateNextButton();
            }

            @Override
            public void onPageChanged(int index, int total) {
                mTvTitle.setText(String.format(Locale.CHINA, "%d/%d", index + 1, paths.size()));
            }
        });
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.add(R.id.img_preview, fragment);
        trans.commit();
        updateNextButton();
    }

    public void updateNextButton() {
        if (selectedPaths.size() > 0) {
            mBtnNext.setSelected(true);
            mBtnNext.setText("下一步(" + selectedPaths.size() + ")");
            mBtnNext.setTextColor(Color.WHITE);
        } else {
            mBtnNext.setSelected(false);
            mBtnNext.setText("下一步");
            mBtnNext.setTextColor(getResources().getColor(R.color.textGreyColor));
        }
    }

    public void goBack() {
        Intent intent = new Intent();
        intent.putExtra("selectedPaths", (Serializable) selectedPaths);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
