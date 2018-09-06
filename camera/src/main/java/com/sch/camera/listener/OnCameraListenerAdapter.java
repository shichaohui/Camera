/*
 * Copyright (c) 2015-2018 Shi ChaoHui
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.sch.camera.listener;

/**
 * Created by StoneHui on 2018/9/6.
 * <p>
 * {@link OnCameraListener} 的空实现。
 */
public class OnCameraListenerAdapter implements OnCameraListener {

    @Override
    public void onFlashSupport(boolean isSupport) {
    }

    @Override
    public void onSensorChanged(int oldDegrees, int newDegrees) {
    }

    @Override
    public void onError(Exception e) {
    }

}
