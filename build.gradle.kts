plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"  // Adicione esta linha
}

group = "org.example"
version = "1.0.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.17")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.17")

    implementation("org.jsoup:jsoup:1.22.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")

    //telegram bots
    //ficar ouvindo
    implementation("org.telegram:telegrambots-longpolling:9.2.0")

    //enviar mensagens
    implementation("org.telegram:telegrambots-client:9.2.0")
}

tasks.test {
    useJUnitPlatform()
}

// Configurar o shadowJar para definir a classe principal
tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "org.example.Main"  // Altere para sua classe principal
    }
    archiveBaseName.set("arsenal-soup")
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())
}

tasks.build {
    dependsOn(tasks.shadowJar)
}