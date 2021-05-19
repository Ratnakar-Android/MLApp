package com.sdk.detection.model

import android.graphics.PointF

data class FaceModel(
    var headEulerAngleZ: Float? = 0F,
    var headEulerAngleY: Float? = 0F,
    var mouthleftPos: PointF? = null,
    var mouthRightPos: PointF? = null,
    var mouthBottomPos: PointF? = null,
    var rightEyePos: PointF? = null,
    var leftEyePos: PointF? = null,
    var leftEyeOpenProbability: Float? = 0F,
    var faceId: Int? = 0,
    var rightEyeOpenProbability: Float? = 0F,
    var smilingProbability: Float? = 0F,
    var leftEarPos: PointF? = null,
    var rightEarPos: PointF? = null
)



