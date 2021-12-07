package com.dunn.tools.recycleview.coocaarecycleview1;

import android.view.KeyEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: NewRecycleViewTest
 * @Author: ZhuYiDian
 * @CreateDate: 2021/11/30 14:51
 * @Description: coocaa使用举例
 */
public class NewRecycleView {
    private NewRecycleLayout<Theme> newRecycleLayout;
    private final List<Theme> list = new ArrayList<>();

    private void init(){
        newRecycleLayout = new NewRecycleLayout<>(mContext);
        newRecycleLayout.setVisibility(GONE);
        newRecycleLayout.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        newRecycleLayout.setLayoutManager(new NewLayoutManager(mContext, 3));
        //newRecycleLayout.setItemSpace(Util.Div(40),Util.Div(40),true);
        //可以不要这一句，因为item四周有留白
        //newRecycleLayout.setItemSpace(new GridSpacingItemDecoration(3,Util.Div(20),false));

        ThemeAdapter themeAdapter = new ThemeAdapter(mContext, list, this);
        newRecycleLayout.setRecyclerAdapter(themeAdapter);
        themeAdapter.setOnBoundaryListener(themeAdapter);
        themeAdapter.setOnItemFocusChangeListener(themeAdapter);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                int pos = newRecycleLayout.getFirstVisiblePosition();
                //int pos = newRecycleLayout.getItemPosition(v);
                Log.e("pos:" + pos + "  row:" + newRecycleLayout.getRow(pos) + " col:" + newRecycleLayout.getCol(pos));
                if (pos == -1) {
                    return false;
                }
                newRecycleLayout.getItemByPosition(pos).requestFocus();
                return true;
            }
        }
        return false;
    }

    @Override
    public void setDate(List<Theme> date) {
        list.clear();
        list.addAll(date);
        newRecycleLayout.notifyDataSetChanged();
    }
}
