package tidinari.mbpunish.recognize

import tidinari.mbpunish.recognize.patterns.*

enum class Patterns(val messagePattern: MessagePattern) {
    AFK(AfkPattern()),
    BROADCAST(BroadcastPattern()),
    CHAT(ChatPattern()),
    COREPROTECTOR(CoreprotectorPattern()),
    JAIL(JailPattern()),
    ME(MePattern()),
    PRIVATE(PrivatePattern()),
    PUNISH(ModerCommandPattern()),
    NEAR(NearPattern()),
    PUNISHED(PunishedPattern()),
    REALNAME(RealnamePattern()),
    SOCIALSPY(SocialspyPattern())
}