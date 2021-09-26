package com.dunn.tools.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunn.tools.log.LogUtil;
import com.dunn.tools.mydraw.PorterDuffXfermodeView;
import com.dunn.tools.time.Ability.AbilityFactory;
import com.dunn.tools.time.Ability.IAbility;
import com.dunn.tools.time.TimeTest;
import com.dunn.tools.time.TimeUtil;
import com.dunn.tools.time.bean.RemoteCommand;

import java.util.Calendar;
import java.util.Date;

import static android.widget.RelativeLayout.CENTER_IN_PARENT;

public class DrawActivity extends Activity implements View.OnClickListener {
    private TextView cmdTypeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(400,400);
        params.addRule(CENTER_IN_PARENT);
        layout.addView(new PorterDuffXfermodeView(this),params);
    }

    @Override
    public void onClick(View v) {

    }
}
