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

package com.sch.camera;

import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by StoneHui on 2018/8/14.
 * <p>
 * 焦点范围处理类。
 */
@SuppressWarnings("SuspiciousNameCombination")
public class FocusRegionHelper {

    /**
     * 指定成像区域选择的半径。
     */
    private static final int HALF_AREA_SIZE = 150;

    /**
     * 获取 View 上指定位置对应的相机成像区域。
     *
     * @param x          View 上的 x 坐标。
     * @param y          View 上的 y 坐标。
     * @param viewWidth  View 的宽度。
     * @param viewHeight View 的高度。
     * @return 成像区域。
     */
    public static Rect get(float x, float y, int viewWidth, int viewHeight) {
        if (viewWidth < viewHeight) {
            int tmp = (int) x;
            x = y;
            y = viewWidth - tmp;
            tmp = viewWidth;
            viewWidth = viewHeight;
            viewHeight = tmp;
        }
        int centerX = (int) (x / viewWidth * 2000 - 1000);
        int centerY = (int) (y / viewHeight * 2000 - 1000);

        return new Rect(clamp(centerX - HALF_AREA_SIZE, -1000, 1000),
                clamp(centerY - HALF_AREA_SIZE, -1000, 1000),
                clamp(centerX + HALF_AREA_SIZE, -1000, 1000),
                clamp(centerY + HALF_AREA_SIZE, -1000, 1000));
    }

    /**
     * 获取 View 上指定位置对应的相机成像区域。
     *
     * @param x          View 上的 x 坐标。
     * @param y          View 上的 y 坐标。
     * @param viewWidth  View 的宽度。
     * @param viewHeight View 的高度。
     * @param region     CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE
     * @return 成像区域。
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static Rect get(float x, float y, int viewWidth, int viewHeight, Rect region) {
        if (viewWidth < viewHeight) {
            int tmp = (int) x;
            x = y;
            y = viewWidth - tmp;
            tmp = viewWidth;
            viewWidth = viewHeight;
            viewHeight = tmp;
        }
        int centerX = (int) (x / viewWidth * region.width());
        int centerY = (int) (y / viewHeight * region.height());
        return new Rect(clamp(centerX - HALF_AREA_SIZE, 0, region.width()),
                clamp(centerY - HALF_AREA_SIZE, 0, region.height()),
                clamp(centerX + HALF_AREA_SIZE, 0, region.width()),
                clamp(centerY + HALF_AREA_SIZE, 0, region.height()));
    }

    private static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

}
