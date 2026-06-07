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

If you publish under `BlackNinjaLive`, the dependency would look like this:

```kotlin
dependencies {
    implementation("com.github.BlackNinjaLive:NinjaInvs:v1.0.0")
}
```

## Release workflow

Recommended versioning:

- `v1.0.0` for the first stable release
- `v1.0.1`, `v1.0.2` for bugfixes
- `v1.1.0` for new backwards-compatible features
- `v2.0.0` only if you introduce breaking API changes

Recommended GitHub flow:

1. Push the repository to GitHub.
2. Create a Git tag such as `v1.0.0`.
3. Push the tag to GitHub.
4. Use that tag in the dependency string of other plugins.

Example commands:

```bash
git tag v1.0.0
git push origin v1.0.0
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

## Example host plugin setup

```kotlin
plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation("com.github.<github-user-or-org>:NinjaInvs:v1.0.0")
}
```

If your final plugin jars should not expose the library as a separate server plugin, that is already the case here:

- `NinjaInvs` no longer ships a Paper plugin entrypoint
- there is no `paper-plugin.yml`
- the library is meant to be bundled into the consuming plugin
