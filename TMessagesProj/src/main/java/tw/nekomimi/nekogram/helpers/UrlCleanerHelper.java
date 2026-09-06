package tw.nekomimi.nekogram.helpers;

import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.nextalone.nagram.NaConfig;

public class UrlCleanerHelper {

    private static final Set<String> TRACKING_PARAMS = new HashSet<>(Arrays.asList(
            "utm_source", "utm_medium", "utm_campaign", "utm_term", "utm_content", "utm_id",
            "fbclid", "gclid", "dclid", "msclkid",
            "si", "igshid", "mc_eid", "ysclid", "_hsenc", "_hsmi", "mkt_tok"
    ));

    private static final List<String> TWITTER_DOMAINS = Arrays.asList("x.com", "twitter.com");
    private static final List<String> TWITTER_PREVIEW_DOMAINS = Arrays.asList("vxtwitter.com", "fxtwitter.com", "fixupx.com", "fixvx.com");
    private static final List<String> YOUTUBE_DOMAINS = Arrays.asList("youtube.com", "youtu.be", "www.youtube.com", "m.youtube.com");
    private static final List<String> TRACKING_QUERY_PREFIXES = Arrays.asList("utm");
    private static final List<String> TRACKING_QUERY_POSTFIXES = Arrays.asList("shid", "clid", "wtrid", "spm", "tracking_source",
            "__s", "__hssc", "__hstc", "hsCtaTracking", "msclkid", "oly_anon_id", "oly_enc_id", "rb_clickid", "s_clid", "wickedid");

    private static final Pattern URL_PATTERN = Pattern.compile("(https?://[^\\s]+)");

    private static boolean isTwitter(String host) {
        return host != null && TWITTER_DOMAINS.contains(host.toLowerCase());
    }

    private static boolean isTwitterPreview(String host) {
        return host != null && TWITTER_PREVIEW_DOMAINS.contains(host.toLowerCase());
    }

    private static boolean isYoutube(String host) {
        return host != null && YOUTUBE_DOMAINS.contains(host.toLowerCase());
    }

    public static String cleanUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        boolean patchEnabled = NaConfig.INSTANCE.getPatchAndCleanupLinks().Bool();
        boolean cleanEnabled = NaConfig.INSTANCE.getCleanTrackingParams().Bool();
        if (!patchEnabled && !cleanEnabled) {
            return url;
        }
        try {
            Uri uri = Uri.parse(url);
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                return url;
            }
            if (patchEnabled) {
                return cleanUrl(uri).toString();
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

    public static Uri cleanUrl(Uri src) {
        if (src == null) return null;
        String scheme = src.getScheme();
        if (scheme == null || !scheme.toLowerCase().startsWith("http")) return src;
        String host = src.getHost();
        if (host == null) host = "";
        if (isTwitter(host) || isTwitterPreview(host)) {
            String outHost = host;
            if (NaConfig.INSTANCE.getPatchAndCleanupLinks().Bool()) {
                outHost = "twitter.com";
            }
            return new Uri.Builder().scheme("https").authority(outHost).path(src.getPath()).build();
        } else if (isYoutube(host)) {
            Uri.Builder out = new Uri.Builder().scheme("https").authority(host).path(src.getPath());
            String t = src.getQueryParameter("t");
            if (t != null) {
                out.appendQueryParameter("t", t);
            }
            return out.build();
        }
        boolean appendPort = (src.getPort() >= 1 && src.getPort() <= 65535) &&
                ((scheme.equalsIgnoreCase("http") && src.getPort() != 80) ||
                 (scheme.equalsIgnoreCase("https") && src.getPort() != 443));
        Uri.Builder out = new Uri.Builder().scheme(scheme);
        if (appendPort) {
            out.encodedAuthority(host + ":" + src.getPort());
        } else {
            out.authority(host);
        }
        String path = src.getPath();
        if (path != null) {
            String stripped = path.startsWith("/") ? path.substring(1) : path;
            if (!stripped.isEmpty()) {
                out.appendEncodedPath(stripped);
            } else if (path.equals("/")) {
                out.path("/");
            }
        }
        String frag = src.getEncodedFragment();
        if (frag != null) out.encodedFragment(frag);
        Set<String> queries = src.getQueryParameterNames();
        for (String q : queries) {
            String lowerQ = q.toLowerCase();
            boolean block = false;
            for (String f : TRACKING_QUERY_PREFIXES) {
                if (lowerQ.startsWith(f.toLowerCase())) {
                    block = true;
                    break;
                }
            }
            if (block) continue;
            for (String f : TRACKING_QUERY_POSTFIXES) {
                if (lowerQ.startsWith(f.toLowerCase())) {
                    block = true;
                    break;
                }
            }
            if (block) continue;
            if (NaConfig.INSTANCE.getCustomGetQueryBlacklistData().contains(q)) continue;
            if (TRACKING_PARAMS.contains(lowerQ)) continue;
            for (String val : src.getQueryParameters(q)) {
                out.appendQueryParameter(q, val);
            }
        }
        return out.build();
    }

    public static String stripAllQueries(String src) {
        if (src == null) return null;
        int idx = src.lastIndexOf("?");
        if (idx < 0) return src;
        return src.substring(0, idx);
    }

    public static String cleanTextUrls(String text) {
        if (TextUtils.isEmpty(text) || !NaConfig.INSTANCE.getPatchAndCleanupLinks().Bool()) {
            return text;
        }
        try {
            Matcher m = URL_PATTERN.matcher(text);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String url = m.group(1);
                // Trim trailing punctuation that is not part of URL (like ),.,! )
                String suffix = "";
                while (!url.isEmpty() && (url.endsWith(".") || url.endsWith(",") || url.endsWith("!") || url.endsWith(")") || url.endsWith("]"))) {
                    suffix = url.substring(url.length() - 1) + suffix;
                    url = url.substring(0, url.length() - 1);
                }
                String cleaned = cleanUrl(url);
                m.appendReplacement(sb, Matcher.quoteReplacement(cleaned + suffix));
            }
            m.appendTail(sb);
            return sb.toString();
        } catch (Exception e) {
            return text;
        }
    }
}
