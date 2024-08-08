package zinc.doiche.service.user.listener

import com.velocitypowered.api.event.Continuation
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PreLoginEvent
import net.kyori.adventure.text.Component
import zinc.doiche.lib.ListenerRegistry
import zinc.doiche.service.user.UserService
import zinc.doiche.service.user.entity.User
import zinc.doiche.service.user.user

@ListenerRegistry
class UserIOListener {
    @Subscribe(async = true)
    fun onLogin(event: PreLoginEvent, continuation: Continuation) {
        val uuid = event.uniqueId ?: run {
            event.result = PreLoginEvent.PreLoginComponentResult.denied(Component.text("Data를 불러오는 데 실패했어요."))
            continuation.resume()
            return
        }

        if(event.result.isAllowed) {
            UserService.repository.runCatching {
                val user = findByUUID(uuid)?.apply {
                    period.update()
                } ?: User(uuid).apply {
                    transaction {
                        save(this@apply)
                    }
                }
                saveId(uuid, user.id!!)
                continuation.resume()
            }.onFailure {
                event.result = PreLoginEvent.PreLoginComponentResult.denied(Component.text("Data를 불러오는 데 실패했어요."))
                continuation.resume()
            }
        }
    }

    @Subscribe(async = true)
    fun onQuit(event: DisconnectEvent, continuation: Continuation) {
        val player = event.player
        val user = player.user ?: run {
            continuation.resume()
            return
        }

        UserService.repository.run {
            transaction {
                save(user)
                removeId(player.uniqueId)
            }
            continuation.resume()
        }
    }
}