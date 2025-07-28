## Installation

### Maven

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.Weesli.RClaim</groupId>
    <artifactId>RClaim-api</artifactId>
    <version>{version}</version>
    <scope>provided</scope>
</dependency>

```

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

compileOnly("com.github.Weesli.RClaim:RClaim-api:{version}")

```