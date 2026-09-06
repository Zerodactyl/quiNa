package xyz.nextalone.nagram

import android.content.SharedPreferences
import android.net.Uri
import android.util.Base64
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.config.ConfigItem
import tw.nekomimi.nekogram.config.ConfigItemKeyLinked
import tw.nekomimi.nekogram.config.ConfigItemKeyLinkedGroup
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.util.ArrayList
import androidx.core.net.toUri


object NaConfig {
    const val TAG =
        "NextAlone"
    val preferences: SharedPreferences =
        NkmrConfig.preferences
    val sync =
        Any()
    private var configLoaded =
        false
    private val configs =
        ArrayList<ConfigItem>()

    // Configs
    val forceCopy =
        addConfig(
            "ForceCopy",
            ConfigItem.configTypeBool,
            false
        )
    val disableSecondAddress =
        addConfig(
            "disableSecondAddress",
            ConfigItem.configTypeBool,
            true
        )
    val showInvertReply =
        addConfig(
            "InvertReply",
            ConfigItem.configTypeBool,
            false
        )
    val showGreatOrPoor =
        addConfig(
            "GreatOrPoor",
            ConfigItem.configTypeBool,
            false
        )
    val showTextBold =
        addConfig(
            "TextBold",
            ConfigItem.configTypeBool,
            true
        )
    val showTextItalic =
        addConfig(
            "TextItalic",
            ConfigItem.configTypeBool,
            true
        )
    val showTextMono =
        addConfig(
            "TextMonospace",
            ConfigItem.configTypeBool,
            true
        )
    val showTextStrikethrough =
        addConfig(
            "TextStrikethrough",
            ConfigItem.configTypeBool,
            true
        )
    val showTextUnderline =
        addConfig(
            "TextUnderline",
            ConfigItem.configTypeBool,
            true
        )
    val showTextQuote =
        addConfig(
            "TextQuote",
            ConfigItem.configTypeBool,
            true
        )
    val showTextSpoiler =
        addConfig(
            "TextSpoiler",
            ConfigItem.configTypeBool,
            true
        )
    val showTextCreateLink =
        addConfig(
            "TextLink",
            ConfigItem.configTypeBool,
            true
        )
    val showTextCreateMention =
        addConfig(
            "TextCreateMention",
            ConfigItem.configTypeBool,
            true
        )
    val showTextRegular =
        addConfig(
            "TextRegular",
            ConfigItem.configTypeBool,
            true
        )
    val combineMessage =
        addConfig(
            "CombineMessage",
            ConfigItem.configTypeInt,
            0
        )
    val showTextUndoRedo =
        addConfig(
            "TextUndoRedo",
            ConfigItem.configTypeBool,
            false
        )
    val noiseSuppressAndVoiceEnhance =
        addConfig(
            "NoiseSuppressAndVoiceEnhance",
            ConfigItem.configTypeBool,
            false
        )
    val showNoQuoteForward =
        addConfig(
            "NoQuoteForward",
            ConfigItem.configTypeBool,
            true
        )
    val disableQuoteForward =
        addConfig(
            "DisableQuoteForward",
            ConfigItem.configTypeBool,
            false
        )
    val showRepeatAsCopy =
        addConfig(
            "RepeatAsCopy",
            ConfigItem.configTypeBool,
            false
        )
    val doubleTapAction =
        addConfig(
            "DoubleTapAction",
            ConfigItem.configTypeInt,
            0
        )
    val doubleTapActionOut =
        addConfig(
            "DoubleTapActionOut",
            ConfigItem.configTypeInt,
            8
        )
    val doubleTapSeekDuration =
        addConfig(
            "doubleTapSeekDuration",
            ConfigItem.configTypeInt,
            1
        )
    val showCopyPhoto =
        addConfig(
            "CopyPhoto",
            ConfigItem.configTypeBool,
            false
        )
    val showReactions =
        addConfig(
            "Reactions",
            ConfigItem.configTypeBool,
            true
        )
    val showServicesTime =
        addConfig(
            "ShowServicesTime",
            ConfigItem.configTypeBool,
            true
        )
    val formatTimeWithSeconds =
        addConfig(
            "FormatTimeWithSeconds",
            ConfigItem.configTypeBool,
            false
        )
    val autoQuoteReplies =
        addConfig(
            "AutoQuoteReplies",
            ConfigItem.configTypeBool,
            false
        )
    val customTitle =
        addConfig(
            "CustomTitle",
            ConfigItem.configTypeString,
            LocaleController.getString(
                R.string.NekoX
            )
        )
    val useSystemUnlock =
        addConfig(
            "UseSystemUnlock",
            ConfigItem.configTypeBool,
            true
        )
    val codeSyntaxHighlight =
        addConfig(
            "CodeSyntaxHighlight",
            ConfigItem.configTypeBool,
            true
        )
    val dateOfForwardedMsg =
        addConfig(
            "DateOfForwardedMsg",
            ConfigItem.configTypeBool,
            false
        )
    val showMessageID =
        addConfig(
            "ShowMessageID",
            ConfigItem.configTypeBool,
            false
        )
    val showRPCError =
        addConfig(
            "ShowRPCError",
            ConfigItem.configTypeBool,
            false
        )
    val showPremiumStarInChat =
        addConfig(
            "ShowPremiumStarInChat",
            ConfigItem.configTypeBool,
            true
        )
    val showPremiumAvatarAnimation =
        addConfig(
            "ShowPremiumAvatarAnimation",
            ConfigItem.configTypeBool,
            true
        )
    val alwaysSaveChatOffset =
        addConfig(
            "AlwaysSaveChatOffset",
            ConfigItem.configTypeBool,
            true
        )
    val autoReplaceRepeat =
        addConfig(
            "AutoReplaceRepeat",
            ConfigItem.configTypeBool,
            true
        )
    val autoInsertGIFCaption =
        addConfig(
            "AutoInsertGIFCaption",
            ConfigItem.configTypeBool,
            true
        )
    val defaultMonoLanguage =
        addConfig(
            "DefaultMonoLanguage",
            ConfigItem.configTypeString,
            ""
        )
    val disableGlobalSearch =
        addConfig(
            "DisableGlobalSearch",
            ConfigItem.configTypeBool,
            false
        )
    val hideOriginAfterTranslation: ConfigItem =
        addConfig(
            "HideOriginAfterTranslation",
            ConfigItem.configTypeBool,
            false
        )
    val zalgoFilter =
        addConfig(
            "ZalgoFilter",
            ConfigItem.configTypeBool,
            false
        )
    val customChannelLabel =
        addConfig(
            "CustomChannelLabel",
            ConfigItem.configTypeString,
            LocaleController.getString(
                R.string.channelLabel
            )
        )
    val alwaysShowDownloadIcon =
        addConfig(
            "AlwaysShowDownloadIcon",
            ConfigItem.configTypeBool,
            false
        )
    val quickToggleAnonymous =
        addConfig(
            "QuickToggleAnonymous",
            ConfigItem.configTypeBool,
            false
        )
    val realHideTimeForSticker =
        addConfig(
            "RealHideTimeForSticker",
            ConfigItem.configTypeBool,
            false
        )
    val ignoreFolderCount =
        addConfig(
            "IgnoreFolderCount",
            ConfigItem.configTypeBool,
            false
        )
    val customArtworkApi =
        addConfig(
            "CustomArtworkApi",
            ConfigItem.configTypeString,
            ""
        )
    val customGreat =
        addConfig(
            "CustomGreat",
            ConfigItem.configTypeString,
            LocaleController.getString(
                R.string.Great
            )
        )
    val CustomPoor =
        addConfig(
            "CustomPoor",
            ConfigItem.configTypeString,
            LocaleController.getString(
                R.string.Poor
            )

        )
    val customEditedMessage =
        addConfig(
            "CustomEditedMessage",
            ConfigItem.configTypeString,
            ""
        )
    val showEditedIcon =
        addConfig(
            "ShowEditedIcon",
            ConfigItem.configTypeBool,
            false
        )
    val disableProxyWhenVpnEnabled =
        addConfig(
            "DisableProxyWhenVpnEnabled",
            ConfigItem.configTypeBool,
            false
        )
    val fakeHighPerformanceDevice =
        addConfig(
            "FakeHighPerformanceDevice",
            ConfigItem.configTypeBool,
            false
        )
    val disableEmojiDrawLimit =
        addConfig(
            "DisableEmojiDrawLimit",
            ConfigItem.configTypeBool,
            false
        )
    val iconDecoration =
        addConfig(
            "IconDecoration",
            ConfigItem.configTypeInt,
            0
        )
    val notificationIcon =
        addConfig(
            "NotificationIcon",
            ConfigItem.configTypeInt,
            0
        )
    val showSetReminder =
        addConfig(
            "SetReminder",
            ConfigItem.configTypeBool,
            false
        )
    val showOnlineStatus =
        addConfig(
            "ShowOnlineStatus",
            ConfigItem.configTypeBool,
            false
        )
    val showFullAbout =
        addConfig(
            "ShowFullAbout",
            ConfigItem.configTypeBool,
            false
        )
    val hideMessageSeenTooltip =
        addConfig(
            "HideMessageSeenTooltip",
            ConfigItem.configTypeBool,
            false
        )
    val autoTranslate =
        addConfig(
            "AutoTranslate",
            ConfigItem.configTypeBool,
            false
        )
    val typeMessageHintUseGroupName =
        addConfig(
            "TypeMessageHintUseGroupName",
            ConfigItem.configTypeBool,
            false
        )
    val showSendAsUnderMessageHint =
        addConfig(
            "ShowSendAsUnderMessageHint",
            ConfigItem.configTypeBool,
            false
        )
    val hideBotButtonInInputField =
        addConfig(
            "HideBotButtonInInputField",
            ConfigItem.configTypeBool,
            false
        )
    val chatDecoration =
        addConfig(
            "ChatDecoration",
            ConfigItem.configTypeInt,
            0
        )
    val stickerShape =
        addConfig(
            "StickerShape",
            ConfigItem.configTypeInt,
            1
        )
    val doNotUnarchiveBySwipe =
        addConfig(
            "DoNotUnarchiveBySwipe",
            ConfigItem.configTypeBool,
            false
        )
    val doNotShareMyPhoneNumber =
        addConfig(
            "DoNotShareMyPhoneNumber",
            ConfigItem.configTypeBool,
            false
        )
    val defaultDeleteMenu =
        addConfig(
            "DefaultDeleteMenu",
            ConfigItem.configTypeInt,
            0
        )
    val defaultDeleteMenuBanUsers =
        addConfig(
            "DeleteBanUsers",
            defaultDeleteMenu,
            3,
            false
        )
    val defaultDeleteMenReportSpam =
        addConfig(
            "DeleteReportSpam",
            defaultDeleteMenu,
            2,
            false
        )
    val defaultDeleteMenuDeleteAll =
        addConfig(
            "DeleteAll",
            defaultDeleteMenu,
            1,
            false
        )
    val defaultDeleteMenuDoActionsInCommonGroups =
        addConfig(
            "DoActionsInCommonGroups",
            defaultDeleteMenu,
            0,
            false
        )
    val defaultDeleteMenuDeleteAllReactions =
        addConfig(
            "DeleteAllReactionsFromUsers",
            defaultDeleteMenu,
            4,
            false
        )
    val disableSuggestionView =
        addConfig(
            "DisableSuggestionView",
            ConfigItem.configTypeBool,
            false
        )
    val disableStories =
        addConfig(
            "DisableStories",
            ConfigItem.configTypeBool,
            false
        )
    val disableSendReadStories =
        addConfig(
            "DisableSendReadStories",
            ConfigItem.configTypeBool,
            false
        )
    val hideFilterMuteAll =
        addConfig(
            "HideFilterMuteAll",
            ConfigItem.configTypeBool,
            false
        )
    val useLocalQuoteColor =
        addConfig(
            "UseLocalQuoteColor",
            ConfigItem.configTypeBool,
            false
        )
    val useLocalQuoteColorData =
        addConfig(
            "useLocalQuoteColorData",
            ConfigItem.configTypeString,
            ""
        )
    val showRecentOnlineStatus =
        addConfig(
            "ShowRecentOnlineStatus",
            ConfigItem.configTypeBool,
            false
        )
    val showSquareAvatar =
        addConfig(
            "ShowSquareAvatar",
            ConfigItem.configTypeBool,
            false
        )
    val disableCustomWallpaperUser =
        addConfig(
            "DisableCustomWallpaperUser",
            ConfigItem.configTypeBool,
            false
        )
    val disableCustomWallpaperChannel =
        addConfig(
            "DisableCustomWallpaperChannel",
            ConfigItem.configTypeBool,
            false
        )
    val externalStickerCache =
        addConfig(
            "ExternalStickerCache",
            ConfigItem.configTypeString,
            ""
        )
    var externalStickerCacheUri: Uri?
        get() = externalStickerCache.String().let { if (it.isBlank()) return null else return it.toUri() }
        set(value) = externalStickerCache.setConfigString(value.toString())
    val externalStickerCacheAutoRefresh =
        addConfig(
            "ExternalStickerCacheAutoRefresh",
            ConfigItem.configTypeBool,
            false
        )
    val externalStickerCacheDirNameType =
        addConfig(
            "ExternalStickerCacheDirNameType",
            ConfigItem.configTypeInt,
            0
        )
    val disableMarkdown =
        addConfig(
            "DisableMarkdown",
            ConfigItem.configTypeBool,
            false
        )
    val newMarkdownParser =
        addConfig(
            "NewMarkdownParser",
            ConfigItem.configTypeBool,
            true
        )
    val markdownParseLinks =
        addConfig(
            "MarkdownParseLinks",
            ConfigItem.configTypeBool,
            true
        )
    val disableClickProfileGalleryView =
        addConfig(
            "DisableClickProfileGalleryView",
            ConfigItem.configTypeBool,
            false
        )
    val showSmallGIF =
        addConfig(
            "ShowSmallGIF",
            ConfigItem.configTypeBool,
            false
        )
    val disableClickCommandToSend =
        addConfig(
            "DisableClickCommandToSend",
            ConfigItem.configTypeBool,
            false
        )
    val disableDialogsFloatingButton =
        addConfig(
            "DisableDialogsFloatingButton",
            ConfigItem.configTypeBool,
            false
        )
    val disableFlagSecure =
        addConfig(
            "DisableFlagSecure",
            ConfigItem.configTypeBool,
            false
        )
    val centerActionBarTitle =
        addConfig(
            "CenterActionBarTitle",
            ConfigItem.configTypeBool,
            false
        )
    val showQuickReplyInBotCommands =
        addConfig(
            "ShowQuickReplyInBotCommands",
            ConfigItem.configTypeBool,
            false
        )
    val pushServiceType =
        addConfig(
            "PushServiceType",
            ConfigItem.configTypeInt,
            1
        )
    val pushServiceTypeInAppDialog =
        addConfig(
            "PushServiceTypeInAppDialog",
            ConfigItem.configTypeBool,
            true
        )
    val pushServiceTypeUnifiedGateway =
        addConfig(
            "PushServiceTypeUnifiedGateway",
            ConfigItem.configTypeString,
            "https://p2p.hoyolab.pp.ua/"
        )
    val pushServiceTypeUnifiedSimple =
        addConfig(
            "PushServiceTypeUnifiedSimple",
            ConfigItem.configTypeString,
            ""
        )
    val pushServiceTypeUnifiedWebPushPrivateKey =
        addConfig(
            "PushServiceTypeUnifiedWebPushPrivateKey",
            ConfigItem.configTypeString,
            ""
        )
    val pushServiceTypeUnifiedWebPushPublicKey =
        addConfig(
            "PushServiceTypeUnifiedWebPushPublicKey",
            ConfigItem.configTypeString,
            ""
        )
    val pushServiceTypeUnifiedWebPushAuthSecret =
        addConfig(
            "PushServiceTypeUnifiedWebPushAuthSecret",
            ConfigItem.configTypeString,
            ""
        )
    val sendMp4DocumentAsVideo =
        addConfig(
            "SendMp4DocumentAsVideo",
            ConfigItem.configTypeBool,
            false
        )
    val disableChannelMuteButton =
        addConfig(
            "DisableChannelMuteButton",
            ConfigItem.configTypeBool,
            false
        )
    val disablePreviewVideoSoundShortcut =
        addConfig(
            "DisablePreviewVideoSoundShortcut",
            ConfigItem.configTypeBool,
            false
        )
    val disableAutoWebLogin =
        addConfig(
            "DisableAutoWebLogin",
            ConfigItem.configTypeBool,
            false
        )
    val sentryAnalytics =
        addConfig(
            "SentryAnalytics",
            ConfigItem.configTypeBool,
            true
        )
    val regexFiltersEnabled =
        addConfig(
            "RegexFilters",
            ConfigItem.configTypeBool,
            false
        )
    val regexFiltersData =
        addConfig(
            "RegexFiltersData",
            ConfigItem.configTypeString,
            "[]"
        )
    val regexFiltersEnableInChats =
        addConfig(
            "RegexFiltersEnableInChats",
            ConfigItem.configTypeBool,
            true
        )
    val showTimeHint =
        addConfig(
            "ShowTimeHint",
            ConfigItem.configTypeBool,
            true
        )
    val showHiddenFeature =
        addConfig(
            "ShowHiddenFeature",
            ConfigItem.configTypeBool,
            false
        )
    val searchHashtagDefaultPageChannel =
        addConfig(
            "SearchHashtagDefaultPageChannel",
            ConfigItem.configTypeInt,
            0
        )
    val searchHashtagDefaultPageChat =
        addConfig(
            "SearchHashtagDefaultPageChat",
            ConfigItem.configTypeInt,
            0
        )
    val openUrlOutBotWebViewRegex =
        addConfig(
            "OpenUrlOutBotWebViewRegex",
            ConfigItem.configTypeString,
            ""
        )
    val enablePanguOnSending =
        addConfig(
            "EnablePanguOnSending",
            ConfigItem.configTypeBool,
            false
        )
    val enablePanguOnEditing =
        addConfig(
            "EnablePanguOnEditing",
            ConfigItem.configTypeBool,
            false
        )
    val enablePanguOnReceiving =
        addConfig(
            "EnablePanguOnReceiving",
            ConfigItem.configTypeBool,
            false
        )
    val defaultHlsVideoQuality =
        addConfig(
            "DefaultHlsVideoQuality",
            ConfigItem.configTypeInt,
            0
        )
    val disableBotOpenButton =
        addConfig(
            "DisableBotOpenButton",
            ConfigItem.configTypeBool,
            false
        )
    val customTitleUserName =
        addConfig(
            "CustomTitleUserName",
            ConfigItem.configTypeBool,
            false
        )
    val enhancedVideoBitrate =
        addConfig(
            "EnhancedVideoBitrate",
            ConfigItem.configTypeBool,
            false
        )
    private val disableTrendingFlags =
        addConfig(
            "DisableTrendingFlags",
            ConfigItem.configTypeInt,
            0x1C0
        )
    val disableStarsSubscription =
        addConfig(
            "DisableStarsSubscription",
            disableTrendingFlags,
            0,
            false
        )
    val disablePremiumExpiring =
        addConfig(
            "DisablePremiumExpiring",
            disableTrendingFlags,
            1,
            false
        )
    val disablePremiumUpgrade =
        addConfig(
            "DisablePremiumUpgrade",
            disableTrendingFlags,
            2,
            false
        )
    val disablePremiumChristmas =
        addConfig(
            "DisablePremiumChristmas",
            disableTrendingFlags,
            3,
            false
        )
    val disableBirthdayContact =
        addConfig(
            "DisableBirthdayContact",
            disableTrendingFlags,
            4,
            false
        )
    val disablePremiumRestore =
        addConfig(
            "DisablePremiumRestore",
            disableTrendingFlags,
            5,
            false
        )
    val disableFeatuerdEmojis =
        addConfig(
            "DisableFeatuerdEmojis",
            disableTrendingFlags,
            6,
            false
        )
    val disableFeaturedStickers =
        addConfig(
            "DisableFeaturedStickers",
            disableTrendingFlags,
            7,
            false
        )
    val disableFeaturedGifs =
        addConfig(
            "DisableFeaturedGifs",
            disableTrendingFlags,
            8,
            false
        )
    val disablePremiumFavoriteEmojiTags =
        addConfig(
            "DisablePremiumFavoriteEmojiTags",
            disableTrendingFlags,
            9,
            false
        )
    val disableFavoriteSearchEmojiTags =
        addConfig(
            "DisableFavoriteSearchEmojiTags",
            disableTrendingFlags,
            10,
            false
        )
    val disableNonPremiumChannelChatShow =
        addConfig(
            "DisableNonPremiumChannelChatShow",
            disableTrendingFlags,
            11,
            false
        )
    val disableShortcutTagActions =
        addConfig(
            "DisableShortcutTagActions",
            disableTrendingFlags,
            12,
            false
        )
    val disablePhoneSharePrompt =
        addConfig(
            "DisablePhoneSharePrompt",
            disableTrendingFlags,
            13,
            false
        )
    val disablePremiumSendTodo =
        addConfig(
            "DisablePremiumSendTodo",
            disableTrendingFlags,
            14,
            false
        )
    val disableEmptyStarButton =
        addConfig(
            "DisableEmptyStarButton",
            disableTrendingFlags,
            15,
            false
        )
    val disableGifts =
        addConfig(
            "DisableGifts",
            disableTrendingFlags,
            16,
            false
    )
    val disableTrendingFeaturedStickersGroup =
        ConfigItemKeyLinkedGroup(
            "DisableTrendingFeaturedStickers",
            listOf(disableFeaturedStickers)
        )
    val disableTrendingFeaturedGifsGroup =
        ConfigItemKeyLinkedGroup(
            "DisableTrendingFeaturedGifs",
            listOf(disableFeaturedGifs)
        )
    val disableTrendingFeaturedEmojisGroup =
        ConfigItemKeyLinkedGroup(
            "DisableTrendingFeaturedEmojis",
            listOf(disableFeatuerdEmojis)
        )
    val disableTrendingPremiumHintsGroup =
        ConfigItemKeyLinkedGroup(
            "DisableTrendingPremiumHints",
            listOf(
                disablePremiumUpgrade,
                disablePremiumExpiring,
                disablePremiumRestore,
                disablePremiumChristmas,
                disableStarsSubscription
            )
        )
    val disableTrendingBirthdayGroup =
        ConfigItemKeyLinkedGroup(
            "DisableTrendingBirthday",
            listOf(disableBirthdayContact)
        )
    val disableTrendingEmojiTagsGroup =
        ConfigItemKeyLinkedGroup(
            "DisableTrendingEmojiTags",
            listOf(
                disableFavoriteSearchEmojiTags,
                disablePremiumFavoriteEmojiTags,
                disableShortcutTagActions
            )
        )
    val disableTrendingChannelPremiumBannerGroup =
        ConfigItemKeyLinkedGroup(
            "DisableTrendingChannelPremiumBanner",
            listOf(disableNonPremiumChannelChatShow)
        )
    val disableTrendingPhoneShareGroup =
        ConfigItemKeyLinkedGroup(
            "DisableTrendingPhoneShare",
            listOf(disablePhoneSharePrompt)
        )
    val disableTrendingGiftsPremiumGroup =
        ConfigItemKeyLinkedGroup(
            "DisableTrendingGiftsPremium",
            listOf(
                disableGifts,
                disableEmptyStarButton,
                disablePremiumSendTodo
            )
        )
    val disableRepeatInChannel =
        addConfig(
            "DisableRepeatInChannel",
            ConfigItem.configTypeBool,
            false
        )
    val disableActionBarButton =
        addConfig(
            "DisableActionBarButton",
            ConfigItem.configTypeInt,
            0
        )
    val disableActionBarButtonReply =
        addConfig(
            "Reply",
            disableActionBarButton,
            0,
            false
        )
    val disableActionBarButtonEdit =
        addConfig(
            "Edit",
            disableActionBarButton,
            1,
            false
        )
    val disableActionBarButtonSelectBetween =
        addConfig(
            "SelectBetween",
            disableActionBarButton,
            2,
            false
        )
    val disableActionBarButtonCopy =
        addConfig(
            "Copy",
            disableActionBarButton,
            3,
            false
        )
    val disableActionBarButtonForward =
        addConfig(
            "Forward",
            disableActionBarButton,
            4,
            false
        )
    val coloredAdminTitle =
        addConfig(
            "ColoredAdminTitle",
            ConfigItem.configTypeBool,
            false
        )
    val playerDecoder =
        addConfig(
            "PlayerDecoder",
            ConfigItem.configTypeInt,
            0
        )
    val showUserIconsInChatsList =
        addConfig(
            "ShowUserIconsInChatsList",
            ConfigItem.configTypeBool,
            false
        )
    val removeFavouriteStickersInRecentStickers =
        addConfig(
            "RemoveFavouriteStickersInRecentStickers",
            ConfigItem.configTypeBool,
            true
        )
    val showVoteCountBeforeVote =
        addConfig(
            "ShowVoteCountBeforeVote",
            ConfigItem.configTypeBool,
            false
        )
    val hideInstantCamera =
        addConfig(
            "HideInstantCamera",
            ConfigItem.configTypeBool,
            false
        )
    val useSystemAiService =
        addConfig(
            "UseSystemAiService",
            ConfigItem.configTypeBool,
            true
        )
    val navigationAnimationSpring =
        addConfig(
            "NavigationAnimationSpring",
            ConfigItem.configTypeBool,
            true
        )
    val forceEdgeToEdge =
        addConfig(
            "ForceEdgeToEdge",
            ConfigItem.configTypeBool,
            false
        )
    val useSystemPhotoPicker =
        addConfig(
            "UseSystemPhotoPicker",
            ConfigItem.configTypeBool,
            false
        )
    var tabStyle =
        addConfig(
            "TabStyle",
            ConfigItem.configTypeInt,
            0
        )
    val chatActivityNavbarTransparent =
        addConfig(
            "ChatActivityNavbarTransparent",
            ConfigItem.configTypeBool,
            false
        )
    val fixUrlPagePreview =
        addConfig(
            "FixUrlPagePreview",
            ConfigItem.configTypeBool,
            true
        )
    val fixUrlAutoInlineBot =
        addConfig(
            "FixUrlAutoInlineBot",
            ConfigItem.configTypeBool,
            true
        )
    val localInlineBotRulesData =
        addConfig(
            "LocalInlineBotRulesData",
            ConfigItem.configTypeString,
            ""
        )
    val localInlineBotRulesEnabled =
        addConfig(
            "LocalInlineBotRulesEnabled",
            ConfigItem.configTypeString,
            ""
        )
    val disabledRemoteInlineBotRules =
        addConfig(
            "DisabledRemoteInlineBotRules",
            ConfigItem.configTypeString,
            ""
        )
    val fixUrlAutoInlineBotSkipMediaPreview =
        addConfig(
            "FixUrlAutoInlineBotSkipMediaPreview",
            ConfigItem.configTypeBool,
            false
        )
    val deepLxCustomApi =
        addConfig(
            "DeepLxCustomApi",
            ConfigItem.configTypeString,
            ""
        )
    val deepLFormality =
        addConfig(
            "DeepLFormality",
            ConfigItem.configTypeInt,
            0 // 0: default, 1: more formal, 2: less formal
        )
    val deepLApiKey =
        addConfig(
            "DeepLApiKey",
            ConfigItem.configTypeString,
            ""
        )
    val deepLFreeApiKey =
        addConfig(
            "DeepLFreeApiKey",
            ConfigItem.configTypeString,
            ""
        )
    val summarizeTextButton =
        addConfig(
            "SummarizeTextButton",
            ConfigItem.configTypeInt,
            0
        )
    val disablePredictiveBackAnimation =
        addConfig(
            "DisablePredictiveBackAnimation",
            ConfigItem.configTypeBool,
            false
        )
    val llmProvider =
        addConfig(
            "LLMProvider",
            ConfigItem.configTypeInt,
            0
        )
    val llmApiFormat =
        addConfig(
            "LLMApiFormat",
            ConfigItem.configTypeInt,
            0
        )
    val llmApiKeys =
        addConfig(
            "LLMApiKeys",
            ConfigItem.configTypeString,
            ""
        )
    val llmApiUrl =
        addConfig(
            "LLMApiUrl",
            ConfigItem.configTypeString,
            "https://api.openai.com/v1/chat/completions"
        )
    val llmOpenAIModel =
        addConfig(
            "LLMOpenAIModel",
            ConfigItem.configTypeString,
            "gpt-4.1-mini"
        )
    val llmGeminiModel =
        addConfig(
            "LLMGeminiModel",
            ConfigItem.configTypeString,
            "gemini-2.5-flash"
        )
    val llmGroqModel =
        addConfig(
            "LLMGroqModel",
            ConfigItem.configTypeString,
            "llama-3.3-70b-versatile"
        )
    val llmDeepSeekModel =
        addConfig(
            "LLMDeepSeekModel",
            ConfigItem.configTypeString,
            "deepseek-chat"
        )
    val llmXAIModel =
        addConfig(
            "LLMXAIModel",
            ConfigItem.configTypeString,
            "grok-3-mini-fast"
        )
    val llmZhipuAIModel =
        addConfig(
            "LLMZhipuAIModel",
            ConfigItem.configTypeString,
            "GLM-4-Flash"
        )
    val llmMistralModel =
        addConfig(
            "LLMMistralModel",
            ConfigItem.configTypeString,
            "mistral-small-latest"
        )
    val llmOpenRouterModel =
        addConfig(
            "LLMOpenRouterModel",
            ConfigItem.configTypeString,
            "meta-llama/llama-3.3-70b-instruct"
        )
    val llmQwenModel =
        addConfig(
            "LLMQwenModel",
            ConfigItem.configTypeString,
            "qwen-turbo-latest"
        )
    val llmMoonshotModel =
        addConfig(
            "LLMMoonshotModel",
            ConfigItem.configTypeString,
            "moonshot-v1-8k"
        )
    val llmSiliconFlowModel =
        addConfig(
            "LLMSiliconFlowModel",
            ConfigItem.configTypeString,
            "Qwen/Qwen2.5-7B-Instruct"
        )
    val llmCustomModel =
        addConfig(
            "LLMCustomModel",
            ConfigItem.configTypeString,
            ""
        )
    val llmSystemPrompt =
        addConfig(
            "LLMSystemPrompt",
            ConfigItem.configTypeString,
            ""
        )
    val llmTranslationPrompt =
        addConfig(
            "LLMTranslationPrompt",
            ConfigItem.configTypeString,
            ""
        )
    val llmUseContext =
        addConfig(
            "LLMUseContext",
            ConfigItem.configTypeBool,
            false
        )
    val llmTemperature =
        addConfig(
            "LLMTemperature",
            ConfigItem.configTypeString,
            "0.7"
        )
    val mainTabsStyle =
        addConfig(
            "MainTabsStyle",
            ConfigItem.configTypeInt,
            0
        )
    val mainTabsOrder =
        addConfig(
            "MainTabsOrder",
            ConfigItem.configTypeString,
            "CHATS,CONTACTS,SETTINGS,!CALLS,PROFILE"
        )
    val mainTabsShowTitles =
        addConfig(
            "MainTabsShowTitles",
            ConfigItem.configTypeBool,
            true
        )
    val mainTabsDisplayMode =
        addConfig(
            "MainTabsDisplayMode",
            ConfigItem.configTypeInt,
            0
        )
    val mainTabsShowSearchButton =
        addConfig(
            "MainTabsShowSearchButton",
            ConfigItem.configTypeBool,
            true
        )
    val mainTabsForceOpenChats =
        addConfig(
            "MainTabsForceOpenChats",
            ConfigItem.configTypeBool,
            false
        )
    val mainTabsHideTitles =
        addConfig(
            "MainTabsHideTitles",
            ConfigItem.configTypeBool,
            false
        )
    val mainTabsHideContacts =
        addConfig(
            "MainTabsHideContacts",
            ConfigItem.configTypeBool,
            false
        )
    val hideTabBarPermissionWarnings =
        addConfig(
            "HideTabBarPermissionWarnings",
            ConfigItem.configTypeBool,
            false
        )
    val showRecentChatsOnTabLongPress =
        addConfig(
            "ShowRecentChatsOnTabLongPress",
            ConfigItem.configTypeBool,
            false
        )
    val customIpStrategy =
        addConfig(
            "CustomIpStrategy",
            ConfigItem.configTypeInt,
            0
        )
    val customDialogsMenu =
        addConfig(
            "CustomDialogsMenu",
            ConfigItem.configTypeInt,
            59
        )
    val customDialogsMenuTheme =
        addConfig(
            "SwitchThemeToDay",
            customDialogsMenu,
            0,
            true
        )
    val showRecentChatsInSidebar =
        addConfig(
            "ShowRecentChatsInSidebar",
            ConfigItem.configTypeBool,
            true
        )
    val customDialogsMenuNewGroup =
        addConfig(
            "NewGroup",
            customDialogsMenu,
            1,
            true
        )
    val customDialogsMenuNewMessage =
        addConfig(
            "NewMessageTitle",
            customDialogsMenu,
            2,
            false
        )
    val customDialogsMenuSavedMessages =
        addConfig(
            "SavedMessages",
            customDialogsMenu,
            3,
            true
        )
    val customDialogsMenuSettings =
        addConfig(
            "Settings",
            customDialogsMenu,
            4,
            true
        )
    val customDialogsMenuProxy =
        addConfig(
            "MenuProxyTitle",
            customDialogsMenu,
            5,
            true
        )
    val customDialogsMenuAccount =
        addConfig(
            "AddAccount",
            customDialogsMenu,
            6,
            false
        )
    val sidebarSettingsActivity =
        addConfig(
            "SidebarSettingsActivity",
            ConfigItem.configTypeBool,
            true
        )
    val sectionsSeparatedHeaders =
        addConfig(
            "sectionsSeparatedHeaders",
            ConfigItem.configTypeBool,
            true
        )
    val drawerItemMyProfile =
        addConfig(
            "DrawerItemMyProfile",
            ConfigItem.configTypeBool,
            true
        )
    val drawerItemSetEmojiStatus =
        addConfig(
            "DrawerItemSetEmojiStatus",
            ConfigItem.configTypeBool,
            true
        )
    val drawerItemNewGroup =
        addConfig(
            "DrawerItemNewGroup",
            ConfigItem.configTypeBool,
            true
        )
    val drawerItemNewChannel =
        addConfig(
            "DrawerItemNewChannel",
            ConfigItem.configTypeBool,
            false
        )
    val drawerItemContacts =
        addConfig(
            "DrawerItemContacts",
            ConfigItem.configTypeBool,
            true
        )
    val drawerItemCalls =
        addConfig(
            "DrawerItemCalls",
            ConfigItem.configTypeBool,
            true
        )
    val drawerItemRecentChats =
        addConfig(
            "DrawerItemRecentChats",
            ConfigItem.configTypeBool,
            true
        )
    val drawerItemSaved =
        addConfig(
            "DrawerItemSaved",
            ConfigItem.configTypeBool,
            true
        )
    val drawerItemSettings =
        addConfig(
            "DrawerItemSettings",
            ConfigItem.configTypeBool,
            true
        )
    val drawerItemNSettings =
        addConfig(
            "DrawerItemNSettings",
            ConfigItem.configTypeBool,
            true
        )
    val drawerItemQrLogin =
        addConfig(
            "DrawerItemQrLogin",
            ConfigItem.configTypeBool,
            false
        )
    val drawerItemArchivedChats =
        addConfig(
            "DrawerItemArchivedChats",
            ConfigItem.configTypeBool,
            false
        )
    val drawerItemRestartApp =
        addConfig(
            "DrawerItemRestartApp",
            ConfigItem.configTypeBool,
            false
        )
    val drawerItemBrowser =
        addConfig(
            "DrawerItemBrowser",
            ConfigItem.configTypeBool,
            false
        )
    val drawerItemSessions =
        addConfig(
            "DrawerItemSessions",
            ConfigItem.configTypeBool,
            false
        )
    val drawerItemGhost =
        addConfig(
            "DrawerItemGhost",
            ConfigItem.configTypeBool,
            true
        )
    val mainMenuLayout =
        addConfig(
            "MainMenuLayout",
            ConfigItem.configTypeString,
            ""
        )
    val mainMenuHiddenItems =
        addConfig(
            "MainMenuHiddenItems",
            ConfigItem.configTypeString,
            ""
        )
    val showAddToBookmark =
        addConfig(
            "ShowAddToBookmark",
            ConfigItem.configTypeBool,
            false
        )
    val compactMessageMenuOptions =
        addConfig(
            "CompactMessageMenuOptions",
            ConfigItem.configTypeString,
            ""
        )
    val hiddenMessageMenuOptions =
        addConfig(
            "HiddenMessageMenuOptions",
            ConfigItem.configTypeString,
            ""
        )
    val showRecentForwardTab =
        addConfig(
            "ShowRecentForwardTab",
            ConfigItem.configTypeBool,
            false
        )
    val disableProfileAvatarBlur =
        addConfig(
            "DisableProfileAvatarBlur",
            ConfigItem.configTypeBool,
            false
        )
    val extendedFeatureUnlockedToken =
        addConfig(
            "ExtendedFeatureUnlockedToken",
            ConfigItem.configTypeString,
            ""
        )
    val disableAiEditor =
        addConfig(
            "DisableAiEditor",
            ConfigItem.configTypeBool,
            false
        )
    val disableGlareEffects =
        addConfig(
            "DisableGlareEffects",
            ConfigItem.configTypeBool,
            false
        )
    val liquidGlassAngle =
        addConfig(
            "LiquidGlassAngle",
            ConfigItem.configTypeInt,
            0
    )
    val liquidGlassIntensity =
        addConfig(
            "LiquidGlassIntensity",
            ConfigItem.configTypeInt,
            75
        )
    val disableGooeyAvatarAnimation =
        addConfig(
            "DisableGooeyAvatarAnimation",
            ConfigItem.configTypeBool,
            false
        )
    val filterMatchLinks =
        addConfig(
            "FilterMatchLinks",
            ConfigItem.configTypeBool,
            true
        )
    val quickReadReactionsOnLongClick =
        addConfig(
            "QuickReadReactionsOnLongClick",
            ConfigItem.configTypeBool,
            false
        )
    val bottomBarSettingsLongPress =
        addConfig(
            "BottomBarSettingsLongPress",
            ConfigItem.configTypeBool,
            true
        )
    val cleanTrackingParams =
        addConfig(
            "CleanTrackingParams",
            ConfigItem.configTypeBool,
            true
        )
    val patchAndCleanupLinks =
        addConfig(
            "PatchAndCleanupLinks",
            ConfigItem.configTypeBool,
            false
        )
    val customGetQueryBlacklist =
        addConfig(
            "CustomGetQueryBlacklist",
            ConfigItem.configTypeString,
            ""
        )
    var customGetQueryBlacklistData: ArrayList<String> = ArrayList()
    fun applyCustomGetQueryBlacklist() {
        val queries = customGetQueryBlacklist.String().split(",")
        customGetQueryBlacklistData.clear()
        for (q in queries) {
            val trimmed = q.trim()
            if (trimmed.isNotEmpty()) {
                customGetQueryBlacklistData.add(trimmed)
            }
        }
    }
    fun replaceCustomGetQueryBlacklist(newList: Collection<String>) {
        customGetQueryBlacklistData.clear()
        customGetQueryBlacklistData.addAll(newList)
        val str = customGetQueryBlacklistData.joinToString(",")
        customGetQueryBlacklist.setConfigString(str)
    }
    val showCopyFileRef =
        addConfig(
            "ShowCopyFileRef",
            ConfigItem.configTypeBool,
            false
        )
    val addCommaAfterMention =
        addConfig(
            "AddCommaAfterMention",
            ConfigItem.configTypeBool,
            false
        )
    val fullSensorRoundVideo =
        addConfig(
            "FullSensorRoundVideo",
            ConfigItem.configTypeBool,
            true
        )
    val hideGreetingSticker =
        addConfig(
            "HideGreetingSticker",
            ConfigItem.configTypeBool,
            false
        )
    val m3ExpressiveProgress =
        addConfig(
            "M3ExpressiveProgress",
            ConfigItem.configTypeBool,
            true
        )
    val m3ExpressiveDialogs =
        addConfig(
            "M3ExpressiveDialogs",
            ConfigItem.configTypeBool,
            true
        )
    val m3SectionCards =
        addConfig(
            "M3SectionCards",
            ConfigItem.configTypeBool,
            true
        )
    val m3GlassMenu =
        addConfig(
            "M3GlassMenu",
            ConfigItem.configTypeBool,
            false
        )
    val disableNumberRounding =
        addConfig(
            "DisableNumberRounding",
            ConfigItem.configTypeBool,
            false
        )
    val swipeMusicBar =
        addConfig(
            "SwipeMusicBar",
            ConfigItem.configTypeBool,
            false
        )
    val mediaSpoilerByDefault =
        addConfig(
            "MediaSpoilerByDefault",
            ConfigItem.configTypeBool,
            false
        )
    val drawerQuran =
        addConfig(
            "DrawerQuran",
            ConfigItem.configTypeBool,
            false
        )
    val drawerBible =
        addConfig(
            "DrawerBible",
            ConfigItem.configTypeBool,
            false
        )
    val repliesLinksShowColors =
        addConfig(
            "RepliesLinksShowColors",
            ConfigItem.configTypeBool,
            true
        )
    val hideSentTimeOnStickers =
        addConfig(
            "HideSentTimeOnStickers",
            ConfigItem.configTypeBool,
            false
        )
    val translatorKeepMarkdown =
        addConfig(
            "TranslatorKeepMarkdown",
            ConfigItem.configTypeBool,
            true
        )
    val largePhotos =
        addConfig(
            "LargePhotos",
            ConfigItem.configTypeBool,
            false
        )
    val disableBgParallax =
        addConfig(
            "DisableBgParallax",
            ConfigItem.configTypeBool,
            false
        )
    val hideFadeView =
        addConfig(
            "HideFadeView",
            ConfigItem.configTypeBool,
            false
        )
    val reduceMenuMotion =
        addConfig(
            "ReduceMenuMotion",
            ConfigItem.configTypeBool,
            false
        )
    val disableSaveDraftToCloud =
        addConfig(
            "DisableSaveDraftToCloud",
            ConfigItem.configTypeBool,
            false
        )
    val tabStyleStroke =
        addConfig(
            "TabStyleStroke",
            ConfigItem.configTypeBool,
            false
        )
    val forceVideoNewRewindMethod =
        addConfig(
            "ForceVideoNewRewindMethod",
            ConfigItem.configTypeBool,
            false
        )
    val m3ExpressiveAll =
        addConfig(
            "M3ExpressiveAll",
            ConfigItem.configTypeBool,
            false
        )
    val m3WavySlider =
        addConfig(
            "M3WavySlider",
            ConfigItem.configTypeBool,
            false
        )
    val m3TabPill =
        addConfig(
            "M3TabPill",
            ConfigItem.configTypeBool,
            false
        )
    val m3SpringPhysics =
        addConfig(
            "M3SpringPhysics",
            ConfigItem.configTypeBool,
            false
        )
    val m3ExpressiveVoice =
        addConfig(
            "M3ExpressiveVoice",
            ConfigItem.configTypeBool,
            false
        )
    val m3ExpressiveSwitch =
        addConfig(
            "M3ExpressiveSwitch",
            ConfigItem.configTypeBool,
            false
        )
    val m3ExpressiveFab =
        addConfig(
            "M3ExpressiveFab",
            ConfigItem.configTypeBool,
            false
        )
    val m3ExpressiveBubbles =
        addConfig(
            "M3ExpressiveBubbles",
            ConfigItem.configTypeBool,
            false
        )
    val m3ExpressiveBottomSheet =
        addConfig(
            "M3ExpressiveBottomSheet",
            ConfigItem.configTypeBool,
            false
        )
    val m3ExpressivePillSliders =
        addConfig(
            "M3ExpressivePillSliders",
            ConfigItem.configTypeBool,
            false
        )
    val m3QuoteCard =
        addConfig(
            "M3QuoteCard",
            ConfigItem.configTypeBool,
            false
        )
    val m3TactileHaptics =
        addConfig(
            "M3TactileHaptics",
            ConfigItem.configTypeBool,
            false
        )
    val m3FloatingSearchBar =
        addConfig(
            "M3FloatingSearchBar",
            ConfigItem.configTypeBool,
            false
        )

