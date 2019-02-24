
# Wordpress Blog
Wordpress blog is a free solution for creating an Android mobile app for your **WORDPRESS** blog. It does not require any advanced knowledge of programming and requires you to follow simple instructions for customisation according to your needs.
![All articles](https://i.imgur.com/49ERTLB.jpg =250x)
![Categories](https://i.imgur.com/QAZB82w.jpg =250x)
![Article](https://i.imgur.com/8BJYC2l.jpg =250x)
# Usage
### Using Android Studio or IntelliJ IDEA (Recommended):
This approach takes more time but is the recommended way of doing it since it is easier to do and leaves less space for error
 - **STEP 1:** If you do not have Android Studio installed, go to this [link](https://developer.android.com/studio/#downloads) and download it. Follow [this](https://www.tutorialspoint.com/android/android_studio.htm) tutorial for setup if you need help.
 -  **STEP 2:** Download this repository. You can either
 `git clone https://github.com/burhanuday/WordPress-Blog-App-Using-RxJava-Retrofit-Butterknife`
or download this repository as a zip file by pressing the `Clone or download button` then unzipping the file.
 - **STEP 3:** Navigate to this path - `/app/src/main` and follow these steps:
	 1. Inside the `res` folder, go to `res/values/strings.xml` and replace `Wordpress Blog` with whatever you want the app's name to be, preferably the name of the blog. User a code editor like [Notepad++](https://notepad-plus-plus.org) to do this. This name will be visible on the Android's launcher
	 2. Go to [Android Asset Studio](https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html#foreground.type=clipart&foreground.clipart=android&foreground.space.trim=1&foreground.space.pad=0.25&foreColor=rgba%2896,%20125,%20139,%200%29&backColor=rgb%2868,%20138,%20255%29&crop=0&backgroundShape=square&effects=none&name=ic_launcher) and create a logo file for yourself (It is important that you do this step for your logo to be properly visible on the phone). Extract the zip file and make sure that you keep the name `ic_launcher`. Now copy all the extracted folders and paste them in the `res` folder that you earlier edited. Replace files when asked. This step changes the icon that is visible in the phone launcher.
	 3. In `/res/drawable/` folder, replace `burhanuday_logo.png` with your own png logo file but make sure the name is still `burhanuday_logo.png`. This file will be visible on the splash screen that appears when the app opens.
	 4. The next step is to add the URL of your blog. Go to `/app/src/main/java/com/burhanuday/wordpressblog/Const.java` file and open it using the code editor you earlier installed. You will see a line that says `public static final String BASE_URL = "https://www.androidhive.info/wp-json/wp/v2/";` . You have to change the URL to your own Wordpress blog. If your blog is `www.example.xyz` then that line should be `public static final String BASE_URL = "https://www.example.xyz/wp-json/wp/v2/";`
	 5. The final step is to change the package name. Each android app has a unique id that identifies it on the Play Store or the phone. Two apps cannot have the same id. So if you want to publish your app to the store or want to distribute it on your website, it is essential that you do this step. Follow [this guide](https://stackoverflow.com/questions/16804093/rename-package-in-android-studio) on changing the package name.
- **STEP 4:** You now have to build your apk file that you want to distribute. You can use [this guide](https://abhiandroid.com/androidstudio/generate-signed-apk-android-studio.html) to learn how to generate a release version of your app.

If you reached this part of the readme then you have an Android app for your Wordpress blog and around **$200** in saved money according to [freelancer.com](https://www.freelancer.com)

# License
MIT