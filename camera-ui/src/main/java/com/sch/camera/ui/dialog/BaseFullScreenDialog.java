/*
 * Copyright (c) 2015-2018 Shi ChaoHui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sch.camera.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

import com.sch.camera.ui.R;

/**
 * Created by StoneHui on 2018/8/24.
 * <p>
 * 全屏对话框。
 */
public abstract class BaseFullScreenDialog extends Dialog {

    public BaseFullScreenDialog(@NonNull Context context) {
        this(context, true);
    }

    public BaseFullScreenDialog(@NonNull Context context, boolean isTranslucentNavigation) {
        super(context, R.style.Camera_FullScreen);
        setContentView(getContentViewResId());
        initWindow(isTranslucentNavigation);
    }

    private void initWindow(boolean isTranslucentNavigation) {
        Window window = getWindow();
        if (window != null) {
            if (isTranslucentNavigation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setWindowAnimations(0);

            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.dimAmount = 0;
            window.setAttributes(params);
        }

        setCanceledOnTouchOutside(false);
    }

    /**
     * 获取内容视图的资源id。
     *
     * @return 内容视图的资源id。
     */
    @LayoutRes
    abstract int getContentViewResId();

}
