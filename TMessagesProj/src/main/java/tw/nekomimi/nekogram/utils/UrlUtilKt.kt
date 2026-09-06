package tw.nekomimi.nekogram.utils

import android.net.Uri
import tw.nekomimi.nekogram.helpers.UrlCleanerHelper

object UrlUtilKt {
    @JvmStatic
    fun cleanUrl(src: String): String {
        return UrlCleanerHelper.cleanUrl(src)
    }

    @JvmStatic
    fun cleanUrl(src: Uri): Uri {
        return UrlCleanerHelper.cleanUrl(src)
    }

    @JvmStatic
    fun stripAllQueries(src: String): String {
        return UrlCleanerHelper.stripAllQueries(src)
    }

    @JvmStatic
    fun cleanTextUrls(text: String): String {
        return UrlCleanerHelper.cleanTextUrls(text)
    }
}
