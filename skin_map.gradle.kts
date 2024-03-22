import java.io.BufferedWriter
import java.io.FileWriter


fun compareFileNames(res: File, skin: File): Map<String, String> {
    val resFiles = res.listFiles()?.map { it.nameWithoutExtension } ?: emptyList()
    val skinFiles = skin.listFiles()?.map { it.nameWithoutExtension } ?: emptyList()
    val commonFiles = mutableMapOf<String, String>()
    resFiles.forEach { resFile ->
        val index = skinFiles.indexOf("skin_${resFile}")
        if (index >= 0) {
            commonFiles[resFile] = skinFiles[index]
        }
    }
    return commonFiles
}

fun writerJava(map: Map<String, String>) {
    val javaFile = File("app/src/main/java/com/widget/uiskin/SkinMap.java")
    val writer = FileWriter(javaFile)
    val bufferedWriter = BufferedWriter(writer)
    bufferedWriter.write("package com.widget.uiskin;")
    bufferedWriter.newLine()
    bufferedWriter.write("import java.util.HashMap;")
    bufferedWriter.newLine()
    bufferedWriter.write("import java.util.Map;")
    bufferedWriter.newLine()
    bufferedWriter.newLine()
    bufferedWriter.write("public class SkinMap {")
    bufferedWriter.newLine()
    bufferedWriter.newLine()
    bufferedWriter.write("    public final Map<Integer, Integer> skinMap = new HashMap<>();")
    bufferedWriter.newLine()
    bufferedWriter.newLine()
    bufferedWriter.write("    public SkinMap() {")
    bufferedWriter.newLine()
    map.forEach { (key, value) ->
        val code = "        skinMap.put(R.drawable.${key},R.drawable.${value});"
        bufferedWriter.write(code)
        bufferedWriter.newLine()
    }
    bufferedWriter.write("    }")
    bufferedWriter.newLine()
    bufferedWriter.newLine()
    bufferedWriter.write("}")
    bufferedWriter.newLine()
    bufferedWriter.close()

    map.forEach { (key, value) ->
        print("skin_replace key:$key,value:$value\n")
    }
}

fun excute() {
    val xxhdpi = compareFileNames(File("app/src/main/res/drawable-xxhdpi"), File("app/src/main/skin/drawable-xxhdpi"))
    val hdpi = compareFileNames(File("app/src/main/res/drawable"), File("app/src/main/skin/drawable"))
    val map = mutableMapOf<String, String>().also {
        it.putAll(xxhdpi)
        it.putAll(hdpi)
    }
    writerJava(map)
}
excute()




