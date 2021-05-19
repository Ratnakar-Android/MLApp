package com.sdk.camera.fragments

import android.hardware.camera2.CameraMetadata.LENS_FACING_BACK
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.mlkit.vision.common.InputImage
import com.sdk.camera.utils.Constants.TEXT_FRAGMENT
import com.sdk.camera.utils.CreateInputImage
import com.sdk.communacator.EventBus.EventBusMultiplePhotoInFrame
import com.sdk.communacator.EventBus.EventBusPhoto
import com.sdk.communacator.EventBus.EventBusText
import com.sdk.communacator.EventBus.EventBusTextException
import com.sdk.communacator.listener.FaceMLListener
import com.sdk.communacator.listener.TextMLListener
import com.sdk.communacator.listener.TextResultListener
import com.sdk.detection.DetectFaceFromCapturedImage
import com.sdk.detection.model.FaceModel
import com.sdk.recognisation.ReadTextFromCapturedImage
import com.sdk.textfacemlsdk.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class TextRecognitionFragment : Fragment() {

    private val args: TextRecognitionFragmentArgs by navArgs()
    private var imageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUri = args.imageUri
        if (imageUri != "Nothing") {
            startTextRecognition()
        } else {
            navigateToCamera()
        }
    }

    private fun navigateToCamera() {
        val action = TextRecognitionFragmentDirections.actionPermissionsToCamera(TEXT_FRAGMENT)
        findNavController().navigate(action)
    }

    private fun startTextRecognition() {
        var inputImage =
            imageUri?.let { CreateInputImage().getInputImageFromUri(activity?.baseContext, it) }

        CoroutineScope(Dispatchers.IO).launch {
            inputImage?.let {
                ReadTextFromCapturedImage.getTextFromImage(it, object : TextMLListener {
                    override fun textContentResult(photoTextContent: String) {
                        EventBus.getDefault().post(EventBusText(photoTextContent))
                        closeTheActivity()
                    }

                    override fun textContentException(expMessage: String) {
                        EventBus.getDefault().post(EventBusTextException(expMessage))
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
