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
    private NewRecycleLayout<Theme> newRecycleLayoutPreview;
    private final List<Theme> drawableList = new ArrayList<>();

    private void init(){
        newRecycleLayoutPreview = new NewRecycleLayout<>(mContext);
        newRecycleLayoutPreview.setFocusable(false);
        newRecycleLayoutPreview.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        newRecycleLayoutPreview.setItemSpace(new HorizontalItemDecoration(Util.Div(40), getContext()));
        //newRecycleLayoutPreview.setLayoutManager(new NewLayoutManager(mContext, 3));
        NewRecyclerView mRecyclerView = null;
        try {
            mRecyclerView = Reflector.with(newRecycleLayoutPreview).field("mRecyclerView").get();
        } catch (Reflector.ReflectedException e) {
            e.printStackTrace();
        }
        mRecyclerView.setLayoutManager(new ScrollSpeedLinearLayoutManger(mContext, LinearLayoutManager.HORIZONTAL, false));

        ThemePreViewAdapter themePreViewAdapter = new ThemePreViewAdapter(mContext, drawableList, this);
        newRecycleLayoutPreview.setRecyclerAdapter(themePreViewAdapter);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((v == button || v == recoveryButton) && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
            newRecycleLayoutPreview.smoothScrollToPosition(Math.min(ThemePreView.indicator + 1, 2));
        } else if ((v == button || v == recoveryButton) && keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
            newRecycleLayoutPreview.smoothScrollToPosition(Math.max(ThemePreView.indicator - 1, 0));
        }
        return false;
    }

    public void setDate(List<Theme> date) {
        this.drawableList.clear();
        this.drawableList.addAll(date);
        newRecycleLayoutPreview.notifyDataSetChanged();
    }
}
