package com.sdk.communacator

import android.app.Activity
import com.sdk.communacator.listener.PhotoResultListener
import com.sdk.communacator.listener.TextResultListener

interface SdkCommunicator {
    fun initCameraForTextRecognition(activity: Activity, sdkResultCallbackText: TextResultListener)
    fun initCameraForFaceDetection(activity: Activity, sdkResultCallbackPhoto: PhotoResultListener)
    fun registerSdk()
    fun unRegisterSdk()
}