    // Save Deleted Messages
    val enableSaveDeletedMessages =
        addConfig(
            "EnableSaveDeletedMessages",
            ConfigItem.configTypeBool,
            false
        )
    val enableSaveEditsHistory =
        addConfig(
            "EnableSaveEditsHistory",
            ConfigItem.configTypeBool,
            false
        )
    val saveLocalLastSeen =
        addConfig(
            "SaveLocalLastSeen",
            ConfigItem.configTypeBool,
            false
        )
    val attachmentFolderSizeLimitPreset =
        addConfig(
            "AttachmentFolderSizeLimitPreset",
            ConfigItem.configTypeInt,
            3
        )
    val attachmentFolderPath =
        addConfig(
            "AttachmentFolderPath",
            ConfigItem.configTypeString,
            ""
        )
    val saveDeletedMessageForBot =
        addConfig(
            "SaveDeletedMessageForBot",
            ConfigItem.configTypeBool,
            false
        )
    val saveDeletedMessageForBotUser =
        addConfig(
            "SaveDeletedMessageForBotUser",
            ConfigItem.configTypeBool,
            false
        )
    val saveToChatSubfolder =
        addConfig(
            "SaveToChatSubfolder",
            ConfigItem.configTypeBool,
            false
        )
    // Cherrygram Per-chat Biometric Lock
    val askBiometricsToOpenChats =
        addConfig(
            "AskBiometricsToOpenChats",
            ConfigItem.configTypeBool,
            false
        )
    val askBiometricsToOpenEncrypted =
        addConfig(
            "AskBiometricsToOpenEncrypted",
            ConfigItem.configTypeBool,
            false
        )
    val askBiometricsToOpenArchive =
        addConfig(
            "AskBiometricsToOpenArchive",
            ConfigItem.configTypeBool,
            false
        )
    val askPasscodeBeforeDelete =
        addConfig(
            "AskPasscodeBeforeDelete",
            ConfigItem.configTypeBool,
            false
        )
    val allowSystemPasscode =
        addConfig(
            "AllowSystemPasscode",
            ConfigItem.configTypeBool,
            true
        )
    val hideArchiveFromChatsList =
        addConfig(
            "HideArchiveFromChatsList",
            ConfigItem.configTypeBool,
            false
        )
    // Cherrygram Message Menu quick actions
    val showClearFromCache =
        addConfig(
            "ShowClearFromCache",
            ConfigItem.configTypeBool,
            true
        )
    val showForwardWithoutAuthor =
        addConfig(
            "ShowForwardWithoutAuthor",
            ConfigItem.configTypeBool,
            true
        )
    val showViewJSON =
        addConfig(
            "ShowViewJSON",
            ConfigItem.configTypeBool,
            false
        )
    val avatarCorners =
        addConfig(
            "avatarCorners",
            ConfigItem.configTypeFloat,
            28.0f
        )
    val singleCornerRadius =
        addConfig(
            "singleCornerRadius",
            ConfigItem.configTypeBool,
            false
        )
    val switchStyle =
        addConfig(
            "SwitchStyle",
            ConfigItem.configTypeInt,
            1
        )
    val sliderStyle =
        addConfig(
            "SliderStyle",
            ConfigItem.configTypeInt,
            2
        )
    val nowPlayingServiceType =
        addConfig(
            "NowPlayingServiceType",
            ConfigItem.configTypeInt,
            0
        )
    val nowPlayingLastFmUsername =
        addConfig(
            "NowPlayingLastFmUsername",
            ConfigItem.configTypeString,
            ""
        )
    val nowPlayingStatsFmUsername =
        addConfig(
            "NowPlayingStatsFmUsername",
            ConfigItem.configTypeString,
            ""
        )
    val replaceBlockedMyInfo =
        addConfig(
            "ReplaceBlockedMyInfo",
            ConfigItem.configTypeBool,
            false
        )
    val showFullAbout =
        addConfig(
            "ShowFullAbout",
            ConfigItem.configTypeBool,
            true
        )
    val builtInFolders =
        addConfig(
            "BuiltInFolders",
            ConfigItem.configTypeString,
            ""
        )
    private fun addConfig(
        k: String,
        t: Int,
        d: Any?
    ): ConfigItem {
        val a =
            ConfigItem(
                k,
                t,
                d
            )
        configs.add(
            a
        )
        return a
    }

