package com.dunn.tools.recycleview.coocaarecycleview2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.NewRecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.coocaa.os.thememanager.R;
import com.coocaa.os.thememanager.data.Theme;
import com.coocaa.os.thememanager.utils.ThemeDebug;
import com.coocaa.os.thememanager.view.CoverImageView;
import com.coocaa.os.thememanager.view.PreviewLayout;
import com.skyworth.ui.newrecycleview.NewRecycleAdapterItem;
import com.skyworth.ui.newrecycleview.NewRecycleLayout;
import com.skyworth.ui.newrecycleview.OnScrollStateListener;
import com.skyworth.util.Util;

public class ThemePreView extends FrameLayout implements NewRecycleAdapterItem<Theme>, View.OnFocusChangeListener,
        View.OnClickListener, View.OnKeyListener, OnScrollStateListener {

    private final Context mContext;
    private final CoverImageView imageView;
    private final CoverImageView maskImageView;
    private final PreviewLayout mPreviewLayout;
    private final NewRecycleLayout mNewRecycleLayout;
    public static boolean isFirst = true;
    public static int indicator;

    public ThemePreView(@NonNull Context context, PreviewLayout previewLayout) {
        super(context);
        mContext = context;
        mPreviewLayout = previewLayout;
        mNewRecycleLayout = mPreviewLayout.getNewRecycleLayoutPreview();
        mNewRecycleLayout.setmScrollStateListener(this);

        LayoutParams params = new LayoutParams(Util.Div(1148), Util.Div(646));
        params.gravity = Gravity.LEFT;

        imageView = new CoverImageView(context, Util.Div(12));
        //imageView = (ImageView) ImageLoader.getLoader().getView(context);
        imageView.setFocusable(false);
        //imageView.setForeground(getResources().getDrawable(R.drawable.linear_gradient));
        addView(imageView, params);

        maskImageView = new CoverImageView(context, Util.Div(12));
        maskImageView.setVisibility(VISIBLE);
        //imageView = (ImageView) ImageLoader.getLoader().getView(context);
        maskImageView.setFocusable(false);
        maskImageView.setImageDrawable(getResources().getDrawable(R.drawable.linear_gradient));
        addView(maskImageView, params);

        //触摸适配
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.requestFocus();
                }
                return false;
            }
        });
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onUpdateData(Theme data, int position) {
        long start = System.currentTimeMillis();
        setOnClickListener(this);
        setOnFocusChangeListener(this);
        setOnKeyListener(this);
        RequestOptions requestOptions = new RequestOptions()//.skipMemoryCache(true)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .signature(new ObjectKey(data.getPkgName()))
                .override(Util.Div(1148), Util.Div(646))
                .format(DecodeFormat.PREFER_RGB_565);   //设置为这种格式去掉透明度通道，可以减少内存占有
                //.placeholder(R.drawable.img_error)
        Glide.with(getContext()).load(data.getPreviewPath()).apply(requestOptions).into(imageView);
        //imageView.setImageDrawable(data);
        refresh(position);
        ThemeDebug.e("position:" + position + " cost" + (System.currentTimeMillis() - start));
    }

    @Override
    public void clearItem() {

    }


    //刚创建item refreshUI可能不会被回调
    private void refresh(int pos) {
        ThemeDebug.d("pos:" + pos + " indicator:" + indicator +  " hasFocus:" + hasFocus() + " isFirst:" + isFirst);
        if (hasFocus()) {
            maskImageView.setVisibility(INVISIBLE);
        } else {
            if (pos < indicator) {
                maskImageView.setImageDrawable(getResources().getDrawable(R.drawable.linear_gradient_reverse));
                maskImageView.setVisibility(VISIBLE);
            } else if (pos > indicator){
                maskImageView.setImageDrawable(getResources().getDrawable(R.drawable.linear_gradient));
                maskImageView.setVisibility(VISIBLE);
            }
        }
        if (isFirst && pos == 0) {
            maskImageView.setVisibility(INVISIBLE);
        }
    }

    @Override
    public void refreshUI() {
        int pos = mNewRecycleLayout.getItemPosition(this);
        ThemeDebug.d("pos:" + pos + " indicator:" + indicator +  " hasFocus:" + hasFocus() + " isFirst:" + isFirst);
        if (hasFocus()) {
            maskImageView.setVisibility(INVISIBLE);
        } else {
            if (pos < indicator) {
                maskImageView.setImageDrawable(getResources().getDrawable(R.drawable.linear_gradient_reverse));
                maskImageView.setVisibility(VISIBLE);
            } else if (pos > indicator){
                maskImageView.setImageDrawable(getResources().getDrawable(R.drawable.linear_gradient));
                maskImageView.setVisibility(VISIBLE);
            }
        }
        if (isFirst && pos == 0) {
            maskImageView.setVisibility(INVISIBLE);

            ViewGroup.LayoutParams lp = getLayoutParams();
            //lp.width = Util.Div(1148+162);//ViewGroup.LayoutParams.WRAP_CONTENT
            //setLayoutParams(lp);
        }

    }

    @Override
    public void destroy() {
        ThemeDebug.e("des");
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        isFirst = false;
        int pos = mNewRecycleLayout.getItemPosition(v);
        mPreviewLayout.updateIndicator(pos);
        indicator = pos;
        ThemeDebug.d("view:" + v + " hasFocus:" + hasFocus + " pos:" + pos);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int pos = mNewRecycleLayout.getItemPosition(v);
        ThemeDebug.d("pos:" + pos + " keycode: " + keyCode);
        //不仅可以滑动到指定位置，还可以屏蔽蒙版切换的闪烁
        if (pos < 2 && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
            mNewRecycleLayout.smoothScrollToPosition(pos + 1);
        } else if (pos > 0 && keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
            mNewRecycleLayout.smoothScrollToPosition(pos -1);
        }
        return false;
    }

    @Override
    public void onScrollby(int selectPosition) {

    }

    @Override
    public void onScrollStart() {
        ThemeDebug.d("start");
    }

    @Override
    public void onScrollEnd(NewRecyclerView parent, int firstPostion, int endPosition) {

    }
}
