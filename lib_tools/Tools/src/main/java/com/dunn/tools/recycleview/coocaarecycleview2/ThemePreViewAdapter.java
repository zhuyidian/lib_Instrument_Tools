package com.dunn.tools.recycleview.coocaarecycleview2;

import android.content.Context;
import android.support.v7.widget.NewRecycleAdapter;
import android.view.ViewGroup;

import com.coocaa.os.thememanager.data.Theme;
import com.coocaa.os.thememanager.view.PreviewLayout;
import com.coocaa.os.thememanager.view.item.ThemePreView;
import com.skyworth.ui.newrecycleview.NewRecycleAdapterItem;

import java.util.List;

public class ThemePreViewAdapter extends NewRecycleAdapter<Theme> {
    private final Context mContext;
    private PreviewLayout previewLayout;
    public ThemePreViewAdapter(Context context, List<Theme> datas, PreviewLayout view) {
        super(datas, 0);
        mContext = context;
        previewLayout =  view;
    }

    //单个item大小
//    @Override
//    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup parent) {
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Util.Div(1148+40), ViewGroup.LayoutParams.WRAP_CONTENT);
//        //params.rightMargin = Util.Div(40);
//        return params;
//    }

    @Override
    public NewRecycleAdapterItem<Theme> onCreateItem(Object type) {
        return new ThemePreView(mContext, previewLayout);
    }

    //这个可以控制每个item的大小，比如可以整屏显示一个item
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup parent) {
        return super.generateLayoutParams(parent);
    }
}
