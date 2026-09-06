package tw.nekomimi.nekogram.helpers;

import android.text.TextUtils;

import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteException;
import org.telegram.messenger.BaseController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;

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
}
