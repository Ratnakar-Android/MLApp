package com.sdk.communacator.EventBus

import com.sdk.communacator.listener.PhotoResultListener
import com.sdk.communacator.listener.TextResultListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

object EventBusManager {

    var sdkResultCallbackPhoto: PhotoResultListener? = null
    var sdkResultCallbackText: TextResultListener? = null

    fun initBus() {
        registerEventBus()
    }

    fun initPhotoBus(callbackPhoto: PhotoResultListener) {
        sdkResultCallbackPhoto = callbackPhoto
    }

    fun initTextBus(callbackText: TextResultListener) {
        sdkResultCallbackText = callbackText
    }

    /*
    * Registering the EventBus for getting updated result from PhotoFragment.
    */
    private fun registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    /*
     * Un-Registering the EventBus.
    */
     fun unRegisterEventBus() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun doThis(textCommunicator: EventBusText) {
        textCommunicator.fullTextOfPhoto?.let { sdkResultCallbackText?.textContentResult(it) }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun doThis(exceptionCommunicator: EventBusPhotoException) {
        sdkResultCallbackPhoto?.photoContentException(exceptionCommunicator.exceptionMessage)
        }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun doThis(exceptionCommunicator: EventBusTextException) {
            sdkResultCallbackText?.textContentException(exceptionCommunicator.exceptionMessage)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun doThis(photoCommunicator: EventBusPhoto) {
        sdkResultCallbackPhoto?.photoContentResult(photoCommunicator.faceModel, photoCommunicator.imageUri)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun doThis(multiFaceMessage: EventBusMultiplePhotoInFrame) {
        sdkResultCallbackPhoto?.multiPhotoInFrame(multiFaceMessage.message)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun doThis(noFaceMessage: EventBusNoPhotoInFrame) {
        sdkResultCallbackPhoto?.multiPhotoInFrame(noFaceMessage.message)
    }


}