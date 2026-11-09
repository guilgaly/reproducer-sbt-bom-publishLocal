# sbt 2: `publishLocal` keeps `*` versions provided by a BOM

`projectA` imports `netty-bom` with `.pomOnly()` and depends on `netty-handler` with version `*`.
`projectB` depends on `projectA`.

## Steps

```sh
cd projectA && sbt publishLocal
cd ../projectB && sbt update
```

## Expected

`projectB` resolves `netty-handler` `4.2.20.Final`, the version from `netty-bom`.

[sbt/sbt#8675](https://github.com/sbt/sbt/pull/8675), which added BOM support, describes it under "Ivy / publishLocal emulation":
the versions resolved from the BOM (dependencies with `"*"`) are written into the published `ivy.xml` as forced dependencies (`force="true"`).

## Actual

The published `ivy.xml` keeps the `*` version:

```xml
<dependency org="io.netty" name="netty-bom" rev="4.2.20.Final" conf="compile->default(runtime)"> </dependency>
<dependency org="io.netty" name="netty-handler" rev="*" conf="compile->default(runtime)"> </dependency>
```

so `projectB` tries to download version `*`, and fails after about a minute of retries:

```
[error] (update) sbt.librarymanagement.ResolveException: Error downloading io.netty:netty-handler:*
[error]   Not found
[error]   Not found
[error]   not found: ~/.ivy2/local/io.netty/netty-handler/*/ivys/ivy.xml
[error]   retryable HTTP error: https://repo1.maven.org/maven2/io/netty/netty-handler/*/netty-handler-*.pom (HTTP 429)
```

With `publishM2` instead, and `resolvers += Resolver.mavenLocal` in `projectB`, `netty-handler` resolves to `4.2.20.Final`:
the published pom imports `netty-bom` in `<dependencyManagement>`.
