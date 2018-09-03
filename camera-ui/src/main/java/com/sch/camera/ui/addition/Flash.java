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

package com.sch.camera.ui.addition;

import android.support.annotation.DrawableRes;

import com.sch.camera.DefOptions;
import com.sch.camera.ui.R;

/**
 * Created by StoneHui on 2018/8/20.
 * <p>
 * 闪光灯。
 */
public enum Flash {

    /**
     * 关闭。
     */
    OFF(DefOptions.FLASH_OFF, R.mipmap.sch_ic_flash_off),
    /**
     * 打开。
     */
    ON(DefOptions.FLASH_ON, R.mipmap.sch_ic_flash_on),
    /**
     * 自动。
     */
    AUTO(DefOptions.FLASH_AUTO, R.mipmap.sch_ic_flash_auto),
    /**
     * 常亮。
     */
    TORCH(DefOptions.FLASH_TORCH, R.mipmap.sch_ic_flash_torch);

    @com.sch.camera.annotation.Flash
    private int value;

    @DrawableRes
    private int icon;

    Flash(@com.sch.camera.annotation.Flash int value, @DrawableRes int icon) {
        this.value = value;
        this.icon = icon;
    }

    /**
     * 获取对应的状态值。
     *
     * @return 对应的状态值。
     */
    @com.sch.camera.annotation.Flash
    public int getValue() {
        return value;
    }

    /**
     * 获取对应的图标资源id。
     *
     * @return 对应的图标资源id。
     */
    @DrawableRes
    public int getIcon() {
        return icon;
    }

    /**
     * 获取指定的闪光灯状态。
     *
     * @param value 状态值。
     * @return 状态枚举常量。
     */
    public static Flash get(@com.sch.camera.annotation.Flash int value) {
        if (value == OFF.value) {
            return OFF;
        } else if (value == ON.value) {
            return ON;
        } else if (value == AUTO.value) {
            return AUTO;
        } else if (value == TORCH.value) {
            return TORCH;
        }
        return OFF;
    }

}