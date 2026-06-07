# NinjaInvs

`NinjaInvs` is a Paper inventory library that is embedded into another plugin.
It is not meant to be installed as a standalone server plugin.

## Compatibility

- Compiled against `paper-api:1.21.11-R0.1-SNAPSHOT`
- Intended for `1.21.11+` servers where the used Paper inventory API stays compatible

Future major Minecraft or Paper API changes can still require adjustments, so this cannot be guaranteed for every version forever.

## Add via GitHub

The simplest distribution path is GitHub + JitPack.

Artifact coordinates:

- Group: `com.github.<github-user-or-org>`
- Artifact: `NinjaInvs`
- Version: your Git tag, for example `v1.0.0`

```kotlin
repositories {
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation("com.github.<github-user-or-org>:NinjaInvs:v1.0.0")
}
```

## Initialize in your plugin

Initialize the manager once in your plugin's `onEnable()`:

```java
public final class MyPlugin extends JavaPlugin {
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        inventoryManager = NinjaInvs.init(this);
    }
}
```

## Create inventories

You can either pass the initialized manager explicitly:

```java
NinjaInventory inventory = NinjaInventory.builder()
        .id("example")
        .manager(inventoryManager)
        .provider(new ExampleProvider())
        .build();
```

Or let the library resolve the manager from your host plugin:

```java
NinjaInventory inventory = NinjaInventory.builder()
        .id("example")
        .plugin(this)
        .provider(new ExampleProvider())
        .build();
```
