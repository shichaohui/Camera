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

import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by StoneHui on 2018/8/11.
 * <p>
 * 尺寸。
 */
public class Size {

    private int width;
    private int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * 尺寸转化。
     */
    public static List<Size> convert(List<Camera.Size> list) {
        List<Size> sizeList = new ArrayList<>();
        for (Camera.Size size : list) {
            sizeList.add(new Size(size.width, size.height));
        }
        return sizeList;
    }

    /**
     * 尺寸转化。
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<Size> convert(android.util.Size[] array) {
        List<Size> sizeList = new ArrayList<>();
        for (android.util.Size size : array) {
            sizeList.add(new Size(size.getWidth(), size.getHeight()));
        }
        return sizeList;
    }

}
