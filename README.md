# MLApp

This repo consist of an android App which has integrated sdk. 
This sdk makes use of Android ML tool kit for reading text and detecting photo from captured images

Branch name : master

Try to run the App on a real device to see the exact flow.

GutHub repo contains:

1. Image processing sdk based on ML kit.
2. Sample App.
3. Sdk flow document.


<b> Text Recognition flow <b/>
 

        SdkCommunicatorImpl.instance.initCameraForTextRecognition(
            this,
            object : TextResultListener {

                override fun textContentResult(photoTextContent: String) {
                    // Here app will get the result from Sdk
                }
                override fun textContentException(errorMessage: String) {
                    // Here app will get the exception while reading the text from Sdk
                }
            })
    }
    
 <b> Face Detection Flow </b>

        SdkCommunicatorImpl.instance.initCameraForFaceDetection(
            this,
            object : PhotoResultListener {

            override fun photoContentResult(faceModel: FaceModel, imageUri: Uri?) {
                // Result from sdk
            }
            override fun photoContentException(errorMessage: String) {
                // Error message from sdk
            }
            override fun multiPhotoInFrame(multiFaceMessage: String) {
                // Multiface message from sdk
            }
            override fun noPhotoInFrame(noFaceMessage: String) {
                // No face message from sdk
            }
        })


 <b> Unregister the sdk from Host app </b>

        override fun onDestroy() {
            super.onDestroy()
            SdkCommunicatorImpl.instance.unRegisterSdk()
        }


Flow Diagram :

![ML App Flow Diagram](https://user-images.githubusercontent.com/58584559/119109082-7e061a80-ba3e-11eb-9055-d3faa74cf2ee.png)






