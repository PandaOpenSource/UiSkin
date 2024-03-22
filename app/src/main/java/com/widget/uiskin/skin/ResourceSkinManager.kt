package com.widget.uiskin.skin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import com.widget.uiskin.R
import com.widget.uiskin.SkinMap

/**
 * @des
 * @author: xujixiong
 * @create:2024/3/13 16:11
 */
class ResourceSkinManager {

    private val mImageRedirectMap = SkinMap().skinMap

    companion object {

        private const val TAG = "ResourceRedirectHelper"

        private val sInstance: ResourceSkinManager = ResourceSkinManager()

        fun getInstance(): ResourceSkinManager {
            return sInstance
        }
    }

    fun init(app: Application) {
        tryHookDrawableR()
        tryHookLayoutInflater(app)
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun tryHookLayoutInflater(app: Application) {
        val tryHookImp: (Activity) -> Unit = { activity ->
            try {
                val clzz = LayoutInflater::class.java
                val factory = activity.layoutInflater.factory2
                if (factory != null) {
                    val field = clzz.getDeclaredField("mFactory2")
                    field.isAccessible = true
                    val newFactory2 = SkinLayoutInflaterFactory(factory)
                    field.set(activity.layoutInflater, newFactory2)
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message ?: "")
            }
        }

        app.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                tryHookImp.invoke(activity)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }

    private fun tryHookDrawableR() {
        try {
            val clzz = R.drawable::class.java
            clzz.fields.forEach { field ->
                field.isAccessible = true
                val current = field.getInt(null)
                val replace = redirectImageRes(current)
                if (replace != 0 && replace != current) {
                    field.set(null, replace)
                    Log.d(TAG, field.name)
                }
            }
        } catch (e: Throwable) {
            Log.d(TAG, e.message ?: "")
        }
    }

    fun redirectImageRes(@DrawableRes id: Int): Int {
        val replace = mImageRedirectMap[id]
        return if (replace == null || replace == 0) id else replace
    }
}