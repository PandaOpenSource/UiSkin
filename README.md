# UiSkin 换肤方案

## 使用背景

公司的APP在首次上线GP被拒后，需要替换一套皮肤进行再次审核（可以理解成是马甲包的一小块内容）。为了审核安全和时间成本考虑，不使用网上的皮肤包方案。
同时，在上线成功后，需要支持通过后台切换到最初的皮肤（IOS已经迭代了几个版本，所以需要统一移动端皮肤）

## 技术原理

### 第一步 统计皮肤资源的映射关系

在使用皮肤之前，我们需要收集皮肤资源中哪些配置了皮肤。为了规范起见，我们将皮肤资源起一个新的文件夹为skin，并且该文件夹下名称需要加上skin_开头，（如默认的皮肤资源名称是：R.drawable.icon_simple,那皮肤包对应资源名称为：R.drawable.skin_icon_simple）
再这个文件夹引入到项目的资源中

~~~ groovy
android{
    sourceSets {
        getByName("main").res.srcDirs("src/main/skin")
    }
}
~~~

再使用脚本生成一个java文件，用于存储皮肤之间的映射表

~~~ groovy
apply(from = "../skin_map.gradle.kts")
~~~

生成的java文件长这样

~~~ java
public class SkinMap {

    public final Map<Integer, Integer> skinMap = new HashMap<>();

    public SkinMap() {
        skinMap.put(R.drawable.ic_launcher,R.drawable.skin_ic_launcher);
        skinMap.put(R.drawable.background_round,R.drawable.skin_background_round);
    }
}
~~~

### 第二步 Hook R.drawable R.color

因为 R.drawable 和R.color只是一个常量池，所以当我们读取到需要配置皮肤时，只需要Hook住其中的原始皮肤，将他的值改成皮肤的值即可（映射关系在SkinMap保存）。
在setBackground(R.drawable.simple)的时候，他的值实际上已经被改成R.drawable.skin_simple了。setColor的原理也是一样。

### 第三部 Hook LayoutInflater.factory

Hook R.drawable R.color 完全解决了动态设置问题，但是还有大部分是写在layout文件中，这些文件会生效。原因是因为在编译的时候，layout的属性是写死的，不是在创建的时候去访问R文件获取。所以需要Hook LayoutInflater.factory，在onCreateView的时候通过AttributeSet去替换其中的资源。

~~~ kotlin
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
~~~

## 联系与支持

如在使用本本开源库过程中遇到任何问题或建议，欢迎通过以下方式联系我们：

邮箱：[jixiongxu2017@gmail.com]
我们将竭诚为您提供帮助和支持。

本开源库仅仅用做学习使用，不适用通用开源项目
