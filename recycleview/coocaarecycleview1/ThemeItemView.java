package com.dunn.tools.recycleview.coocaarecycleview1;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemProperties;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.coocaa.os.thememanager.PreviewActivity;
import com.coocaa.os.thememanager.R;
import com.coocaa.os.thememanager.constant.Constant;
import com.coocaa.os.thememanager.data.Theme;
import com.coocaa.os.thememanager.utils.ThemeDebug;
import com.coocaa.os.thememanager.view.CoverImageView;
import com.coocaa.os.thememanager.view.ThemeSettingLayout;
import com.skyworth.ui.api.widget.CCFocusDrawable;
import com.skyworth.ui.newrecycleview.NewRecycleAdapterItem;
import com.skyworth.ui.newrecycleview.NewRecycleLayout;
import com.skyworth.util.Util;

public class ThemeItemView extends FrameLayout implements NewRecycleAdapterItem<Theme>, View.OnFocusChangeListener,
        View.OnClickListener, View.OnKeyListener {

    private final CoverImageView imageView;
    private final TextView title;
    private final CCFocusDrawable ccFocusDrawable;
    private final GradientDrawable gradientDrawable;
    private final FrameLayout frameLayout;
    private final TipsView tipsView;
    private final ImageView selectedImageView;

    private final NewRecycleLayout mNewRecycleLayout;
    private final ThemeSettingLayout mThemeSettingLayout;

    private final Context mContext;

    public ThemeItemView(@NonNull Context context, ThemeSettingLayout themeSettingLayout) {
        super(context);
        mContext = context;
        mNewRecycleLayout = themeSettingLayout.getNewRecycleLayout();
        mThemeSettingLayout = themeSettingLayout;
        setFocusable(true);
        setClickable(true);

        frameLayout = new FrameLayout(context);
        LayoutParams params = new LayoutParams(Util.Div(560 + 6 + 6), Util.Div(314 + 6 + 6));
        params.gravity = Gravity.CENTER;
        addView(frameLayout, params);

        FrameLayout imageFrameLayout = new FrameLayout(context);
        params = new LayoutParams(Util.Div(560), Util.Div(314));
        params.gravity = Gravity.CENTER;
        frameLayout.addView(imageFrameLayout, params);

        imageFrameLayout.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // 设置按钮圆角率
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), Util.Div(18));
                // 设置按钮为圆形
                //outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        imageFrameLayout.setClipToOutline(true);

        //imageView = (ImageView) ImageLoader.getLoader().getView(context);
        imageView = new CoverImageView(context, Util.Div(0));
        imageView.setFocusable(false);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        imageFrameLayout.addView(imageView, params);

        title = new TextView(context);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setFocusable(false);
        title.setTextSize(Util.Dpi(36));
        title.setTextColor(getColorWithAlpha(R.color.text_color, 0xff));
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = Util.Div(20);
        params.topMargin = Util.Div(248);
        frameLayout.addView(title, params);

        ccFocusDrawable = new CCFocusDrawable(context, false)   //asCircle: true圆  false方
                .setSolidVisible(false)  //是否需要填充颜色
                .setSolidColor(Color.WHITE)   //填充颜色
                .setBorderVisible(true)   //是否需要边框
                .setBorderColor(Color.WHITE)   //边框颜色
                .setBorderWidth(Util.Div(3))   //边框宽度
                .setRadius(Util.Div(18))   //统一设置四个角
                //.setRadius(radius);    //定制四个角
                //.setGradient(GradientDrawable.Orientation.BOTTOM_TOP,colors)   //填充渐变色
                .setGapWidth(Util.Div(3));   //间隙
        gradientDrawable = new GradientDrawable();
        //gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(Color.TRANSPARENT);
        gradientDrawable.setStroke(Util.Div(3), Color.WHITE);
        gradientDrawable.setCornerRadius(Util.Div(20));
        //frameLayout.setBackground(gradientDrawable);
        //imageView.setBackground(getResources().getDrawable(R.drawable.drawable_shape));

        float[] radius = new float[]{0, 0, Util.Div(16), Util.Div(16), 0, 0, 0, 0};
        selectedImageView = new CoverImageView(context, 0);
        selectedImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_selected));
        selectedImageView.setVisibility(INVISIBLE);
        params = new LayoutParams(Util.Div(70), Util.Div(70));
        params.gravity = Gravity.END;
        //params.rightMargin = Util.Div(6);
        //params.topMargin = Util.Div(6);
        imageFrameLayout.addView(selectedImageView, params);

        tipsView = new TipsView(context);
        tipsView.setVisibility(GONE);
        //params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params = new LayoutParams(Util.Div(560), Util.Div(314));
        params.gravity = Gravity.CENTER;
        frameLayout.addView(tipsView, params);

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
        //触摸菜单键用长按表示
        setLongClickable(true);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                doActionByMenu(v);
                return true;
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
        ThemeDebug.e("data pkgName:" + data.getPkgName());
        setOnClickListener(this);
        setOnFocusChangeListener(this);
        setOnKeyListener(this);
        //imageView.setImageDrawable(getResources().getDrawable(R.drawable.theme_icon_test));
        //Bitmap bitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.theme_icon_test);
        //imageView.setImageDrawable(new RoundCornerDrawable(bitmap, (float) Util.Div(100)));
