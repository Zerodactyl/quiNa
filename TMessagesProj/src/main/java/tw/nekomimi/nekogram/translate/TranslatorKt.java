package tw.nekomimi.nekogram.translate;

import android.text.TextUtils;
import java.util.Locale;

public class TranslatorKt {
    public static Locale getCode2Locale(String code) {
        if (TextUtils.isEmpty(code)) return Locale.getDefault();
        try { return new Locale(code); } catch (Exception e) { return Locale.getDefault(); }
    }
    public static String getLocale2code(Locale locale) {
        if (locale == null) return "en";
        return locale.getLanguage();
    }
}
