package com.dunn.tools.sample;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.dunn.tools.navigationbar.DefaultNavigationBar;

public class NavigationBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigationbar);

        ViewGroup parent = (ViewGroup) findViewById(R.id.view_root);

        DefaultNavigationBar navigationBar =
                new DefaultNavigationBar.Builder(this,parent)
                        .setLeftText("返回")
                        //.hideLeftText()
                        .setLeftClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        })
                        // 还有一些参数
                        .create();

    }
}
