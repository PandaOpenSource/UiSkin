package com.widget.uiskin

import android.app.Application
import com.widget.uiskin.skin.ResourceSkinManager

/**
 * @des
 * @author: xujixiong
 * @create:2024/3/22 14:53
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ResourceSkinManager.getInstance().init(this)
    }
}