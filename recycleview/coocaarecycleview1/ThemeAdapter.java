package com.dunn.tools.recycleview.coocaarecycleview1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.List;

public class ThemeAdapter extends NewRecycleAdapter<Theme> implements OnItemFocusChangeListener,
        OnBoundaryListener {
    private Context mContext;
    private NewRecycleLayout mNewRecycleLayout;
    private ThemeSettingLayout themeSettingLayout;

    public ThemeAdapter(Context context, List<Theme> datas, ThemeSettingLayout view) {
        super(datas, 0);
        mContext = context;
        mNewRecycleLayout = view.getNewRecycleLayout();
        themeSettingLayout = view;
    }

    @Override
    public NewRecycleAdapterItem<Theme> onCreateItem(Object type) {
        return new ThemeItemView(mContext, themeSettingLayout);
    }

    //单个item大小
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup parent) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Util.Div(584+40), Util.Div(332+40));
        //params.rightMargin = Util.Div(40);
        return params;
    }

    @Override
    public boolean onLeftBoundary(View leaveView, int position) {
        ThemeDebug.e("v");
        return false;
    }

    @Override
    public boolean onTopBoundary(View leaveView, int position) {
        ThemeDebug.e("v");
        return false;
    }

    @Override
    public boolean onDownBoundary(View leaveView, int position) {
        ThemeDebug.e("v");
        return false;
    }

    @Override
    public boolean onRightBoundary(View leaveView, int position) {
        ThemeDebug.e("v");
        return false;
    }

    @Override
    public boolean onOtherKeyEvent(View v, int position, int keyCode) {
        ThemeDebug.e("v");
        return false;
    }

    @Override
    public void focusChange(View v, int position, boolean hasFocus) {
        ThemeDebug.e("v");
    }

    //这个可以控制每个item的大小，比如可以整屏显示一个item
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup parent) {
        return super.generateLayoutParams(parent);
    }
}
