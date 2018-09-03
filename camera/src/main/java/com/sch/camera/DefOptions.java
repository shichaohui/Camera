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

import com.sch.camera.annotation.Facing;
import com.sch.camera.annotation.Flash;

import java.io.Serializable;

/**
 * Created by StoneHui on 2018/8/20.
 * <p>
 * 基础配置。
 */
public class DefOptions implements Serializable {

    /**
     * 摄像头方向，后置。
     */
    public static final int FACING_BACK = 0;
    /**
     * 摄像头方向，前置。
     */
    public static final int FACING_FRONT = 1;

    /**
     * 闪光灯状态，关闭。
     */
    public static final int FLASH_OFF = 0;
    /**
     * 闪光灯状态，打开。
     */
    public static final int FLASH_ON = 1;
    /**
     * 闪光灯状态，自动。
     */
    public static final int FLASH_AUTO = 2;
    /**
     * 闪光灯状态，常量。
     */
    public static final int FLASH_TORCH = 3;

    private boolean isAutoFocus = true;
    @Facing
    private int facing = FACING_BACK;
    @Flash
    private int flash = FLASH_OFF;

    /**
     * 是否支持自动对焦。
     */
    public boolean isAutoFocus() {
        return isAutoFocus;
    }

    /**
     * 设置是否自动对焦, 默认 true。
     *
     * @param autoFocus 是否自动对焦。
     */
    public void setAutoFocus(boolean autoFocus) {
        isAutoFocus = autoFocus;
    }

    /**
     * 获取相机方向。
     */
    @Facing
    public int getFacing() {
        return facing;
    }

    /**
     * 设置相机方向, 默认 FACING_BACK。
     *
     * @param facing 相机方向。
     */
    public void setFacing(@Facing int facing) {
        this.facing = facing;
    }

    /**
     * 获取闪光灯状态。
     */
    @Flash
    public int getFlash() {
        return flash;
    }

    /**
     * 设置闪光灯状态，FLASH_ON。
     *
     * @param flash 闪光灯状态。
     */
    public void setFlash(@Flash int flash) {
        this.flash = flash;
    }

}
