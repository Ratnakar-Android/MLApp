package com.sdk.communacator.listener

interface TextMLListener {
   fun textContentResult(photoTextContent: String)
   fun textContentException(message: String)
}