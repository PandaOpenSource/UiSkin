package com.widget.uiskin.skin

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView

/**
 * @des
 * @author: xujixiong
 * @create:2024/3/14 10:24
 */
class SkinLayoutInflaterFactory(private val origin: LayoutInflater.Factory2) : LayoutInflater.Factory2 {

    companion object {
        const val ANDROID_NAME_SPACE = "http://schemas.android.com/apk/res/android"
    }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        val view = origin.onCreateView(parent, name, context, attrs)
        replaceSkin(view = view, attrs = attrs)
        return view
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        val view = origin.onCreateView(name, context, attrs)
        replaceSkin(view = view, attrs = attrs)
        return view
    }

    private fun replaceSkin(view: View?, attrs: AttributeSet) {
        view ?: return
        val background = attrs.getAttributeResourceValue(ANDROID_NAME_SPACE, "background", 0)
        val redirectBackground = ResourceSkinManager.getInstance().redirectImageRes(background)
        if (redirectBackground != background) {
            view.setBackgroundResource(redirectBackground)
        }
        if (view is ImageView) {
            val src = attrs.getAttributeResourceValue(ANDROID_NAME_SPACE, "src", 0)
            val redirectSrc = ResourceSkinManager.getInstance().redirectImageRes(src)
            if (redirectSrc != src) {
                view.setImageResource(redirectSrc)
            }
        }
    }
}