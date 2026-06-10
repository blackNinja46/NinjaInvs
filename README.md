# NinjaInvs

Reusable inventory library for Paper `1.21.11+`.

## Installation

```kotlin
plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.6"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation("com.github.<github-user-or-org>:NinjaInvs:v1.0.0")
}
```

Build and use the shaded plugin jar.

## Usage

```java
public final class MyPlugin extends JavaPlugin {
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        inventoryManager = NinjaInvs.init(this);
    }
}
```

```java
NinjaInventory inventory = NinjaInventory.builder()
        .id("menu")
        .miniMessageTitle("<gradient:#00d2ff:#3a7bd5><bold>Ninja Menu</bold></gradient>")
        .manager(inventoryManager)
        .provider(new ExampleProvider())
        .build();
```
