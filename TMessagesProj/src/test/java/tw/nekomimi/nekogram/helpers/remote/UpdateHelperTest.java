package tw.nekomimi.nekogram.helpers.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;

import java.lang.reflect.Method;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(application = android.app.Application.class)
public class UpdateHelperTest {
    private UpdateHelper helper;
    private Method selectVersion;

    @Before
    public void setUp() throws Exception {
        ApplicationLoader.applicationContext = ApplicationProvider.getApplicationContext();
        ReflectionHelpers.setStaticField(Build.class, "SUPPORTED_ABIS", new String[]{"arm64-v8a", "armeabi-v7a"});
        helper = new UpdateHelper();
        selectVersion = UpdateHelper.class.getDeclaredMethod("getShouldUpdateVersion", List.class);
        selectVersion.setAccessible(true);
    }

    @Test
    public void acceptsArm64OnlyMapEvenWhenFirstAbiIsMissing() throws Exception {
        var update = select(payload(new JSONObject().put("arm64-v8a", 42)));
        assertNotNull(update);
        assertEquals(Integer.valueOf(42), update.gcm.get("arm64-v8a"));
        assertFalse(update.gcm.containsKey("armeabi-v7a"));
    }

    @Test
    public void ignoresMalformedAbiWithoutDiscardingValidAbi() throws Exception {
        var update = select(payload(new JSONObject().put("armeabi-v7a", "bad").put("arm64-v8a", 42)));
        assertNotNull(update);
        assertEquals(Integer.valueOf(42), update.gcm.get("arm64-v8a"));
    }

    @Test
    public void skipsEmptyAndInvalidMapsBeforeValidPayload() throws Exception {
        var update = select(
                payload(new JSONObject()),
                payload(new JSONObject().put("arm64-v8a", -1).put("armeabi-v7a", 0)),
                payload(new JSONObject().put("armeabi-v7a", 43)));
        assertNotNull(update);
        assertEquals(Integer.valueOf(43), update.gcm.get("armeabi-v7a"));
    }

    @Test
    public void incompatibleApkDoesNotBlockLaterCompatiblePayload() throws Exception {
        ReflectionHelpers.setStaticField(Build.class, "SUPPORTED_ABIS", new String[]{"armeabi-v7a"});
        var update = select(
                payload(new JSONObject().put("arm64-v8a", 42)),
                payload(new JSONObject().put("armeabi-v7a", 43)));
        assertEquals(Integer.valueOf(43), update.gcm.get("armeabi-v7a"));
    }

    @Test
    public void allMalformedMapsReturnNoUpdate() throws Exception {
        assertNull(select(payload(new JSONObject()), payload(new JSONObject().put("arm64-v8a", JSONObject.NULL))));
    }

    @Test
    public void forcedUpdateSurvivesMalformedPayload() throws Exception {
        ReflectionHelpers.setField(helper, "updateAlways", true);
        var malformed = payload(new JSONObject().put("arm64-v8a", 42));
        malformed.remove("version");
        var valid = payload(new JSONObject().put("arm64-v8a", 43))
                .put("version_code", BuildConfig.VERSION_CODE)
                .put("timestamp", BuildConfig.BUILD_TIMESTAMP);
        assertNotNull(select(malformed, valid));
        assertFalse(ReflectionHelpers.getField(helper, "updateAlways"));
    }

    @Test
    public void errorCompletesDelegateAndAllowsBackgroundLoad() {
        String[] result = new String[1];
        helper.onError("resolution failed", (update, error) -> {
            assertNull(update);
            result[0] = error;
        });
        assertEquals("resolution failed", result[0]);
        helper.onError("resolution failed", null);
    }

    private UpdateHelper.Update select(JSONObject... payloads) throws Exception {
        return (UpdateHelper.Update) selectVersion.invoke(helper, List.of(payloads));
    }

    private JSONObject payload(JSONObject files) throws Exception {
        return new JSONObject()
                .put("version_code", BuildConfig.VERSION_CODE + 1)
                .put("timestamp", BuildConfig.BUILD_TIMESTAMP + 1)
                .put("can_not_skip", false)
                .put("version", "test")
                .put("sticker", 1)
                .put("message", 2)
                .put("gcm", files)
                .put("url", "https://example.org/update");
    }
}
