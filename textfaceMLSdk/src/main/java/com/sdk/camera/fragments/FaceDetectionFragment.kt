package com.sdk.camera.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sdk.camera.utils.Constants.FACE_FRAGMENT
import com.sdk.camera.utils.CreateInputImage
import com.sdk.communacator.EventBus.EventBusMultiplePhotoInFrame
import com.sdk.communacator.EventBus.EventBusNoPhotoInFrame
import com.sdk.communacator.EventBus.EventBusPhoto
import com.sdk.communacator.EventBus.EventBusTextException
import com.sdk.communacator.listener.FaceMLListener
import com.sdk.detection.DetectFaceFromCapturedImage
import com.sdk.detection.model.FaceModel
import com.sdk.textfacemlsdk.R
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus


class FaceDetectionFragment : Fragment() {

    private val args: FaceDetectionFragmentArgs by navArgs()
    private var imageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUri = args.imageUri
        if (imageUri != "Nothing") {
            startFaceDetection()
        } else {
            navigateToCamera()
        }
    }

    private fun navigateToCamera() {
        val action = FaceDetectionFragmentDirections.actionPermissionsToCamera(FACE_FRAGMENT)
        findNavController().navigate(action)
    }

    private fun startFaceDetection() {
        var inputImage =
            imageUri?.let { CreateInputImage().getInputImageFromUri(activity?.baseContext, it) }
        CoroutineScope(Dispatchers.IO).launch {
            inputImage?.let {
                DetectFaceFromCapturedImage.detectFaces(it, object : FaceMLListener {

                    override fun photoContentResult(faceModel: FaceModel) {
                        EventBus.getDefault().post(EventBusPhoto(faceModel, Uri.parse(imageUri)))
                        closeTheActivity()
                    }

                    override fun photoContentException(message: String) {
                        EventBus.getDefault().post(EventBusTextException(message))
                        closeTheActivity()
                    }

                    override fun multiPhotoInFrame() {
                        EventBus.getDefault().post(EventBusMultiplePhotoInFrame(resources.getString(R.string.multi_face)))
                        closeTheActivity()
                    }

                    override fun noPhotoInFrame() {
                        EventBus.getDefault().post(EventBusNoPhotoInFrame(resources.getString(R.string.no_face)))
                        closeTheActivity()
                    }
                })
            }
        }
    }

    private fun closeTheActivity() {
        activity?.finish()
    }
}
