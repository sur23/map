Android App for Current Location


#Instructions

1.Download Google Play Services from SDK Manager.

2.In Build.gradle add dependency for google:

    implementation 'com.google.android.gms:play-services-maps:15.0.1'
    implementation 'com.google.android.gms:play-services-location:15.0.1'    
    
3.Add premissions in androidmanifest.xml:

     <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
     <uses-permission android:name="android.permission.INTERNET"/>
     
#This repo contains the following Detail:

1. Retrieve the current location from a device and show on Google Map Using Google Fused Api.

2. Show Current Location Address On Marker Using Geocode API to display a device's location as an address.

3. Show Mumbai Airport and Chennai Airport Location and Address On Marker Using Geocode API to display address.

4. Show Polyline Between Mumbai Airport and Chennai Airport to Current Location.
