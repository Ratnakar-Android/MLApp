package com.sdk.communacator.listener

import android.net.Uri
import com.sdk.detection.model.FaceModel

interface FaceMLListener {
   fun photoContentResult(faceModel: FaceModel)
   fun photoContentException(message: String)
   fun multiPhotoInFrame()
   fun noPhotoInFrame()
}