## ImageBrowser
ImageBrowser is an images browser and picker for Android<br>
<br>
![screenshot](https://github.com/acuna-public/ImageBrowser/blob/master/screenshot.png?raw=true)<br>
<br>
**Usage**

~~~java
ImagePicker picker = new ImagePicker (MainActivity.this);

picker.setToolbarTitle (R.string.select_images);          // Images activity title
picker.setToolbarDescr (R.string.select_images_descr);    // Images activity description
picker.setMinNum (3);                                     // Minimum number images to select (optional)
picker.setMaxNum (5);                                     // Maximum number of images to select (optional)
picker.setMinNumMess (String.format (getString (R.string.app_add_min_screenshots), String.valueOf (3))); // Message when minimum images number reaches (optional)
picker.setMaxNumMess (String.format (getString (R.string.app_add_max_screenshots), String.valueOf (5))); // Message when maximum images number reaches (optional)
picker.start ();                                          // Don't forget to call this to start the activity
~~~
