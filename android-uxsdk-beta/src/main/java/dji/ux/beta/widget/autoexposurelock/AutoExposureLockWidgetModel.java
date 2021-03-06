/*
 * Copyright (c) 2018-2019 DJI
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dji.ux.beta.widget.autoexposurelock;

import androidx.annotation.NonNull;

import dji.keysdk.CameraKey;
import dji.keysdk.DJIKey;
import dji.thirdparty.io.reactivex.Completable;
import dji.thirdparty.io.reactivex.Flowable;
import dji.ux.beta.base.DJISDKModel;
import dji.ux.beta.base.SchedulerProviderInterface;
import dji.ux.beta.base.WidgetModel;
import dji.ux.beta.base.uxsdkkeys.ObservableInMemoryKeyedStore;
import dji.ux.beta.util.DataProcessor;
import dji.ux.beta.util.SettingDefinitions.CameraIndex;

/**
 * Auto Exposure Lock Widget Model
 *
 * Widget Model for the {@link AutoExposureLockWidget} used to define the
 * underlying logic and communication
 */
public class AutoExposureLockWidgetModel extends WidgetModel {
    //region Fields
    private final DataProcessor<Boolean> autoExposureLockBooleanProcessor;
    private DJIKey autoExposureLockKey;
    private int cameraIndex = CameraIndex.CAMERA_INDEX_0.getIndex();
    private SchedulerProviderInterface schedulerProvider;
    //endregion

    public AutoExposureLockWidgetModel(@NonNull DJISDKModel djiSdkModel,
                                       @NonNull ObservableInMemoryKeyedStore uxKeyManager,
                                       @NonNull SchedulerProviderInterface schedulerProvider) {
        super(djiSdkModel, uxKeyManager);
        autoExposureLockBooleanProcessor = DataProcessor.create(false);
        this.schedulerProvider = schedulerProvider;
    }

    //region Data

    /**
     * Check if the auto exposure lock is enabled
     *
     * @return Flowable with boolean true - enabled  false - disabled
     */
    public Flowable<Boolean> isAutoExposureLockOn() {
        return autoExposureLockBooleanProcessor.toFlowable();
    }

    //endregion

    //region Actions

    /**
     * Set camera index to which the model should react.
     *
     * @param cameraIndex index of the camera.
     */
    public void setCameraIndex(@NonNull CameraIndex cameraIndex) {
        this.cameraIndex = cameraIndex.getIndex();
        restart();
    }

    /**
     * Get the camera index for which the model is reacting.
     *
     * @return int representing {@link CameraIndex}.
     */
    @NonNull
    public CameraIndex getCameraIndex() {
        return CameraIndex.find(cameraIndex);
    }

    /**
     * Set auto exposure lock enabled/disabled
     *
     * @param isLocked boolean true - enabled  false - disabled
     * @return Completable representing success and failure of action
     */
    public Completable setAutoExposureLock(boolean isLocked) {
        return djiSdkModel.setValue(autoExposureLockKey, isLocked)
                .subscribeOn(schedulerProvider.io());
    }
    //endregion

    //region lifecycle
    @Override
    protected void inSetup() {
        autoExposureLockKey = CameraKey.create(CameraKey.AE_LOCK, cameraIndex);
        bindDataProcessor(autoExposureLockKey, autoExposureLockBooleanProcessor);
    }

    @Override
    protected void inCleanup() {
        // nothing to clean
    }

    @Override
    protected void updateStates() {
        // No States
    }

    //endregion
}
