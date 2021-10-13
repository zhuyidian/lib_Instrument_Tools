package com.dunn.tools.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunn.tools.common.CommonUtil;
import com.dunn.tools.log.LogUtil;
import com.dunn.tools.mydraw.PorterDuffXfermodeView;
import com.dunn.tools.screen.ScreenUtils;

import static android.widget.RelativeLayout.CENTER_IN_PARENT;

public class ScreenActivity extends Activity implements View.OnClickListener {
    private StringBuilder screenResultBuilder = new StringBuilder();
    private TextView screenText;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        mContext = ScreenActivity.this;
        screenText = findViewById(R.id.screen);

        screenResultBuilder.setLength(0);
        screenText.setText("");

        DisplayMetrics dMetrics = ScreenUtils.getDisplayMetrics(mContext);
        //  屏幕的绝对宽度（像素）
        //int screenWidth = dMetrics.widthPixels;  //不包含虚拟导航栏的高度
        int screenWidth = ScreenUtils.getScreenWidth3(mContext); //包含虚拟导航栏的高度
        // 屏幕的绝对高度（像素）
        //int screenHeight = dMetrics.heightPixels;  //不包含虚拟导航栏的高度
        int screenHeight = ScreenUtils.getScreenHeight3(mContext); //包含虚拟导航栏的高度
        screenResultBuilder.append("\r\n"+"像素:"+screenWidth+","+screenHeight);

        // X轴方向上屏幕每英寸的物理像素数。
        float xdpi = dMetrics.xdpi;
        // Y轴方向上屏幕每英寸的物理像素数。
        float ydpi = dMetrics.ydpi;
        screenResultBuilder.append("\r\n"+"XY每英寸的像素数(密度PPI):"+xdpi+","+ydpi);

        //获取屏幕尺寸
        double screenInches = ScreenUtils.getScreenInches(mContext);
        screenResultBuilder.append("\r\n"+"屏幕尺寸Inches:"+screenInches);

        // 屏幕的逻辑密度，是密度无关像素（dip）的缩放因子，160dpi是系统屏幕显示的基线，1dip = 1px， 所以，在160dpi的屏幕上，density = 1， 而在一个120dpi屏幕上 density = 0.75。
        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        float density = dMetrics.density;
        // 每英寸的像素点数，屏幕密度的另一种表示。densityDpi = density * 160.
        // 屏幕密度（每寸像素：120/160/240/320）
        float desityDpi = dMetrics.densityDpi;
        screenResultBuilder.append("\r\n"+"屏幕密度(像素比例):"+density+",   屏幕密度dpi(每英寸的点数):"+desityDpi);

        String dpi = ScreenUtils.getResourcesDpiMsg(mContext);
        screenResultBuilder.append("\r\n"+"res: "+dpi);

        //  屏幕上字体显示的缩放因子，一般与density值相同，除非在程序运行中，用户根据喜好调整了显示字体的大小时，会有微小的增加。
        float scaledDensity = dMetrics.scaledDensity;
        screenResultBuilder.append("\r\n"+"字体显示的缩放因子:"+scaledDensity);

        //px-->dp
        int widthDp = ScreenUtils.pxConvertDip(mContext,screenWidth);
        int heightDp = ScreenUtils.pxConvertDip(mContext,screenHeight);
        screenResultBuilder.append("\r\n"+"dp转换:"+widthDp+","+heightDp);

        //StatusBar
        int statusBar = ScreenUtils.getStatusBarHeight(mContext);
        int navigationBar = ScreenUtils.getNavigationBarHeight(mContext);
        screenResultBuilder.append("\r\n"+"statusBar:"+statusBar+",   navigationBar"+navigationBar);
        screenText.setText(screenResultBuilder.toString());

        LogUtil.v("screen","result="+screenResultBuilder.toString());
    }

    @Override
    public void onClick(View v) {

    }
}
