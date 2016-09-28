package com.owant.linestateview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.owant.linestateview.view.LineStateView;

public class MainActivity extends AppCompatActivity {

    LineStateView lineStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineStateView = (LineStateView) findViewById(R.id.main_lsv_test);
        lineStateView.setLineStateClick(new LineStateView.LineStateClick() {
            @Override
            public void onPositionClick(View view, int position) {
                Toast.makeText(MainActivity.this, "pos=" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
