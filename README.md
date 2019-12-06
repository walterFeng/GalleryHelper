
<a href="./README_CH.md"><img src="https://img.shields.io/badge/Doc-%E4%B8%AD%E6%96%87%E6%96%87%E6%A1%A3-green" alt="Build Status"></a> 

<a href="./CHANGE_LOG.md"><img src="https://img.shields.io/badge/Doc-ChangeLog-red" alt="Build Status"></a>

### GalleryHelper
   This is a very simple library for Android that allows you to view image as gallery using RecyclerView

### Video
   ![](image/gallery_demo_show.gif)

### Usage
1. Add the dependencies to your build.gradle file, GalleryHelper is avaiable in jitPack:
   ```groovy
   //in your root project build.gralde file:
   classpath 'com.github.dcendents:android-maven-gradle-plugin:2.1'//dependencies 
   maven { url 'https://jitpack.io' } //repositories

   //in your app project build.gralde file:
   implementation 'com.github.walterFeng:GalleryHelper:1.0.0'
   ```

2. Attach to recyclerView after `setContentView()`:
    ```kotlin
    // init recyclerView:
    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
    val adapter = setupAdapter(recyclerView)

    // support gallery:
    helper = GalleryHelper.from(recyclerView)
        .attach(loopParams = 500, itemSpace = 40, scale = 0.87f, alpha = 0.9f)

    // add page changed listener:
    helper?.pagingScrollHelper?.setOnPageChangedListener(object :
        PagingScrollHelper.OnPageChangedListener {
        override fun onPageChange(index: Int) {
            val i = helper?.lopperHelper?.gerCurrentRealPosition() ?: 0
            val position = i % adapter.itemCount
            Log.d("walter", "onPageChange:$index || position=$position")
        }
    })
    ```
3. Set paddingLeft and paddingRight( or LinearLayoutManager.HORIZONTAL: paddingTop and paddingBottom) to your RecyclerView , you need add `android:clipToPadding="false"` for `RecyclerView` in your layout XML:
   ```xml
   <android.support.v7.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:clipToPadding="false"
        android:paddingLeft="50dp"
        android:paddingRight="50dp"/>
   ```
4. Other support:
   ```kotlin
   helper?.pagingScrollHelper?.setOnPageChangedListener(listener)
   helper?.pagingScrollHelper?.setAutoScroll(enable,duration)
   helper?.pagingScrollHelper?.scrollToNextPage(skip)
   //...
   helper?.lopperHelper?.setLoopEnable(loop)
   //...
   helper?.scaleTransformHelper?.setAnimatorParams(scale,alpha)
   ```
   PagingScrollHelper, LopperHelper android ScaleTransformHelper can use alone 
    
### License

    Android Gallery Helper.
    https://github.com/walterFeng/GalleryHelper/

	Copyright 2019 Walter Feng

	Licensed under the Apache License, Version 2.0 (the "License");	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
		http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.