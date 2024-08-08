package zinc.doiche.lib

// ===== Message injections

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TranslationRegistry

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Translatable(val key: String, val defaultValue: String = "", val defaultValues: Array<String> = [])

// ===== Command Registry

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandRegistry

/**
 * [com.velocitypowered.api.command.BrigadierCommand]를 리턴한다
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandFactory(val aliases: Array<String> = [])

//// ===== File Configurations
//
//@Target(AnnotationTarget.CLASS)
//@Retention(AnnotationRetention.RUNTIME)
//annotation class Configuration(val directory: String = "")
//
//@Target(AnnotationTarget.FUNCTION)
//@Retention(AnnotationRetention.RUNTIME)
//annotation class Read(val path: String)
//
// ===== Misc Annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ListenerRegistry

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Priority(val value: Int)