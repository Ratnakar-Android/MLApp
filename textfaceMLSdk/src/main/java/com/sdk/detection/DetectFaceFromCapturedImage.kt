package com.sdk.detection

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import com.sdk.communacator.listener.FaceMLListener
import com.sdk.detection.model.FaceModel
import kotlinx.coroutines.Deferred

object DetectFaceFromCapturedImage {

    fun detectFaces(image: InputImage, updateListener: FaceMLListener) {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()

        val detector = FaceDetection.getClient(options)
        detector.process(image)
            .addOnSuccessListener { faces ->
                when (faces.size) {
                    0 -> updateListener.noPhotoInFrame()
                    1 -> updateListener.photoContentResult(processFaceList(faces[0]))
                    else -> updateListener.multiPhotoInFrame()
                }
            }
            .addOnFailureListener { e ->
                e.message?.let { updateListener.photoContentException(it) }
            }
    }

    private fun processFaceList(face: Face): FaceModel {

        var faceObject = FaceModel()

        faceObject.headEulerAngleY = face.headEulerAngleY
        faceObject.headEulerAngleZ = face.headEulerAngleZ
        val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
        leftEar?.let {
            faceObject.leftEarPos = leftEar.position
        }
        val rightEar = face.getLandmark(FaceLandmark.RIGHT_EAR)
        rightEar?.let {
            faceObject.rightEarPos = rightEar.position
        }
        val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)
        leftEye?.let {
            faceObject.leftEyePos = leftEye.position
        }
        val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)
        rightEye?.let {
            faceObject.rightEyePos = rightEye.position
        }
        val mouthBottom = face.getLandmark(FaceLandmark.MOUTH_BOTTOM)
        mouthBottom?.let {
            faceObject.mouthBottomPos = mouthBottom.position
        }
        val mouthRight = face.getLandmark(FaceLandmark.MOUTH_RIGHT)
        mouthRight?.let {
            faceObject.mouthRightPos = mouthRight.position
        }
        val mouthLeft = face.getLandmark(FaceLandmark.MOUTH_LEFT)
        mouthLeft?.let {
            faceObject.mouthleftPos = mouthLeft.position
        }
        if (face.leftEyeOpenProbability != null) {
            faceObject.leftEyeOpenProbability = face.leftEyeOpenProbability
        }
        if (face.rightEyeOpenProbability != null) {
            faceObject.rightEyeOpenProbability = face.rightEyeOpenProbability
        }
        if (face.smilingProbability != null) {
            faceObject.smilingProbability = face.smilingProbability
        }
        // If face tracking was enabled:
        if (face.trackingId != null) {
            faceObject.faceId = face.trackingId
        }
        return faceObject
    }
}