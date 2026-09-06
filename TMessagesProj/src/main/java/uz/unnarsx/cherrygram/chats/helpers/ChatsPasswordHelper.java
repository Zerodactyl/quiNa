package uz.unnarsx.cherrygram.chats.helpers;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

/**
 * Alias to tw.nekomimi.nekogram.helpers.ChatsPasswordHelper for Cherrygram compatibility.
 */
public class ChatsPasswordHelper extends tw.nekomimi.nekogram.helpers.ChatsPasswordHelper {

    public ChatsPasswordHelper(int num) {
        super(num);
    }

    public static ChatsPasswordHelper getInstance(int num) {
        // delegate to base instance but return alias type (wraps base)
        tw.nekomimi.nekogram.helpers.ChatsPasswordHelper base = tw.nekomimi.nekogram.helpers.ChatsPasswordHelper.getInstance(num);
        ChatsPasswordHelper alias = new ChatsPasswordHelper(num);
        // copy cache via reflection? simpler: just return base cast via alias instance that delegates
        // Instead, create alias that delegates calls to base
        return new Delegating(num);
    }

    private static class Delegating extends ChatsPasswordHelper {
        private final tw.nekomimi.nekogram.helpers.ChatsPasswordHelper delegate;
        Delegating(int num) {
            super(num);
            delegate = tw.nekomimi.nekogram.helpers.ChatsPasswordHelper.getInstance(num);
        }
        @Override public String getPasscodeArray() { return delegate.getPasscodeArray(); }
        @Override public void saveArrayList(ArrayList<String> list, String key) { delegate.saveArrayList(list, key); }
        @Override public ArrayList<String> getArrayList(String key) { return delegate.getArrayList(key); }
        @Override public boolean isChatLocked(long chatId) { return delegate.isChatLocked(chatId); }
        @Override public boolean isChatLocked(MessageObject mo) { return delegate.isChatLocked(mo); }
        @Override public boolean isEncryptedChat(long chatId) { return delegate.isEncryptedChat(chatId); }
        @Override public boolean isEncryptedChat(MessageObject mo) { return delegate.isEncryptedChat(mo); }
        @Override public ArrayList<TLRPC.MessageEntity> checkLockedChatsEntities(MessageObject mo) { return delegate.checkLockedChatsEntities(mo); }
        @Override public ArrayList<TLRPC.MessageEntity> checkLockedChatsEntities(MessageObject mo, ArrayList<TLRPC.MessageEntity> o) { return delegate.checkLockedChatsEntities(mo, o); }
        @Override public String replaceStringToSpoilers(String t, boolean f) { return delegate.replaceStringToSpoilers(t, f); }
        @Override public int getLockedChatsCount() { return delegate.getLockedChatsCount(); }
        @Override public boolean shouldRequireBiometrics(long u, long c, int e) { return delegate.shouldRequireBiometrics(u,c,e); }
        @Override public boolean shouldRequireBiometricsToOpenChats() { return delegate.shouldRequireBiometricsToOpenChats(); }
        @Override public boolean shouldRequireBiometricsToOpenEncryptedChats() { return delegate.shouldRequireBiometricsToOpenEncryptedChats(); }
        @Override public boolean askPasscodeBeforeDelete() { return delegate.askPasscodeBeforeDelete(); }
        @Override public boolean checkBiometricAvailable() { return delegate.checkBiometricAvailable(); }
    }
}
