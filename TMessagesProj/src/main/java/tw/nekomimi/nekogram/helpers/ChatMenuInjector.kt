package tw.nekomimi.nekogram.helpers

import org.telegram.messenger.LocaleController
import org.telegram.messenger.MessageObject
import org.telegram.messenger.R
import org.telegram.ui.ChatActivity
import tw.nekomimi.nekogram.ui.MessageDetailsActivity
import xyz.nextalone.nagram.NaConfig

object ChatMenuInjector {

    const val OPTION_CLEAR_FROM_CACHE = 152
    const val OPTION_FORWARD_WO_AUTHOR = 153
    const val OPTION_VIEW_JSON = 154

    @JvmStatic
    fun injectClearFromCache(
        items: ArrayList<CharSequence?>,
        options: ArrayList<Int?>,
        icons: ArrayList<Int?>,
        selectedObject: MessageObject?
    ) {
        if (selectedObject == null) return
        if (!NaConfig.showClearFromCache.Bool()) return
        if (selectedObject.type == MessageObject.TYPE_TEXT && selectedObject.caption == null) return
        items.add(LocaleController.getString(R.string.ClearFromCache))
        options.add(OPTION_CLEAR_FROM_CACHE)
        icons.add(R.drawable.msg_clear)
    }

    @JvmStatic
    fun injectForwardWoAuthor(
        selectedObject: MessageObject?,
        chatMode: Int,
        items: ArrayList<CharSequence?>,
        options: ArrayList<Int?>,
        icons: ArrayList<Int?>
    ) {
        if (selectedObject == null) return
        if (!NaConfig.showForwardWithoutAuthor.Bool()) return
        if (selectedObject.isSponsored) return
        if (chatMode == ChatActivity.MODE_QUICK_REPLIES || chatMode == ChatActivity.MODE_SCHEDULED) return
        if (selectedObject.isLiveLocation) return
        if (selectedObject.type == MessageObject.TYPE_PHONE_CALL) return
        if (selectedObject.isWallpaperAction) return
        items.add(LocaleController.getString(R.string.Forward) + " " + LocaleController.getString(R.string.WithoutAuthor))
        options.add(OPTION_FORWARD_WO_AUTHOR)
        icons.add(R.drawable.msg_forward)
    }

    @JvmStatic
    fun injectViewJSON(
        chatActivity: ChatActivity?,
        force: Boolean,
        items: ArrayList<CharSequence?>,
        options: ArrayList<Int?>,
        icons: ArrayList<Int?>
    ) {
        val show = force || (chatActivity != null && NaConfig.showViewJSON.Bool())
        if (show) {
            items.add("JSON")
            options.add(OPTION_VIEW_JSON)
            icons.add(R.drawable.msg_info)
        }
    }

    @JvmStatic
    fun handleOption(chatActivity: ChatActivity, option: Int, selectedObject: MessageObject?): Boolean {
        when (option) {
            OPTION_CLEAR_FROM_CACHE -> {
                var cleared = false
                try {
                    val path = selectedObject?.messageOwner?.attachPath
                    var filePath = path
                    if (filePath.isNullOrEmpty()) {
                        val f = org.telegram.messenger.FileLoader.getInstance(chatActivity.currentAccount).getPathToMessage(selectedObject?.messageOwner)
                        filePath = f?.path
                    }
                    if (!filePath.isNullOrEmpty()) {
                        val file = java.io.File(filePath)
                        val cacheDir = org.telegram.messenger.FileLoader.getDirectory(org.telegram.messenger.FileLoader.MEDIA_DIR_CACHE)?.canonicalPath
                        if (cacheDir != null && file.canonicalPath.startsWith(cacheDir + java.io.File.separator) && file.exists()) {
                            cleared = file.delete()
                            if (cleared && selectedObject != null) selectedObject.mediaExists = false
                        } else {
                            org.telegram.messenger.FileLog.d("ClearFromCache skipped non-cache path")
                        }
                    }
                    val success = cleared
                    org.telegram.messenger.AndroidUtilities.runOnUIThread {
                        try {
                            val bulletin = org.telegram.ui.Components.BulletinFactory.of(chatActivity)
                            if (success) {
                                bulletin.createSimpleBulletin(R.raw.done, LocaleController.getString(R.string.CacheCleared)).show()
                            } else {
                                bulletin.createErrorBulletin(LocaleController.getString(R.string.CacheClearFailed)).show()
                            }
                        } catch (_: Exception) {}
                    }
                } catch (e: Exception) {
                    org.telegram.messenger.FileLog.e(e)
                }
                return true
            }
            OPTION_FORWARD_WO_AUTHOR -> {
                if (selectedObject == null) return true
                ChatActivity.noForwardQuote = true
                chatActivity.messagePreviewParams?.setHideForwardSendersName(true)
                chatActivity.forwardingMessage = selectedObject
                chatActivity.forwardingMessageGroup = null
                val args = android.os.Bundle()
                args.putBoolean("onlySelect", true)
                args.putInt("dialogsType", org.telegram.ui.DialogsActivity.DIALOGS_TYPE_FORWARD)
                args.putInt("messagesCount", 1)
                args.putBoolean("forward_noquote", true)
                val fragment = org.telegram.ui.DialogsActivity(args)
                fragment.setDelegate(chatActivity)
                chatActivity.presentFragment(fragment)
                return true
            }
            OPTION_VIEW_JSON -> {
                if (selectedObject != null) {
                    try {
                        chatActivity.presentFragment(MessageDetailsActivity(selectedObject))
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(chatActivity.context, "JSON: ${selectedObject.messageOwner?.message}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }
        }
        return false
    }
}
