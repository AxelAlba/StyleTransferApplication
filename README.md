# Mockingbird

**Mockingbird** is a mobile app that lets the users turn their photos into the style of the famous painters. The app acts like a virtual museum where user can share their works online and appreciate other people's works.

## MCO3 Features
|          Function          |                                                                                                   Description                                                                                                  |
|:--------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|       Register/Log-in      | The user must first register an account using his/her google account. After authentication, the user would be able to automatically login to the Application with his/her google name as his/her display name. |
|       View User Posts      | The user can view the list of posts by other users.                                                                                                                                                            |
| Create Post (from storage) | The user can upload an image from storage and put a caption.                                                                                                                                                   |
|  Create Post (from camera) | The user can upload an image from the camera and put a caption.                                                                                                                                                |
|    Apply Style Transfer    | The user can transfer a painting’s style based on a set of paintings (filters) for posting an image.                                                                                                           |
|      Add Appreciation      | The user can give appreciation to the post (similar to instagram hearts).                                                                                                                                      |

## Comments for MCO3 submission

### For signing in using your gmail account

For the google sign-in, since the application itself is not yet uploaded to google play services, new users must send their SHA-1 key to the developers in order for Firebase Authentication to recognize the device and allow authentication. With this, you may contact the developers:

* axel_alba@dlsu.edu.ph (Axel Alba)
* dan_velasco@dlsu.edu.ph (Dan Velasco)

#### How to get SHA-1 key:
Go to bin folder of your java sdk
Run the terminal from that directory
Run the following command:
```
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
From the output, copy the SHA-1 key and send it to the developers. The developers will add the SHA-1 key to the Firebase configuration.
```


### Minor technical issue:
When updating the data through the adapter, it scrolls up. This behavior can be replicated by pressing the heart icon on the posts. We were not able to find the root cause of this behavior.
