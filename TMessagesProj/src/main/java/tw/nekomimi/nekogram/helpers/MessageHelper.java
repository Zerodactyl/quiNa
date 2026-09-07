package tw.nekomimi.nekogram.helpers;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import androidx.core.content.FileProvider;

import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteException;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BaseController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

public class MessageHelper extends BaseController {

    private static final MessageHelper[] Instance = new MessageHelper[UserConfig.MAX_ACCOUNT_COUNT];

    public MessageHelper(int num) {
        super(num);
    }

    public static MessageHelper getInstance(int num) {
        MessageHelper localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (MessageHelper.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    Instance[num] = localInstance = new MessageHelper(num);
                }
            }
        }
        return localInstance;
    }

    public TLRPC.Message getMessage(long dialogId, long msgId) {
        TLRPC.Message message = null;
        SQLiteCursor cursor = null;
        try {
            cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT data FROM messages_v2 WHERE uid = " + dialogId + " AND mid = " + msgId + " LIMIT 1");
            while (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                    if (message != null) {
                        message.readAttachPath(data, UserConfig.getInstance(currentAccount).clientUserId);
                    }
                    data.reuse();
                }
            }
            cursor.dispose();
            cursor = null;
        } catch (Exception e) {
            FileLog.e(e);
        } finally {
            if (cursor != null) {
                cursor.dispose();
            }
        }
        return message;
    }

    public ArrayList<TLRPC.Message> getMessagesStorageMessages(long dialogId, ArrayList<Integer> messageIds) {
        ArrayList<TLRPC.Message> messages = null;
        SQLiteCursor cursor = null;
        try {
            String ids = TextUtils.join(",", messageIds);
            cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE uid = %d AND mid IN (%s)", dialogId, ids));
            while (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                    if (message != null) {
                        message.readAttachPath(data, UserConfig.getInstance(currentAccount).clientUserId);
                        if (messages == null) {
                            messages = new ArrayList<>();
                        }
                        messages.add(message);
                    }
                    data.reuse();
                }
            }
            cursor.dispose();
            cursor = null;
        } catch (SQLiteException e) {
            FileLog.e(e);
        } finally {
            if (cursor != null) {
                cursor.dispose();
            }
        }
        return messages;
    }

    public static String getDCLocation(int dc) {
        switch (dc) {
            case 1:
            case 3:
                return "Miami";
            case 2:
            case 4:
                return "Amsterdam";
            case 5:
                return "Singapore";
            default:
                return "Unknown";
        }
    }

    public static String getDCName(int dc) {
        switch (dc) {
            case 1:
                return "Pluto";
            case 2:
                return "Venus";
            case 3:
                return "Aurora";
            case 4:
                return "Vesta";
            case 5:
                return "Flora";
            default:
                return "Unknown";
        }
    }

    private static final CharsetDecoder utf8Decoder = StandardCharsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT);

    public static String getTextOrBase64(byte[] data) {
        try {
            return utf8Decoder.decode(ByteBuffer.wrap(data)).toString();
        } catch (CharacterCodingException e) {
            return Base64.encodeToString(data, Base64.NO_PADDING | Base64.NO_WRAP);
        }
    }

    public static String getPathToMessage(MessageObject messageObject) {
        if (messageObject == null || messageObject.messageOwner == null) return null;
        if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
            File f = new File(messageObject.messageOwner.attachPath);
            if (f.exists()) return f.getAbsolutePath();
        }
        File f = FileLoader.getInstance(messageObject.currentAccount).getPathToMessage(messageObject.messageOwner);
        if (f != null && f.exists()) return f.getAbsolutePath();
        return null;
    }

    public static void addFileToClipboard(File file, Runnable callback) {
        try {
            Context context = ApplicationLoader.applicationContext;
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            Uri uri = FileProvider.getUriForFile(context, ApplicationLoader.getApplicationId() + ".provider", file);
            ClipData clip = ClipData.newUri(context.getContentResolver(), "label", uri);
            clipboard.setPrimaryClip(clip);
            if (callback != null) callback.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void addMessageToClipboard(MessageObject selectedObject, Runnable callback) {
        String path = getPathToMessage(selectedObject);
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                addFileToClipboard(file, callback);
            }
        }
    }

    public static void addMessageToClipboardAsSticker(MessageObject selectedObject, Runnable callback) {
        String path = getPathToMessage(selectedObject);
        try {
            if (!TextUtils.isEmpty(path)) {
                Bitmap image = BitmapFactory.decodeFile(path);
                if (image != null) {
                    File file2 = path.endsWith(".jpg") ? new File(path.replace(".jpg", ".webp")) : new File(path + ".webp");
                    try (FileOutputStream stream = new FileOutputStream(file2)) {
                        if (Build.VERSION.SDK_INT >= 30) {
                            image.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, stream);
                        } else {
                            image.compress(Bitmap.CompressFormat.WEBP, 100, stream);
                        }
                    } finally {
                        image.recycle();
                    }
                    addFileToClipboard(file2, callback);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static boolean shouldKeepOriginalForManualTranslation(int translatorMode) {
        return false;
    }

    public static boolean shouldKeepOriginalForDisplay(int translatorMode, boolean manualTranslated, boolean autoTranslated) {
        return false;
    }

    public static String buildTranslatedDisplayText(CharSequence originalText, TLRPC.TL_textWithEntities translatedText, boolean keepOriginal) {
        return buildTranslatedDisplayText(originalText, translatedText != null ? translatedText.text : null, keepOriginal);
    }

    public static String buildTranslatedDisplayText(CharSequence originalText, String translatedText, boolean keepOriginal) {
        if (TextUtils.isEmpty(translatedText)) {
            return originalText == null ? "" : originalText.toString();
        }
        if (!keepOriginal || TextUtils.isEmpty(originalText)) {
            return translatedText;
        }
        return originalText + "\n\n" + translatedText;
    }
}
