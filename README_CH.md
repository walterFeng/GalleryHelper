
<a href="./README.md"><img src="https://img.shields.io/badge/Doc-English-green" alt="Build Status"></a>

<a href="./CHANGE_LOG.md"><img src="https://img.shields.io/badge/Doc-%E7%89%88%E6%9C%AC%E5%8E%86%E5%8F%B2-red" alt="Build Status"></a>

### GalleryHelper
   使用RecyclerView实现类似画廊翻页滑动的效果；使用简单，无须重写recyclerView或者layoutManager的任何方法。

### Gif图演示效果
   ![](image/gallery_demo_show.gif)

### 使用
1. 在你的项目 build.gradle 文件中添加依赖, GalleryHelper 已经发布到 jitPack 上:
   ```groovy
   //在你项目更目录的build.gradle中添加:
   classpath 'com.github.dcendents:android-maven-gradle-plugin:2.1'//放到dependencies下
   maven { url 'https://jitpack.io' } //放到repositories下

   //在你项目的app目录下的build.gradle中添加:
   implementation 'com.github.walterFeng:GalleryHelper:1.0.2'
   ```

2. `setContentView()`之后, 将 GalleryHelper 加载到RecyclerView上:
    ```kotlin
    // 初始化RecyclerView并设置Adapter:
    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
    val adapter = setupAdapter(recyclerView)

    // 使 RecyclerView 支持 gallery 效果:
    helper = GalleryHelper.from(recyclerView)
        .attach(loopParams = 500, itemSpace = 40, scale = 0.87f, alpha = 0.9f)

    // 添加翻页监听:
    helper?.pagingScrollHelper?.setOnPageChangedListener(object :
        PagingScrollHelper.OnPageChangedListener {
        override fun onPageChange(index: Int) {
            val i = helper?.lopperHelper?.gerCurrentRealPosition() ?: 0
            val position = i % adapter.itemCount
            Log.d("walter", "onPageChange:$index || position=$position")
        }
    })
    ```
3. 设置 paddingLeft 和 paddingRight ( 如果需要是纵向滑动，设置 paddingTop 和 paddingBottom) 到你的 RecyclerView 上 , 必须在XML里为 RecyclerView 添加 `android:clipToPadding="false"`:
   ```xml
   <android.support.v7.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:clipToPadding="false"
        android:paddingLeft="50dp"
        android:paddingRight="50dp"/>
   ```
4. 其他方法支持:
   ```kotlin
   helper?.pagingScrollHelper?.setOnPageChangedListener(listener)
   helper?.pagingScrollHelper?.setAutoScroll(enable,duration)
   helper?.pagingScrollHelper?.scrollToNextPage(skip)
   //...
   helper?.lopperHelper?.setLoopEnable(loop)
   //...
   helper?.scaleTransformHelper?.setAnimatorParams(scale,alpha)
   ```
   PagingScrollHelper, LopperHelper 和 ScaleTransformHelper 支持结合RecyclerView单独使用
    
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