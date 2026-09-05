package tw.nekomimi.nekogram.helpers;

import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import xyz.nextalone.nagram.NaConfig;

public class UrlCleanerHelper {

    private static final Set<String> TRACKING_PARAMS = new HashSet<>(Arrays.asList(
            "utm_source", "utm_medium", "utm_campaign", "utm_term", "utm_content", "utm_id",
            "fbclid", "gclid", "dclid", "msclkid",
            "si", "igshid", "mc_eid", "ysclid", "_hsenc", "_hsmi", "mkt_tok"
    ));

    public static String cleanUrl(String url) {
        if (TextUtils.isEmpty(url) || !NaConfig.INSTANCE.getCleanTrackingParams().Bool()) {
            return url;
        }
        try {
            Uri uri = Uri.parse(url);
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                return url;
            }
            if (uri.getQueryParameterNames().isEmpty()) {
                return url;
            }
            boolean modified = false;
            Uri.Builder builder = uri.buildUpon().clearQuery();
            for (String param : uri.getQueryParameterNames()) {
                if (TRACKING_PARAMS.contains(param.toLowerCase())) {
                    modified = true;
                } else {
                    for (String val : uri.getQueryParameters(param)) {
                        builder.appendQueryParameter(param, val);
                    }
                }
            }
            return modified ? builder.build().toString() : url;
        } catch (Exception e) {
            return url;
        }
    }
}
