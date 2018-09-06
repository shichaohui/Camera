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

package com.sch.camera.listener;

import com.sch.camera.annotation.SensorDegrees;

/**
 * Created by StoneHui on 2018/8/8.
 * <p>
 * 相机监听。
 */
public interface OnCameraListener {

    /**
     * 是否支持闪光灯的回调。
     *
     * @param isSupport 是否支持闪光灯。
     */
    void onFlashSupport(boolean isSupport);

    /**
     * 传感器角度改变。
     *
     * @param oldDegrees 改变前的角度。
     * @param newDegrees 改变后的角度。
     */
    void onSensorChanged(@SensorDegrees int oldDegrees, @SensorDegrees int newDegrees);

    /**
     * 相机出错。
     *
     * @param e Exception, e.getMessage() 可获得错误提示。
     */
    void onError(Exception e);

}
