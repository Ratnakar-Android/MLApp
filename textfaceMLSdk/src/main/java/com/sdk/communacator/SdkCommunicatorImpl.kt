package com.sdk.communacator

import android.app.Activity
import android.content.Intent
import com.sdk.SdkMainActivity
import com.sdk.camera.utils.Constants.FACE_FRAGMENT
import com.sdk.camera.utils.Constants.TARGET_FRAGMENT
import com.sdk.camera.utils.Constants.TEXT_FRAGMENT
import com.sdk.communacator.EventBus.EventBusManager
import com.sdk.communacator.listener.PhotoResultListener
import com.sdk.communacator.listener.TextResultListener


class SdkCommunicatorImpl private constructor() : SdkCommunicator {

    companion object {
        val instance: SdkCommunicator by lazy { HOLDER.INSTANCE }
    }

    private object HOLDER {
        val INSTANCE: SdkCommunicator = SdkCommunicatorImpl()
    }

    override fun initCameraForTextRecognition(
        activity: Activity,
        sdkResultCallbackText: TextResultListener
    ) {
        EventBusManager.initTextBus(sdkResultCallbackText)
        val intent = Intent(activity, SdkMainActivity::class.java)
        intent.putExtra(TARGET_FRAGMENT, TEXT_FRAGMENT)
        activity.startActivity(intent)
    }

    override fun initCameraForFaceDetection(
        activity: Activity,
        sdkResultCallbackPhoto: PhotoResultListener
    ) {
        EventBusManager.initPhotoBus(sdkResultCallbackPhoto)
        val intent = Intent(activity, SdkMainActivity::class.java)
        intent.putExtra(TARGET_FRAGMENT, FACE_FRAGMENT)
        activity.startActivity(intent)

    }

    override fun registerSdk() {
        EventBusManager.initBus()
    }

    override fun unRegisterSdk() {
        EventBusManager.unRegisterEventBus()
    }
}