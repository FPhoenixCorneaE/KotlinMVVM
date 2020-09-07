package com.fphoenixcorneae.util

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import androidx.core.content.FileProvider
import androidx.loader.content.CursorLoader
import java.io.*

/**
 * Uri获取真正的Path工具类
 *
 * @date 2019-12-04 13:37
 */
object UriUtil {

    /**
     * File to uri.
     *
     * @param file The file.
     * @return uri
     */
    fun file2Uri(file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = "${AppUtil.packageName}.FileProvider"
            FileProvider.getUriForFile(ContextUtil.context, authority, file)
        } else {
            Uri.fromFile(file)
        }
    }

    /**
     * 复杂版处理  (适配多种API)
     *
     * @param context
     * @param uri
     * @return
     */
    @JvmStatic
    fun getRealPathFromUri(context: Context, uri: Uri): String? {
        val sdkVersion = Build.VERSION.SDK_INT
        return when {
            sdkVersion < Build.VERSION_CODES.HONEYCOMB -> {
                getRealPathFromUriBelowApi11(context, uri)
            }
            sdkVersion < Build.VERSION_CODES.KITKAT -> {
                getRealPathFromUriApi11To18(context, uri)
            }
            sdkVersion < Build.VERSION_CODES.N -> {
                getRealPathFromUriApi19To23(context, uri)
            }
            else -> {
                getRealPathFromUriAboveApi24(context, uri)
            }
        }
    }

    /**
     * 适配api24以上,根据uri获取图片的绝对路径
     */
    @TargetApi(Build.VERSION_CODES.N)
    private fun getRealPathFromUriAboveApi24(
        context: Context,
        uri: Uri
    ): String? {
        val rootDataDir = context.filesDir
        val fileName = getFileName(uri)
        if (!TextUtils.isEmpty(fileName)) {
            val copyFile =
                File(rootDataDir.toString() + File.separator + fileName)
            copyFile(context, uri, copyFile)
            return copyFile.absolutePath
        }
        return null
    }

    /**
     * 适配api19-api24,根据uri获取图片的绝对路径
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun getRealPathFromUriApi19To23(
        context: Context,
        uri: Uri
    ): String? {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                val contentUri: Uri
                contentUri = when (type) {
                    "image" -> {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    else -> {
                        MediaStore.Files.getContentUri("external")
                    }
                }
                val selection = "_id=?"
                val selectionArgs =
                    arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * 适配api11-api18,根据uri获取图片的绝对路径
     */
    private fun getRealPathFromUriApi11To18(
        context: Context,
        uri: Uri
    ): String? {
        var filePath: String? = null
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        //这个有两个包不知道是哪个。。。。不过这个复杂版一般用不到
        val loader =
            CursorLoader(context, uri, projection, null, null, null)
        val cursor = loader.loadInBackground()
        if (cursor != null) {
            cursor.moveToFirst()
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]))
            CloseUtil.closeIOQuietly(cursor)
        }
        return filePath
    }

    /**
     * 适配api11以下(不包括api11),根据uri获取图片的绝对路径
     */
    private fun getRealPathFromUriBelowApi11(
        context: Context,
        uri: Uri
    ): String? {
        var filePath: String? = null
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor =
            context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]))
            CloseUtil.closeIOQuietly(cursor)
        }
        return filePath
    }

    /**
     * 获取文件名
     */
    private fun getFileName(uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        var fileName: String? = null
        val path = uri.path
        if (path != null) {
            val cut = path.lastIndexOf('/')
            if (cut != -1) {
                fileName = path.substring(cut + 1)
            }
        }
        return fileName
    }

    /**
     * 复制文件
     */
    private fun copyFile(
        context: Context,
        srcUri: Uri,
        dstFile: File
    ) {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(srcUri)
            if (inputStream == null) {
                return
            }
            outputStream = FileOutputStream(dstFile)
            copyStream(inputStream, outputStream)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            CloseUtil.closeIOQuietly(inputStream, outputStream)
        }
    }

    /**
     * 复制文件流
     */
    private fun copyStream(input: InputStream, output: OutputStream): Int {
        val BUFFER_SIZE = 1024 * 2
        val buffer = ByteArray(BUFFER_SIZE)
        val `in` = BufferedInputStream(input, BUFFER_SIZE)
        val out = BufferedOutputStream(output, BUFFER_SIZE)
        var count = 0
        var n = 0
        try {
            while (`in`.read(buffer, 0, BUFFER_SIZE).also { n = it } != -1) {
                out.write(buffer, 0, n)
                count += n
            }
            out.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            CloseUtil.closeIOQuietly(out, `in`)
        }
        return count
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    @JvmStatic
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.MediaColumns.DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            CloseUtil.closeIOQuietly(cursor)
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    @JvmStatic
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    @JvmStatic
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    @JvmStatic
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}