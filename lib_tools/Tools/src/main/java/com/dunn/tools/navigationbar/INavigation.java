package com.dunn.tools.navigationbar;

import android.view.View;
import android.view.ViewGroup;

/**
 * 导航栏的规范
 * Created by dunn
 */
public interface INavigation {
    /**
     * 创建导航栏
     */
    void createNavigationBar();

    /**
     * 绑定参数
     */
    void attachNavigationParams();

    /**
     * 将 NavigationView添加到父布局
     */
    void attachParent(View navigationBar, ViewGroup parent);
}
