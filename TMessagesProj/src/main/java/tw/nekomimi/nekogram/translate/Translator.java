package tw.nekomimi.nekogram.translate;

import androidx.annotation.NonNull;
import org.telegram.messenger.TranslateController;
import org.telegram.tgnet.TLRPC;
import java.util.ArrayList;
import java.util.Locale;

public class Translator {
    public static class Companion {
        public interface TranslateCallBack2 {
            void onSuccess(@NonNull TLRPC.TL_textWithEntities finalText);
            void onFailed(boolean unsupported, @NonNull String message);
        }
        public interface TranslateCallBack3 {
            void onSuccess(@NonNull TranslateController.PollText poll);
            void onFailed(boolean unsupported, @NonNull String message);
        }
    }
    public static void translate(Locale targetLocale, String originalText, ArrayList<TLRPC.MessageEntity> entities, Companion.TranslateCallBack2 callback) {
        if (callback != null) callback.onFailed(true, "Translation not available");
    }
    public static void translatePoll(Locale targetLocale, TranslateController.PollText pollText, Companion.TranslateCallBack3 callback) {
        if (callback != null) callback.onFailed(true, "Translation not available");
    }
    public static void translatePoll(Locale targetLocale, String pollText, Companion.TranslateCallBack3 callback) {
        if (callback != null) callback.onFailed(true, "Translation not available");
    }
    public static void showTargetLangSelect(Object cell, boolean b1, boolean b2, Object callback) {
    }
}
