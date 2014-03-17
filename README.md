Access a self signed website content
====================================

Access contents of a website with self-signed certificate from your android app



### After import this project do the following in MainActivity.java

* At line 27 change value of hostURL variable to match your situation
```
hostURL = "https://YOURHOST";	// Change this line 
```
* Import your server certificate and paste a copy in raw folder res/raw
* At line 29 change value of hostCertificate variable to match certificate filename, but without the extension (.crt)
```
hostCertificate = getResources().openRawResource(R.raw.host);
```
