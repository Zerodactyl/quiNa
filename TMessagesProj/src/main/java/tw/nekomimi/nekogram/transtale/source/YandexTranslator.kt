package tw.nekomimi.nekogram.transtale.source

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.telegram.messenger.FileLog
import tw.nekomimi.nekogram.transtale.Translator
import java.util.UUID
import java.util.concurrent.TimeUnit

object YandexTranslator : Translator {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val uuid = UUID.randomUUID().toString().replace("-", "")

    override suspend fun doTranslate(from: String, to: String, query: String): String {
        val uuid2 = UUID.randomUUID().toString().replace("-", "")

        val formBody = FormBody.Builder()
            .add("text", query)
            .add("lang", if (from == "auto") to else "$from-$to")
            .build()

        val req = Request.Builder()
            .url("https://translate.yandex.net/api/v1/tr.json/translate?srv=android&uuid=$uuid&id=$uuid2-9-0")
            .header("User-Agent", "Mozilla/5.0 (Android; Mobile; rv:109.0) Gecko/114.0 Firefox/114.0")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .post(formBody)
            .build()

        val response = httpClient.newCall(req).execute()

        if (!response.isSuccessful) {
            val err = "HTTP ${response.code} : ${response.body?.string()}"
            FileLog.e("Yandex translator error: $err, query: $query")
            error(err)
        }

        val respObj = JSONObject(response.body?.string() ?: "")

        if (respObj.optInt("code", -1) != 200) {
            error(respObj.toString(4))
        }

        return respObj.getJSONArray("text").getString(0)
    }
}
