package com.sdk.recognisation

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.sdk.communacator.listener.TextMLListener
import com.sdk.communacator.listener.TextResultListener

object ReadTextFromCapturedImage {

    fun getTextFromImage(capturedImage: InputImage, updateListener: TextMLListener) {
        val textRecognizer = getTextRecognizer()
        textRecognizer.process(capturedImage)
            .addOnSuccessListener { completeText ->
                updateListener.textContentResult(completeText.text)
            }
            .addOnFailureListener { e ->
                e.message?.let { updateListener.textContentException(it) }
            }
    }

    private fun getTextRecognizer(): TextRecognizer {
        return TextRecognition.getClient()
    }

    //This way we can read all content value frame wise.
    private fun getTextContent(completeText: Text) {
        val resultText = completeText.text
        for (block in completeText.textBlocks) {
            val blockText = block.text
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            for (line in block.lines) {
                val lineText = line.text
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                for (element in line.elements) {
                    val elementText = element.text
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                }
            }
        }
    }
}