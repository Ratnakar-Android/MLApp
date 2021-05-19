package com.sdk.camera.utils

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import java.io.IOException

class CreateInputImage {

  fun  getInputImageFromUri(baseContext: Context?, imageUri: String): InputImage? {
      var uri = Uri.parse(imageUri)
      var image: InputImage? = null
      try {
          image = baseContext.let {
              uri?.let { it1 ->
                  it?.let { it2 ->
                      InputImage.fromFilePath(
                          it2,
                          it1
                      )
                  }
              }
          }
      } catch (e: IOException) {
          e.printStackTrace()
      }
      return image
    }
}