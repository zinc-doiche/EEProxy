import org.hidetake.groovy.ssh.connection.AllowAnyHosts
import org.hidetake.groovy.ssh.core.Remote
import org.hidetake.groovy.ssh.core.RunHandler
import org.hidetake.groovy.ssh.session.SessionHandler
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

plugins {
    val kotlinVersion = "2.0.0"

    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    kotlin("plugin.noarg") version kotlinVersion

    id("org.hidetake.ssh") version "2.11.2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "zinc.doiche"
version = "1.0-SNAPSHOT"

val server = Remote(
    mapOf<String, Any>(
        "host" to project.property("host") as String,
        "port" to (project.property("port") as String).toInt(),
        "user" to project.property("user") as String,
        "password" to project.property("password") as String,
        "knownHosts" to AllowAnyHosts.instance
    )
)

with(extensions) {
    configure(AllOpenExtension::class) {
        annotation("jakarta.persistence.Entity")
        annotation("jakarta.persistence.Embeddable")
        annotation("jakarta.persistence.MappedSuperclass")
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    compileOnly("com.google.code.gson:gson:2.10.1")
    implementation("org.reflections:reflections:0.9.12")

    implementation("redis.clients:jedis:5.1.2")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.hibernate:hibernate-core:6.5.0.Final") {
        exclude(group = "cglib", module = "cglib")
        exclude(group = "asm", module = "asm")
    }
    implementation("org.hibernate:hibernate-jcache:6.5.0.Final")
    implementation("org.ehcache:ehcache:3.10.0")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.querydsl:querydsl-core:5.0.0")
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    implementation("com.querydsl:querydsl-apt:5.0.0:jakarta")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    kapt("jakarta.persistence:jakarta.persistence-api:3.1.0")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
}

kotlin {
    jvmToolchain(21)
}

tasks.create(name = "deploy") {
    dependsOn("shadowJar")

    doLast {
        ssh.run(delegateClosureOf<RunHandler> {
            session(server, delegateClosureOf<SessionHandler> {
                val file = "$projectDir/build/libs/${project.name}-${project.version}-all.jar"
                val directory = "/home/minecraft/${rootProject.name}/${project.name}/plugins"

                put(
                    hashMapOf(
                        "from" to file,
                        "into" to directory
                    )
                )
            })
        })
    }
}