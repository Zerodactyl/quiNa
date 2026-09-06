package tw.nekomimi.nekogram.helpers;

import android.content.SharedPreferences;

import androidx.core.content.edit;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.telegram.messenger.BaseController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FingerprintController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.HashSet;

import xyz.nextalone.nagram.NaConfig;

/**
 * Port of Cherrygram ChatsPasswordHelper.
 * Persisted in MessagesController.mainSettings as JSON via Gson.
 */
public class ChatsPasswordHelper extends BaseController {

    private static final ChatsPasswordHelper[] instances = new ChatsPasswordHelper[UserConfig.MAX_ACCOUNT_COUNT];
    private HashSet<String> lockedChatsCache = null;

    private static final char[] spoilerChars = new char[]{'⠌', '⡢', '⢑', '⠨', '⠥', '⠮', '⡑'};

    public ChatsPasswordHelper(int num) {
        super(num);
    }

    public static ChatsPasswordHelper getInstance(int num) {
        ChatsPasswordHelper local = instances[num];
        if (local == null) {
            synchronized (ChatsPasswordHelper.class) {
                local = instances[num];
                if (local == null) {
                    local = new ChatsPasswordHelper(num);
                    instances[num] = local;
                }
            }
        }
        return local;
    }

    public String getPasscodeArray() {
        return "locked_chats_list";
    }

    public void saveArrayList(ArrayList<String> list, String key) {
        if (key.equals(getPasscodeArray())) {
            lockedChatsCache = new HashSet<>(list);
        }
        SharedPreferences prefs = getMessagesController().getMainSettings();
        prefs.edit(edit -> edit.putString(key, new Gson().toJson(list)));
    }

    public ArrayList<String> getArrayList(String key) {
        if (key.equals(getPasscodeArray()) && lockedChatsCache != null) {
            return new ArrayList<>(lockedChatsCache);
        }
        SharedPreferences prefs = getMessagesController().getMainSettings();
        String json = prefs.getString(key, null);
        ArrayList<String> list = null;
        try {
            if (json != null) {
                list = new Gson().fromJson(json, new TypeToken<ArrayList<String>>() {}.getType());
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (list == null) {
            list = new ArrayList<>();
            // do not seed with clientUserId; keep empty
        }
        if (key.equals(getPasscodeArray())) {
            lockedChatsCache = new HashSet<>(list);
        }
        return list;
    }

    public boolean isChatLocked(long chatId) {
        if (chatId == 0L || !NaConfig.INSTANCE.getAskBiometricsToOpenChats().Bool()) return false;
        if (lockedChatsCache == null) {
            getArrayList(getPasscodeArray());
        }
        String idStr = String.valueOf(chatId);
        return lockedChatsCache != null && (lockedChatsCache.contains(idStr) || lockedChatsCache.contains("-" + idStr));
    }

    public boolean isChatLocked(MessageObject messageObject) {
        if (messageObject == null || messageObject.messageOwner == null || messageObject.messageOwner.message == null) return false;
        if (messageObject.isStoryReactionPush || messageObject.isStoryPush || messageObject.isStoryMentionPush || messageObject.isStoryPushHidden) return false;
        return NaConfig.INSTANCE.getAskBiometricsToOpenChats().Bool() && isChatLocked(messageObject.getChatId());
    }

    public boolean isEncryptedChat(long chatId) {
        if (!NaConfig.INSTANCE.getAskBiometricsToOpenEncrypted().Bool()) return false;
        int encId = DialogObject.getEncryptedChatId(chatId);
        return getMessagesController().getEncryptedChat(encId) != null;
    }

    public boolean isEncryptedChat(MessageObject messageObject) {
        if (!NaConfig.INSTANCE.getAskBiometricsToOpenEncrypted().Bool()) return false;
        if (messageObject == null || messageObject.messageOwner == null || messageObject.messageOwner.message == null) return false;
        if (messageObject.isStoryReactionPush || messageObject.isStoryPush || messageObject.isStoryMentionPush || messageObject.isStoryPushHidden) return false;
        int encId = DialogObject.getEncryptedChatId(messageObject.getDialogId());
        return getMessagesController().getEncryptedChat(encId) != null;
    }

    public ArrayList<TLRPC.MessageEntity> checkLockedChatsEntities(MessageObject messageObject) {
        return checkLockedChatsEntities(messageObject, messageObject.messageOwner.entities);
    }

    public ArrayList<TLRPC.MessageEntity> checkLockedChatsEntities(MessageObject messageObject, ArrayList<TLRPC.MessageEntity> original) {
        if (isChatLocked(messageObject) || isEncryptedChat(messageObject)) {
            ArrayList<TLRPC.MessageEntity> entities = original != null ? new ArrayList<>(original) : new ArrayList<>();
            TLRPC.TL_messageEntitySpoiler spoiler = new TLRPC.TL_messageEntitySpoiler();
            spoiler.offset = 0;
            spoiler.length = messageObject.messageOwner.message != null ? messageObject.messageOwner.message.length() : 0;
            entities.add(spoiler);
            return entities;
        } else {
            return original;
        }
    }

    public String replaceStringToSpoilers(String originalText, boolean force) {
        if (originalText == null) return null;
        if (NaConfig.INSTANCE.getAskBiometricsToOpenArchive().Bool() || force) {
            StringBuilder sb = new StringBuilder(originalText);
            for (int i = 0; i < originalText.length(); i++) {
                sb.setCharAt(i, spoilerChars[i % spoilerChars.length]);
            }
            return sb.toString();
        } else {
            return originalText;
        }
    }

    public int getLockedChatsCount() {
        return getArrayList(getPasscodeArray()).size();
    }

    public boolean shouldRequireBiometrics(long userID, long chatID, int encID) {
        boolean lockedChat = (userID != 0L && isChatLocked(userID)) || (chatID != 0L && isChatLocked(chatID));
        boolean encryptedChat = encID != 0 && isEncryptedChat(encID);
        return (lockedChat && shouldRequireBiometricsToOpenChats()) || (encryptedChat && shouldRequireBiometricsToOpenEncryptedChats());
    }

    public boolean shouldRequireBiometricsToOpenChats() {
        return NaConfig.INSTANCE.getAskBiometricsToOpenChats().Bool() && checkBiometricAvailable();
    }

    public boolean shouldRequireBiometricsToOpenEncryptedChats() {
        return NaConfig.INSTANCE.getAskBiometricsToOpenEncrypted().Bool() && checkBiometricAvailable();
    }

    public boolean askPasscodeBeforeDelete() {
        return NaConfig.INSTANCE.getAskPasscodeBeforeDelete().Bool() && checkBiometricAvailable();
    }

    public boolean checkBiometricAvailable() {
        boolean hasBiometrics = BiometricHelper.hasBiometricEnrolled();
        if (!hasBiometrics) return false;
        boolean hasFingerprints = BiometricHelper.hasEnrolledFingerprints();
        if (hasFingerprints) {
            try {
                return FingerprintController.isKeyReady() && !FingerprintController.checkDeviceFingerprintsChanged();
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        } else {
            return true;
        }
    }
}
