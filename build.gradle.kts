plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.jsoup:jsoup:1.22.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")

    //telegram bots
    //ficar ouvindo
    implementation("org.telegram:telegrambots-longpolling:9.2.0")

    //enviar mensagens
    implementation("org.telegram:telegrambots-client:9.2.0")

    //para requisicao já que o android não usa o java completo
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

tasks.test {
    useJUnitPlatform()
}