    private fun addConfig(
        k: String,
        t: ConfigItem,
        d: Int,
        e: Any?
    ): ConfigItem {
        val a =
            ConfigItemKeyLinked(
                k,
                t,
                d,
                e,
            )
        configs.add(
            a
        )
        return a
    }

    fun loadConfig(
        force: Boolean
    ) {
        synchronized(
            sync
        ) {
            if (configLoaded && !force) {
                return
            }
            for (i in configs.indices) {
                val o =
                    configs[i]
                if (o.type == ConfigItem.configTypeBool) {
                    o.value =
                        preferences.getBoolean(
                            o.key,
                            o.defaultValue as Boolean
                        )
                }
                if (o.type == ConfigItem.configTypeInt) {
                    o.value =
                        preferences.getInt(
                            o.key,
                            o.defaultValue as Int
                        )
                }
                if (o.type == ConfigItem.configTypeLong) {
                    o.value =
                        preferences.getLong(
                            o.key,
                            (o.defaultValue as Long)
                        )
                }
                if (o.type == ConfigItem.configTypeFloat) {
                    o.value =
                        preferences.getFloat(
                            o.key,
                            (o.defaultValue as Float)
                        )
                }
                if (o.type == ConfigItem.configTypeString) {
                    o.value =
                        preferences.getString(
                            o.key,
                            o.defaultValue as String
                        )
                }
                if (o.type == ConfigItem.configTypeSetInt) {
                    val ss =
                        preferences.getStringSet(
                            o.key,
                            HashSet()
                        )
                    val si =
                        HashSet<Int>()
                    for (s in ss!!) {
                        si.add(
                            s.toInt()
                        )
                    }
                    o.value =
                        si
                }
                if (o.type == ConfigItem.configTypeMapIntInt) {
                    val cv =
                        preferences.getString(
                            o.key,
                            ""
                        )
                    // Log.e("NC", String.format("Getting pref %s val %s", o.key, cv));
                    if (cv!!.isEmpty()) {
                        o.value =
                            HashMap<Int, Int>()
                    } else {
                        try {
                            val data =
                                Base64.decode(
                                    cv,
                                    Base64.DEFAULT
                                )
                            val ois =
                                ObjectInputStream(
                                    ByteArrayInputStream(
                                        data
                                    )
                                )
                            o.value =
                                ois.readObject() as HashMap<*, *>
                            if (o.value == null) {
                                o.value =
                                    HashMap<Int, Int>()
                            }
                            ois.close()
                        } catch (e: Exception) {
                            o.value =
                                HashMap<Int, Int>()
                        }
                    }
                }
                if (o.type == ConfigItem.configTypeBoolLinkInt) {
                    o as ConfigItemKeyLinked
                    o.changedFromKeyLinked(o.keyLinked.Int())
                }
            }
            try {
                applyCustomGetQueryBlacklist()
            } catch (_: Exception) {}
            configLoaded =
                true
        }
    }

    init {
        loadConfig(
            false
        )
    }
}
