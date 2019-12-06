
### Usage

 - attach to recyclerView:
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

    ![](image/gallery_demo_show.gif)