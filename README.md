Box Android Preview SDK
==============
This SDK enables you to support previewing Box files within your application.

Types of files supported:
- images
- PDFs
- audio/video files
- markdown/text/code files
- Office documents

Currently not supported:
- Box Notes

Developer Setup
--------------
The box preview sdk is currently private, and distributed as an aar file through maven:
```gradle
    compile 'com.box:box-android-preview-sdk:2.0.0'
    compile 'com.android.support:cardview-v7:23.4.0'
    compile 'com.android.support:appcompat-v7:23.4.0'
```

Please refer to the build.gradle file in box-preview-sample for setting up your gradle dependencies.


Quickstart
--------------
Refer to  [box-content-sdk](https://github.com/box/box-android-content-sdk) for details on how to set your client id, and secret;
and authenticate the user.

You can get a BoxFile instance using the [box-content-sdk](https://github.com/box/box-android-content-sdk) 
or the [box-browse-sdk](https://github.com/box/box-android-browse-sdk) 

The simplest way to preview a single file, is to start a BoxPreviewActivity, the intent for which can be created using the createIntentBuilder factory method.
```java
     BoxPreviewActivity.IntentBuilder builder = BoxPreviewActivity.createIntentBuilder(this, boxSession, boxFile);
              
```

####Paging through multiple images, audio or video files
Passing the BoxFolder to the intent builder BoxPreviewActivity allows the sdk to also load other files
of the same type from the parent folder into a custom ViewPager called the BoxPreviewViewPager. 
Another way of doing this, is to directly pass a BoxItems collection to the builder.

```java

     builder.setBoxFolder(boxFolder);
                
  
```

Precaching files:

To preload files into the cache, without presenting them, make use of com.box.androidsdk.preview.BoxPreviewViewPager.getCacheFileRequest api.

```java
     FutureTask task = BoxPreviewViewPager.getCacheFileRequest(boxSession, previewStorage, boxFile);
     
```
Customizing cache policy:

You can customize the max cache size (default 500MB), and cache age (default 90 days) by defininig

com.box.androidsdk.preview.PreviewStorage.MAX_CACHE_SIZE 
com.box.androidsdk.preview.PreviewStorage.MAX_CACHE_AGE

Cache age defines how long an unaccessed file will stay in the cache, if it hasn't been cleared out of the LRU cache for memory.


Sample App
--------------
A sample app can be found in the [box-preview-sample](https://github.com/box/box-android-preview-sdk) folder.

Copyright and License
--------------
Copyright 2015 Box, Inc. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
