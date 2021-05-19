package com.sdk.communacator.listener

interface TextResultListener {
   fun textContentResult(photoTextContent: String)
   fun textContentException(message: String)
}