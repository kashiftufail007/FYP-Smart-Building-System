
<h1 align="center">:heart: FYP-Smart-Building-System :heart: </h1>
<h3 align="center">✨ IoT Based Smart Building System ✨ </h3>
<p align="center">
  <a class="badge-align" href="https://github.com/kashiftufail007"><img src="https://api.codacy.com/project/badge/Grade/58e51bc418d349499b3eac9c3f6f3ef1"/></a>
  <a href="https://github.com/kashiftufail007"> <img src="https://circleci.com/gh/mukeshsolanki/MarkdownView-Android/tree/master.svg?style=shield" /></a>
   <a href="https://github.com/kashiftufail007"><img src="https://img.shields.io/badge/License-MIT-blue.svg"/></a>
  <br /><br />
  

</p>



## Table of Contents
* [Splash Screen ](#splash-screen) ✨
* [Login Screen ](#login-screen) ✨
* [Forget Password Solutions](#forget-password) ✨
* [Home Screen ](#home-screen) ✨
* [Schedule Screen ](#schedule-screen) ✨
* [Storage Screen ](#storage-screen) ✨ 
* [Profile Screen ](#profile-screen) ✨
* [Device Management ](#device-management) ✨
* [Schedule Management ](#schedule-management) ✨

	<p align="center">
  <img width="320" height="600" align="right" src="https://user-images.githubusercontent.com/34978760/64927373-995d6300-d823-11e9-9ecb-ef997b154338.gif">
</p>

## Splash Screen 🚀 :heart:
This is animated Splash Screen for My Android App

* bganim 💻
``` xml
<translate android:duration="800" 
  android:toYDelta="30%p" 
    android:fromYDelta="0%"/>
```
* clovernim 💻
``` xml
<alpha android:duration="800" 
  android:toAlpha="0.0"
    android:fromAlpha="1.0"/>
```
* frombottom 💻
``` xml
<translate android:duration="800" 
  android:toYDelta="0%" 
      android:fromYDelta="10%p"/>
```
```xml
<alpha android:duration="600" 
    android:toAlpha="1.0" 
        android:fromAlpha="0.0"/>
```


<p align="center">
  <img width="320" height="600" align="left" src="https://user-images.githubusercontent.com/34978760/64927366-7fbc1b80-d823-11e9-84e0-fcc632184af8.gif">
</p>


##    Login Screen 🚀 :heart:

* Email Format Checking Function  💻
``` java
Pattern EMAIL_ADDRESS_PATTERN = Pattern
    .compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
          + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
              + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");
```

* Use Android &#x1F49C;  Volley &#x1F49C; Framework
     * Import Volley Library to build.gradle 
``` java
            implementation 'com.mcxiaoke.volley:library:1.0.18'
```

  *    Implement a Link with NodeJS API
``` java
    private String Login_GET_User_All_Data_API = 
        "http://10.0.2.2:3000/login_response_data";
````
*    Link Volley to handle Requests 
``` java
    requestQueue = Volley.newRequestQueue(getApplicationContext());
```
<p align="center">
  <img width="320" height="600" align="right" src="https://user-images.githubusercontent.com/34978760/64927842-8d749f80-d829-11e9-8719-f5de18f4971f.png">
</p>
 <br />
  <br />
  
## Forget Password 🚀 :heart:
  
If you forget Password , You can Login Via 
  * Google SignIn
  * Mobile Verification 
   
   
 Here Mobile Code Verification is Done with 
  ### LocalTxt API
  Then You will move to next Activty for verification
<p align="center">
  <img width="320" height="600" align="left" src="https://user-images.githubusercontent.com/34978760/64927843-94031700-d829-11e9-8690-5218c88b7c9d.png">
</p>    
<br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
  <br />
<pre>
          Code Verificaition Activity  💻

               If verification code matched 

                     Move to Home Page
 
                           You can resend code after One Mint 
 
                           You can Change Number
</pre>
  
<p align="center">
  <img width="320" height="600" align="right" src="https://user-images.githubusercontent.com/34978760/64928163-c151c400-d82d-11e9-9c0f-e941cafc247f.gif">
</p>
  <br />
    <br />
      <br />
      
## Home Screen 🚀 :heart:

Button will be Generated Dynamically According to User Access
``` java
final Button btn = new Button(this);
```

On Button Click Data will be Updated in SQLite 💻
``` java 
 SQLite_DATABASE_OBJECT.update_device_on_off_status_detail_table(btn.getId(), 0);

```
<br />
<br />
<br />

<p align="center">
  <img width="320" height="600" align="left" src="https://user-images.githubusercontent.com/34978760/64928167-daf30b80-d82d-11e9-81bd-e2089e0b4b26.gif">
</p>

<br />
<br />
<br />
<br />
<br />
<br />
<br />

## Schedule Screen 🚀 :heart:

Here all Schedule Added will be Display and you can add more
* One Scheddule per Device
* Scheduled Device will not Display again in Drop Down

Array is defined to Store data about devices under Action 
``` java 
  ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
        (this,android.R.layout.simple_spinner_item, list);
```
<p align="center">
  <img width="320" height="600" align="right" src="https://user-images.githubusercontent.com/34978760/64928341-39b98480-d830-11e9-804e-55afd38e6742.gif">
</p>

<br />
<br />
<br />
<br />
<br />
<br />

## Storage Screen 🚀 :heart:

Here all data from database (MySQL) is fetched and Displayed
* Temperature Data
* Humidity Data
* Use Android &#x1F49C;  MPAndroidChart &#x1F49C;   💻
     * Import MPAndroidChart Library to build.gradle

``` java 
  implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
```

<p align="center">
  <img width="320" height="600" align="left" src="https://user-images.githubusercontent.com/34978760/64928381-f57ab400-d830-11e9-9f40-a7e7a874459e.gif">
</p>
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br /> 

## Profile Screen 🚀 :heart:

Here your all data will be display 
  * Your Name 
  * Location 
  * etc
  * Can View Help Manual   &#x1F49C; 
  
 <p align="center">
  <img width="320" height="600" align="right" src="https://user-images.githubusercontent.com/34978760/64928415-9a958c80-d831-11e9-81ad-612a7fbce88c.gif">
  </p>
  
 #### Can Update
 * User Name
 * Phone Number
 * Password
 * Can Manage Devices 
 * Can Manage Schedules 


* Use Android &#x1F49C;  Glide &#x1F49C;  
     * Import Glide Library to build.gradle

``` java 
  implementation 'com.github.bumptech.glide:glide:3.8.0'
```

Update Picture to profile page using Glide 
``` java
    Glide.with(getApplicationContext()).load(kashif_pic)
                    .thumbnail(0.5f)
                     .crossFade()
                     .diskCacheStrategy(DiskCacheStrategy.ALL)
                     .into(imageView_for_profile_picture);
   
```
Implicit Intent in App ( Open messaging app Automatically and write about use)
``` java
Intent n = new Intent(Intent.ACTION_VIEW);
                n.setType("vnd.android-dir/mms-sms");
                n.putExtra("address", "+92308xxxxx");
                n.putExtra("sms_body","User:   "xyz +"\nLocation:   "+abc+"\n Issue:\n    ");
                startActivity(n);
             
 ```
     
   <p align="center">
  <img width="320" height="600" align="left" src="https://user-images.githubusercontent.com/34978760/64942958-dd805000-d883-11e9-82a7-f1655f5dd9f0.gif">
</p>
 
## Device Management  🚀 :heart:

Can be use to  
  ### Enable Devce Control 
  ### Disable Device 
  
  If a Device is Disabled , All Schedule on it will be distroy and no more 
  schedule can be added 
  
 <p align="center">
  <img width="320" height="600" align="right" src="https://user-images.githubusercontent.com/34978760/64943224-9a72ac80-d884-11e9-889b-37db2873badb.gif">
</p>
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />

 
 Add AlertDialog Boxes 
``` java
  AlertDialog.Builder builder = new AlertDialog.Builder(HomePageActivity.this);
                    builder.setTitle("Disable Device !");
                    builder.setMessage("\n If you Disable this device, Schedule on this  will be Delete !");
                    builder.setIcon(R.drawable.ic_sad);
     
```
     
     
   <p align="center">
  <img width="320" height="600" align="left" src="https://user-images.githubusercontent.com/34978760/64943557-7c597c00-d885-11e9-9553-4c137655ad82.gif">
</p> 
<br />
<br />
<br />
<br />

## Schedule Management  🚀 :heart:

Can be use to  
  #### Delete Schedule
  #### After Deletion Device will be release
  #### Delete on Back-end Side too
  #### Send Notification when time reach  and according Action on device
 <br />
<br />

 ``` java
   NotificationManager notificationManager = 
      (NotificationManager) getSystemService(
            Context.NOTIFICATION_SERVICE);
            
   NotificationCompat.Builder notificationBuilder = 
    new NotificationCompat.Builder(
       this, NOTIFICATION_CHANNEL_ID);
  ```
  ## Libraries Imported  🚀 :heart: :heart: 
``` java
    implementation 'com.github.bumptech.glide:glide:3.8.0'
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
    implementation 'com.google.firebase:firebase-auth:16.2.1'
    implementation 'com.google.android.gms:play-services-auth:16.0.1'
    implementation 'pl.droidsonroids.gif:android-gif-drawable:1.+'
    implementation 'com.mcxiaoke.volley:library:1.0.18'
    implementation 'com.android.support:design:28.0.0'
    implementation 'com.shobhitpuri.custombuttons:google-signin:1.0.0'
```

## Backend  NodeJS APIs  🚀 :heart: :heart: 
Use Node JS on Backend with MySQL Database 
File Availble in Code Above 🚀 (Named: SBS APIs)🚀 
  
### Author 👤 :heart:

### Muhammad Kashif  :heart:

* [GitHub Profile](https://github.com/kashiftufail007)
* [Facebook Profile](https://www.facebook.com/KashifCache)
* [LinkedIn Profile](https://www.linkedin.com/in/muhammad-kashif007/)

### License 📝 

Copyright © 2019-2020, [Muhammad Kashif](https://github.com/kashiftufail007).
Released under the [MIT License](LICENSE).

Please ⭐️ this repository if this project helped you!
<br />
[More Projects ](https://github.com/kashiftufail007?tab=repositories)  :muscle: :muscle: :muscle:
  
