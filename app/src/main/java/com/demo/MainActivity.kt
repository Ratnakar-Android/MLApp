package com.demo

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.demo.facedetectionapplication.R
import com.sdk.communacator.EventBus.EventBusMultiplePhotoInFrame
import com.sdk.communacator.SdkCommunicatorImpl
import com.sdk.communacator.listener.PhotoResultListener
import com.sdk.communacator.listener.TextResultListener
import com.sdk.detection.model.FaceModel
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main entry point into our app. This app follows the single-activity pattern, and all
 * functionality is implemented in the form of fragments.
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onStart() {
        super.onStart()
        SdkCommunicatorImpl.instance.registerSdk()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text_recognition.setOnClickListener(this)
        face_detection.setOnClickListener(this)
        back_button.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.text_recognition -> {
                SdkCommunicatorImpl.instance.initCameraForTextRecognition(
                    this,
                    object : TextResultListener {

                        override fun textContentResult(photoTextContent: String) {
                            setTextViewUI(photoTextContent)
                            if(TextUtils.isEmpty(photoTextContent)){
                                Toast.makeText(
                                    this@MainActivity,
                                    "There is no text available in the taken photograph, Please try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun textContentException(errorMessage: String) {
                            Toast.makeText(
                                this@MainActivity,
                                "There is error in TextReading please try again and error is $errorMessage",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            }
            R.id.face_detection -> {
                SdkCommunicatorImpl.instance.initCameraForFaceDetection(this, object :
                    PhotoResultListener {

                    override fun photoContentResult(faceModel: FaceModel, imageUri: Uri?) {
                        setPhotoViewUI(faceModel, imageUri)
                    }

                    override fun photoContentException(errorMessage: String) {
                        Toast.makeText(
                            this@MainActivity,
                            "There is error in TextReading please try again and error is $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun multiPhotoInFrame(multiFaceMessage: String) {
                        Toast.makeText(
                            this@MainActivity,
                            "$multiFaceMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun noPhotoInFrame(noFaceMessage: String) {
                        Toast.makeText(
                            this@MainActivity,
                            "$noFaceMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            }

            R.id.back_button -> {
                text_recognition.visibility = View.VISIBLE
                face_detection.visibility = View.VISIBLE
                text_content_layout.visibility = View.GONE
                image_content_layout.visibility = View.GONE
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        SdkCommunicatorImpl.instance.unRegisterSdk()
    }

    fun setTextViewUI(photoTextContent: String) {
        text_recognition.visibility = View.GONE
        face_detection.visibility = View.GONE
        text_content_layout.visibility = View.VISIBLE
        text_content.text = photoTextContent
    }

    fun setPhotoViewUI(faceModel: FaceModel, imageUri: Uri?) {
        text_recognition.visibility = View.GONE
        face_detection.visibility = View.GONE
        text_content_layout.visibility = View.GONE
        image_content_layout.visibility = View.VISIBLE
        image_view.setImageURI(imageUri)

        val faceId = faceModel.faceId.toString()
        text2.text = "Face id is :->  $faceId"

        val leftEarPos = faceModel.leftEarPos.toString()
        text3.text = "Left Ear position is :->  $leftEarPos"

        val rightEarPos = faceModel.rightEarPos.toString()
        text4.text = "Right Ear position is :->  $rightEarPos"

        val smilingProbability = faceModel.smilingProbability.toString()
        text5.text = "Smiling probability is :-> $smilingProbability"

        val leftEyeOpenProbability = faceModel.leftEyeOpenProbability.toString()
        text6.text = "Left eye open probability is :-> $leftEyeOpenProbability"

        val rightEyeOpenProbability = faceModel.rightEyeOpenProbability.toString()
        text7.text = "Right eye open probability is :-> $rightEyeOpenProbability"

        val mouthBottomPos = faceModel.mouthBottomPos.toString()
        text8.text = "Mouth bottom position is :-> $mouthBottomPos"

        val mouthRightPos = faceModel.mouthRightPos.toString()
        text9.text = "Mouth right position is :-> $mouthRightPos"

        val headEulerAngleZ = faceModel.headEulerAngleZ.toString()
        text10.text = "Head euler angle Z is :-> $headEulerAngleZ"

        val headEulerAngleY = faceModel.headEulerAngleY.toString()
        text11.text = "Head euler angle Y is :-> $headEulerAngleY"

    }
}
