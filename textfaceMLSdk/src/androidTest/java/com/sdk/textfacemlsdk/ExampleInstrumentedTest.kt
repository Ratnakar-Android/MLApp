package com.sdk.textfacemlsdk

import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.mlkit.vision.common.InputImage
import com.sdk.communacator.listener.FaceMLListener
import com.sdk.communacator.listener.TextMLListener
import com.sdk.detection.DetectFaceFromCapturedImage
import com.sdk.detection.model.FaceModel
import com.sdk.recognisation.ReadTextFromCapturedImage
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch


@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun textRecognitionTestCase() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val fileName = "users.jpeg"
        val assets = InstrumentationRegistry.getInstrumentation().targetContext.assets
        val file = assets.open(fileName)

        val bitmap = BitmapFactory.decodeStream(file)
        val bitImage = InputImage.fromBitmap(bitmap, 0)
        val latch = CountDownLatch(1)
         ReadTextFromCapturedImage.getTextFromImage(bitImage, object : TextMLListener {
                override fun textContentResult(photoTextContent: String) {
                    assertTrue(photoTextContent != null)
                    latch.countDown()
                }

                override fun textContentException(message: String) {
                    Assert.assertFalse(message != null)
                    latch.countDown()
                }
            })

        try {
            latch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun ImageDetectionTestCase() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val fileName = "user.jpeg"
        val assets = InstrumentationRegistry.getInstrumentation().targetContext.assets
        val file = assets.open(fileName)

        val bitmap = BitmapFactory.decodeStream(file)
        val bitImage = InputImage.fromBitmap(bitmap, 0)
        val latch = CountDownLatch(1)
        DetectFaceFromCapturedImage.detectFaces(bitImage, object : FaceMLListener {
            override fun photoContentResult(faceModel: FaceModel) {
                assertTrue(faceModel.faceId != null)
                latch.countDown()
            }

            override fun photoContentException(message: String) {
                assertFalse(true)
                latch.countDown()
            }

            override fun multiPhotoInFrame() {
                assertFalse(true)
                latch.countDown()
            }

            override fun noPhotoInFrame() {
                assertFalse(true)
                latch.countDown()
            }
        })

        try {
            latch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun MultiFaceDetectionTestCase() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val fileName = "users.jpeg"
        val assets = InstrumentationRegistry.getInstrumentation().targetContext.assets
        val file = assets.open(fileName)

        val bitmap = BitmapFactory.decodeStream(file)
        val bitImage = InputImage.fromBitmap(bitmap, 0)
        val latch = CountDownLatch(1)
        DetectFaceFromCapturedImage.detectFaces(bitImage, object : FaceMLListener {
            override fun photoContentResult(faceModel: FaceModel) {
                assertFalse(true)
                latch.countDown()
            }

            override fun photoContentException(message: String) {
                assertFalse(true)
                latch.countDown()
            }

            override fun multiPhotoInFrame() {
                assertTrue(true)
                latch.countDown()
            }

            override fun noPhotoInFrame() {
                assertFalse(true)
                latch.countDown()
            }
        })

        try {
            latch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}