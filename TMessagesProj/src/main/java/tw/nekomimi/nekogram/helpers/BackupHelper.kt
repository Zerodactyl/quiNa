package tw.nekomimi.nekogram.helpers

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONException
import org.json.JSONObject
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.LocaleController.getString
import org.telegram.messenger.R
import org.telegram.messenger.SendMessagesHelper
import org.telegram.ui.ActionBar.AlertDialog
import org.telegram.ui.ActionBar.BaseFragment
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.DocumentSelectActivity
import org.telegram.ui.LaunchActivity
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupHelper {

    const val FILE_TYPE_CG_BACKUP = 1390

    fun backupSettings(fragment: BaseFragment) {
        try {
            val formattedDate = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val fileName = "${formattedDate}-qna.cherry.json"
            val file = File(fragment.context.getExternalFilesDir(null), fileName)
            writeUtf8String(backupSettingsJson(fragment.context), file)
            shareFile(fragment.context, file)
        } catch (e: JSONException) {
            handleError(fragment.context, e)
        } catch (e: Exception) {
            handleError(fragment.context, e)
        }
    }

    fun importSettings(fragment: BaseFragment) {
        val importActivity = DocumentSelectActivity(false).apply {
            setMaxSelectedFiles(1)
            setAllowPhoto(false)
            setDelegate(object : DocumentSelectActivity.DocumentSelectActivityDelegate {
                override fun didSelectFiles(
                    activity: DocumentSelectActivity,
                    files: ArrayList<String>,
                    caption: String,
                    notify: Boolean,
                    scheduleDate: Int
                ) {
                    activity.finishFragment()
                    importSettings(File(files.first()), fragment.context)
                }

                override fun didSelectPhotos(
                    photos: ArrayList<SendMessagesHelper.SendingMediaInfo>,
                    notify: Boolean,
                    scheduleDate: Int
                ) {}

                override fun startDocumentSelectActivity() {}
            })
        }
        fragment.presentFragment(importActivity)
    }

    fun importSettings(file: File, context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle(getString(R.string.ImportSettings))
            setMessage(getString(R.string.ImportSettingsAlert))
            setNegativeButton(getString(R.string.Cancel), null)
            setPositiveButton(getString(R.string.OK)) { _, _ ->
                importSettingsConfirmed(file, context)
            }
            val dialog = show()
            try {
                val button = dialog.getButton(DialogInterface.BUTTON_POSITIVE) as TextView
                button.setTextColor(Theme.getColor(Theme.key_text_RedBold))
            } catch (_: Exception) {}
        }
    }

    private fun importSettingsConfirmed(file: File, context: Context) {
        try {
            val json = readJsonObjectWithGson(file)
            restoreSharedPreferences(json, context)
            val dialog = AlertDialog(context, 0)
            dialog.setTitle(getString(R.string.NekoX))
            dialog.setMessage(getString(R.string.RestartAppToTakeEffect))
            dialog.setPositiveButton(getString(R.string.OK)) { _, _ ->
                AppRestartHelper.triggerRebirth()
            }
            dialog.show()
        } catch (e: Exception) {
            handleError(context, e)
        }
    }

    private fun shareFile(context: Context, fileToShare: File, caption: String = "") {
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${ApplicationLoader.getApplicationId()}.provider", fileToShare)
        } else {
            Uri.fromFile(fileToShare)
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            if (caption.isNotBlank()) {
                putExtra(Intent.EXTRA_SUBJECT, caption)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            setClass(context, LaunchActivity::class.java)
        }
        context.startActivity(intent)
    }

    private fun writeUtf8String(text: String, file: File) {
        file.parentFile?.let { initDir(it) }
        file.writeText(text, Charsets.UTF_8)
    }

    private fun readJsonObjectWithGson(file: File): JsonObject {
        file.inputStream().buffered().use { inputStream ->
            InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                return JsonParser.parseReader(reader).asJsonObject
            }
        }
    }

    private fun restoreSharedPreferences(json: JsonObject, context: Context) {
        for ((spName, data) in json.entrySet()) {
            if (data.isJsonNull) continue
            val prefs = context.getSharedPreferences(spName, Activity.MODE_PRIVATE)
            val editor = prefs.edit()
            for ((keyRaw, valueElement) in data.asJsonObject.entrySet()) {
                var key = keyRaw
                if (!valueElement.isJsonPrimitive) continue
                val value = valueElement.asJsonPrimitive
                when {
                    value.isBoolean -> editor.putBoolean(key, value.asBoolean)
                    value.isNumber -> {
                        when {
                            key.endsWith("_long") -> {
                                key = key.removeSuffix("_long")
                                editor.putLong(key, value.asLong)
                            }
                            key.endsWith("_float") -> {
                                key = key.removeSuffix("_float")
                                editor.putFloat(key, value.asFloat)
                            }
                            else -> editor.putInt(key, value.asInt)
                        }
                    }
                    value.isString -> editor.putString(key, value.asString)
                }
            }
            editor.apply()
        }
    }

    private fun initDir(dir: File) {
        if (dir.exists() && dir.isFile) dir.delete()
        dir.mkdirs()
    }

    private fun handleError(context: Context, e: Exception) {
        try { AndroidUtilities.addToClipboard(e.toString()) } catch (_: Exception) {}
        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun backupSettingsJson(context: Context): String {
        val json = JSONObject()
        // 70-key selection mirroring Cherrygram but mapped to quiNa keys
        val keys = setOf(
            // General / appearance
            "AvatarAsBackground", "HidePhone", "ShowSeconds", "TransparentStatusBar",
            "TabTitleType", "HideAllTab", "OpenArchiveOnPull", "DisablePullDownSearch",
            "IgnoreMutedCount", "IgnoreFolderCount", "ShowUserIconsInChatsList",
            "TypefaceUseDefault", "DisableAppBarShadow", "MediaPreview",
            "HideAllTab", "AutoTranslate", "HideOriginAfterTranslation",
            // Chat
            "CopyPhoto", "CopyPhotoAsSticker", "ShowClearFromCache", "ShowForwardWithoutAuthor", "ShowViewJSON",
            "ShowMessageID", "ShowRPCError", "DateOfForwardedMsg", "HideTimeForSticker",
            "HideGreetingSticker", "ShowGreatOrPoor", "InvertReply", "ForceCopy",
            "NoQuoteForward", "DisableQuoteForward", "RepeatAsCopy", "CombineMessage",
            "DisableSwipeToNextChannel", "HideKeyboardOnChatScroll",
            "DisableNumberRounding", "CleanTrackingParams",
            "LargePhotos", "DisableBgParallax", "ChatDecoration", "IconDecoration",
            "DoNotUnarchiveBySwipe", "EnableSaveDeletedMessages", "EnableSaveEditsHistory",
            "SaveToChatSubfolder",
            // Privacy / biometric
            "AskBiometricsToOpenChats", "AskBiometricsToOpenEncrypted", "AskBiometricsToOpenArchive",
            "AskPasscodeBeforeDelete", "AllowSystemPasscode", "HideArchiveFromChatsList",
            "DisableSystemAccount", "DoNotShareMyPhoneNumber", "DisableFlagSecure",
            "SentryAnalytics", "UseSystemDNS", "DisableProxyWhenVpnEnabled",
            // Translate / LLM
            "TranslationProvider", "TransToLang", "DeepLxCustomApi", "DeepLApiKey",
            "LLMProvider", "LLMApiKeys",
            // Material 3
            "M3ExpressiveAll", "M3SectionCards", "M3GlassMenu", "M3TactileHaptics",
            // Misc
            "RegexFilters", "RegexFiltersData", "DefaultHlsVideoQuality"
        )
        spToJSON("nkmrcfg", json, keys, context)
        spToJSON("nkmrcfg_default", json, keys, context)
        spToJSON("mainconfig", json, keys, context)
        spToJSON("mainconfig0", json, keys, context)
        return json.toString(4)
    }

    private fun spToJSON(name: String, target: JSONObject, keys: Set<String>, context: Context) {
        val prefs = context.getSharedPreferences(name, Activity.MODE_PRIVATE)
        val jsonPrefs = JSONObject()
        var hasAny = false
        for ((keyRaw, value) in prefs.all) {
            var key = keyRaw
            if (key !in keys) continue
            hasAny = true
            when (value) {
                is Long -> key += "_long"
                is Float -> key += "_float"
            }
            jsonPrefs.put(key, value)
        }
        if (hasAny) target.put(name, jsonPrefs)
    }
}
