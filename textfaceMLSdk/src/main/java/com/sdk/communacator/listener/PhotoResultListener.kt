package com.sdk.communacator.listener

import android.net.Uri
import com.sdk.communacator.EventBus.EventBusMultiplePhotoInFrame
import com.sdk.detection.model.FaceModel

interface PhotoResultListener {
   fun photoContentResult(faceModel: FaceModel, imageUri: Uri?)
   fun photoContentException(message: String)
   fun multiPhotoInFrame(multiFaceMessage: String)
   fun noPhotoInFrame(noFaceMessage: String)
}