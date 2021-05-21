# MLApp

In the repo code, We have an android App which has android sdk integrated. Sdk is using Android ML 
tool kit for reading text from captured images and doing processing the Photo detection.

Prerequisites:

Branch name : master
Try to run the App on a real device to see the exact flow.

GutHub repo contains:

1. Image processing sdk based on ML kit.
2. Sample App.
3. Sdk flow document.


Initialize the sdk from Host app in onStart() method:

   override fun onStart() {
        super.onStart()
        SdkCommunicatorImpl.instance.registerSdk()
    }

    
Text Recognition Flow :

-> We are calling the sdk class (SdkCommunicatorImpl) and passing the interface (TextResultListener) for getting the result back.

                SdkCommunicatorImpl.instance.initCameraForTextRecognition(this, object : TextResultListener {

                        override fun textContentResult(photoTextContent: String) {
                             // Here app will get the result from Sdk
                        }
                        override fun textContentException(errorMessage: String) {
                            // Here app will get the exception while reading the text from Sdk
                        }
                    })
            }


Face Detection Flow:

-> We are calling the sdk class (SdkCommunicatorImpl) and passing the interface (PhotoResultListener) for getting the result back. 
   We have differnt methods based on sdk response result.

               SdkCommunicatorImpl.instance.initCameraForFaceDetection(this, object :
                    PhotoResultListener {

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


Unregister the sdk from Host app in onStart() method:


    override fun onDestroy() {
        super.onDestroy()
        SdkCommunicatorImpl.instance.unRegisterSdk()
    }





Fyi : For testing the feature we have stored image internally and reading text and detecting face from that, 
      if there is mutiface in a photo testing framework is giving error.

Client (Host App) - Sdk interaction:


<img width="1170" alt="Screenshot 2021-05-21 at 2 47 41 PM" src="https://user-images.githubusercontent.com/58584559/119114606-ee636a80-ba43-11eb-8b85-03745be0f232.png">



Flow Diagram :

![ML App Flow Diagram](https://user-images.githubusercontent.com/58584559/119109082-7e061a80-ba3e-11eb-9055-d3faa74cf2ee.png)






