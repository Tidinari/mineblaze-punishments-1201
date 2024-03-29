package tidinari.mbpunish.recognize

import tidinari.mbpunish.recognize.patterns.*

enum class Patterns(val messagePattern: MessagePattern) {
    CHAT(ChatPattern()),
    SOCIALSPY(SocialspyPattern()),
    CHATGAME(ChatGamePattern()),
    AFK(AfkPattern()),
    JOIN(JoinPattern()),
    ANTICHEAT(AntiCheatPattern()),
    BROADCAST(BroadcastPattern()),
    COREPROTECTOR(CoreprotectorPattern()),
    HISTORY(HistoryPattern()),
    ADMINCHAT(AdminChatPattern()),
    ME(MePattern()),
    PRIVATE(PrivatePattern()),
    PUNISH(ModerCommandPattern()),
    PUNISHED(PunishedPattern()),
    REALNAME(RealnamePattern()),
    JAIL(JailPattern()),
    CLEARCHAT(ClearChatPattern()),
    SAY(SayPattern())
}