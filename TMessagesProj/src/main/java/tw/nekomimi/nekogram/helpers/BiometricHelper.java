package tw.nekomimi.nekogram.helpers;

import android.app.Activity;
import android.hardware.biometrics.BiometricManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FingerprintController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

import xyz.nextalone.nagram.NaConfig;

/**
 * Minimal port of Cherrygram CGBiometricPrompt.
 * Gracefully no-ops if hardware absent.
 */
public class BiometricHelper {

    private static BiometricPrompt.PromptInfo createPromptInfo() {
        BiometricPrompt.PromptInfo.Builder builder = new BiometricPrompt.PromptInfo.Builder();
        builder.setTitle(LocaleController.getString(R.string.NekoX));
        if (!NaConfig.INSTANCE.getAllowSystemPasscode().Bool()) {
            builder.setNegativeButtonText(LocaleController.getString(R.string.Cancel));
        } else {
            builder.setDeviceCredentialAllowed(true);
        }
        builder.setConfirmationRequired(false);
        return builder.build();
    }

    public static void prompt(Activity activity, Runnable successCallback) {
        prompt(activity, successCallback, null);
    }

    public static void prompt(Activity activity, Runnable successCallback, Runnable failCallback) {
        if (activity == null) {
            if (failCallback != null) failCallback.run();
            return;
        }
        if (!(activity instanceof FragmentActivity)) {
            if (successCallback != null) successCallback.run();
            return;
        }
        try {
            FragmentActivity fa = (FragmentActivity) activity;
            BiometricPrompt prompt = new BiometricPrompt(fa, ContextCompat.getMainExecutor(activity), new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    if (successCallback != null) successCallback.run();
                }
                @Override
                public void onAuthenticationFailed() {
                    if (failCallback != null) failCallback.run();
                }
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    FileLog.d("BiometricHelper error " + errorCode + " " + errString);
                    if (failCallback != null) failCallback.run();
                    else if (successCallback != null && errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                        // do nothing
                    }
                }
            });
            prompt.authenticate(createPromptInfo());
        } catch (Exception e) {
            FileLog.e(e);
            if (failCallback != null) failCallback.run();
        }
    }

    public static boolean hasBiometricEnrolled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                BiometricManager bm = ApplicationLoader.applicationContext.getSystemService(BiometricManager.class);
                if (bm == null) return false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    return bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS;
                } else {
                    return bm.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS;
                }
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        } else {
            return hasEnrolledFingerprints();
        }
    }

    public static boolean hasEnrolledFingerprints() {
        try {
            FingerprintManager fm = ApplicationLoader.applicationContext.getSystemService(FingerprintManager.class);
            if (fm != null) {
                return fm.isHardwareDetected() && fm.hasEnrolledFingerprints();
            }
        } catch (SecurityException e) {
            FileLog.e(e);
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            org.telegram.messenger.support.fingerprint.FingerprintManagerCompat compat = org.telegram.messenger.support.fingerprint.FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
            return compat.isHardwareDetected() && compat.hasEnrolledFingerprints();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return false;
    }

    public static boolean checkBiometricAvailable() {
        if (!hasBiometricEnrolled()) return false;
        boolean hasFingerprints = hasEnrolledFingerprints();
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

    public static int getBiometricIconResId() {
        return R.drawable.fingerprint;
    }
}
