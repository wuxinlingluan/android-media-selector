package com.ice.android_media_selector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ice.picker.activity.MediaListActivity;
import com.ice.picker.entity.MediaType;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView picPaths;
    private TextView videoPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button select_video_btn = (Button) findViewById(R.id.select_video);
        Button select_pic_btn = (Button) findViewById(R.id.select_pic);
        picPaths = (TextView) findViewById(R.id.pic_paths);
        videoPaths = (TextView) findViewById(R.id.video_paths);

        select_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MediaListActivity.class);
                i.putExtra("type", MediaType.VIDEO);
                i.putExtra("maxCount", 3);
                startActivityForResult(i, 99);
            }
        });
        select_pic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MediaListActivity.class);
                i.putExtra("type", MediaType.PIC);
                i.putExtra("maxCount", 3);
                startActivityForResult(i, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            List<String> selectedPaths = (List<String>) data.getSerializableExtra("selectedPaths");
            StringBuilder builder = new StringBuilder();
            for (String path : selectedPaths) {
                builder.append(path);
                builder.append("\n");
            }
            if (requestCode == 99) {
                videoPaths.setText(builder);
            } else {
                picPaths.setText(builder);
            }
        }
    }
}