//        ImageLoader.getLoader().with(themeContext)
//                .load(getDrawableId(themeContext, "cover")).resize(100, 100).setLeftBottomCorner(Util.Div(18)).setLeftTopCorner(Util.Div(18))
//                .setRightBottomCorner(Util.Div(18)).setRightTopCorner(Util.Div(18))
//                        .setScaleType(ImageView.ScaleType.CENTER_CROP).into(imageView);
        RequestOptions requestOptions = new RequestOptions()
                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new ObjectKey(data.getPkgName()))
                .encodeQuality(70)
                .override(Util.Div(560), Util.Div(314))
                .format(DecodeFormat.PREFER_RGB_565);   //设置为这种格式去掉透明度通道，可以减少内存占有
        //.placeholder(R.drawable.img_error)
        if (!imageView.isLoad()) {
            Glide.with(getContext()).load(data.getCoverPath())
                    .apply(requestOptions)
                    //.apply(options)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            imageView.setImageDrawable(resource);
                            imageView.setLoad(true);
                        }
                    });
        }

        title.setText(data.getThemeName());
        setTag(data.getPkgName());
        if (SystemProperties.get(Constant.THEME_PACKAGE_NAME_PROPERTY, "noting").equals(data.getPkgName())) {
            selectedImageView.setVisibility(VISIBLE);
        } else {
            selectedImageView.setVisibility(INVISIBLE);
        }
        ThemeDebug.e("cost" + (System.currentTimeMillis() - start));
    }

    @Override
    public void clearItem() {

    }

    @Override
    public void refreshUI() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        ThemeDebug.e("v:" + v);
        if (v instanceof ThemeItemView) {
            title.invalidate();
            title.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            ThemeDebug.e("v:" + hasFocus + " isHardwareAccelerated: " + title.isHardwareAccelerated());
            if (hasFocus) {
                //((View) getParent()).setLayerType(View.LAYER_TYPE_SOFTWARE, null); //暂时，解决TextView模
                //this.setElevation(Util.Div(5));
                frameLayout.setBackground(gradientDrawable);
                ViewCompat.animate(this).setDuration(200).scaleX(1.02f).scaleY(1.02f).start();
//                AnimationSet animationSet = new AnimationSet(true);
//                ScaleAnimation animation = new ScaleAnimation(1.0f,1.1f,1.0f,1.1f,
//                        Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//                animation.setDuration(350);//动画效果时间
//                animation.setFillAfter(true);
//                animationSet.addAnimation(animation);
//                animationSet.setFillAfter(true);
//                v.clearAnimation();
//                v.startAnimation(animationSet);
            } else {
                frameLayout.setBackground(null);
                tipsView.setVisibility(GONE);
                //this.setElevation(Util.Div(0));
                ViewCompat.animate(this).setDuration(200).scaleX(1f).scaleY(1f).start();
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        ThemeDebug.e("v getTag:" + v.getTag());
        Intent intent = new Intent(getContext(), PreviewActivity.class);
        intent.putExtra(Constant.THEME_PACKAGE_INTENT_KEY, (String) v.getTag());
        getContext().startActivity(intent);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        ThemeDebug.e("keyCode:" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN && v instanceof ThemeItemView) {
            ThemeDebug.e("v: ThemeItemView");
            doActionByMenu(v);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
            int pos = mNewRecycleLayout.getItemPosition(v);
            ThemeDebug.e("pos:" + pos + "  row:" + mNewRecycleLayout.getRow(pos) + " col:" + mNewRecycleLayout.getCol(pos));
            if (pos != -1 && pos < 3) {
                ThemeDebug.e("top item" + getRootView());
                mThemeSettingLayout.requestFocus();
                return true;
            }
        }
        return false;
    }

    private void doActionByMenu(View v){
        if (v.hasFocus() && tipsView.getVisibility() != VISIBLE) {
            tipsView.setVisibility(VISIBLE);
            title.setTextColor(getColorWithAlpha(R.color.text_color, 0xE6));
        } else if (v.hasFocus() && tipsView.getVisibility() == VISIBLE) {
            tipsView.setVisibility(GONE);
            title.setTextColor(getColorWithAlpha(R.color.text_color, 0xff));
        }
    }

    //不透明度
    private ColorStateList getColorWithAlpha(int resId, int alpha) {
        return ColorStateList.valueOf(getResources().getColor(resId)).withAlpha(alpha);
    }

    static class TipsView extends LinearLayout {

        private final Context mContext;

        public TipsView(Context context) {
            super(context);
            mContext = context;
            initView();
        }

        private void initView() {
            CCFocusDrawable focusDrawable = new CCFocusDrawable(mContext, false)   //asCircle: true圆  false方
                    .setSolidVisible(true)  //是否需要填充颜色
                    .setSolidColor(Color.parseColor("#66000000"))   //填充颜色
                    .setBorderVisible(false)   //是否需要边框
                    .setBorderColor(Color.WHITE)   //边框颜色
                    .setBorderWidth(Util.Div(3))   //边框宽度
                    .setRadius(Util.Div(18))   //统一设置四个角
                    //.setRadius(radius);    //定制四个角
                    //.setGradient(GradientDrawable.Orientation.BOTTOM_TOP,colors)   //填充渐变色
                    .setGapWidth(Util.Div(3));   //间隙
            //setBackgroundColor(Color.parseColor("#66000000"));
            GradientDrawable gradientDrawable = new GradientDrawable();
            //gradientDrawable.setShape(GradientDrawable.RECTANGLE);
            gradientDrawable.setColor(Color.parseColor("#66000000"));
            //gradientDrawable.setStroke(Util.Div(3),Color.WHITE);
            gradientDrawable.setCornerRadius(Util.Div(16));
            setBackground(gradientDrawable);

            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);

            ImageView deleteImageView = new ImageView(mContext);
            deleteImageView.setImageResource(R.drawable.ic_delete);
            LayoutParams params = new LayoutParams(Util.Div(78), Util.Div(78));
            addView(deleteImageView, params);

            LinearLayout linearLayout = new LinearLayout(mContext);
            params = new LayoutParams(Util.Div(104 + 28 + 56), ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = Util.Div(29);
            addView(linearLayout, params);

            TextView textView = new TextView(mContext);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(Util.Dpi(28));
            textView.setTextColor(mContext.getResources().getColor(R.color.text_color));
            textView.setText("按");
            params = new LayoutParams(Util.Div(28), Util.Div(40));
            linearLayout.addView(textView, params);


            CCFocusDrawable ccFocusDrawable = new CCFocusDrawable(mContext, false)   //asCircle: true圆  false方
                    .setSolidVisible(false)  //是否需要填充颜色
                    .setSolidColor(Color.WHITE)   //填充颜色
                    .setBorderVisible(true)   //是否需要边框
                    .setBorderColor(Color.WHITE)   //边框颜色
                    .setBorderWidth(Util.Div(1))   //边框宽度
                    .setRadius(Util.Div(8))   //统一设置四个角
                    //.setGradient(GradientDrawable.Orientation.BOTTOM_TOP,colors)   //填充渐变色
                    .setGapWidth(Util.Div(3));   //间隙
            TextView textView2 = new TextView(mContext);
            //textView2.setPadding(0,0,0,0);
            textView2.setTypeface(Typeface.DEFAULT_BOLD);
            textView2.setBackground(ccFocusDrawable);
            textView2.setGravity(Gravity.CENTER);
            textView2.setTextSize(Util.Dpi(24));
            textView2.setTextColor(mContext.getResources().getColor(R.color.text_color));
            textView2.setText("确定键");
            params = new LayoutParams(Util.Div(88), ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = Util.Div(8);
            linearLayout.addView(textView2, params);

            TextView textView3 = new TextView(mContext);
            textView3.setGravity(Gravity.CENTER);
            textView3.setTextSize(Util.Dpi(28));
            textView3.setTextColor(mContext.getResources().getColor(R.color.text_color));
            textView3.setText("删除");
            params = new LayoutParams(Util.Div(56), Util.Div(40));
            params.leftMargin = Util.Div(8);
            linearLayout.addView(textView3, params);
        }
    }
}
