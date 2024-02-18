# Type Explorer

Explore the types of your Scala code in a visual way.

# Install
Download the latest release from [here](https://github.com/juanpablo-r/type-explorer/releases).

Unzip the file and add `type-explorer-$version/bin` to your `$PATH`.

There are two files in the `bin` folder:
- `type-explorer`: The main executable
- `type-explorer-compile-project.sh`: A script to compile your project with semanticdb enabled.

## Requirements
- JVM 8 or higher
- SBT based project

# Compile your code
`type-explorer-compile-project.sh` will compile the project in the current directory. 

```bash
cd akka-persistence-jdbc
type-explorer-compile-project.sh
```

To compile a single module:

```bash
type-explorer-compile-project.sh module-name
```

# Run

```bash
❯ type-explorer
--------------------------------------------------
Welcome to Type Explorer!
name: type-explorer-shared, version: 0.2.0, scalaVersion: 3.3.1, sbtVersion: 1.9.6
Open your browser at http://localhost:8090/
Press Ctrl-C to stop the server
--------------------------------------------------
```

# Examples

Scala standard library.

```bash
❯ cd scala

❯ pwd
/Users/jpablo/GitHub/scala

❯ type-explorer-compile-project.sh

❯ find . -name "*.semanticdb" | wc -l
    1215
```

Initial screen

<img src="/Users/jpablo/proyectos/playground/type-explorer/docs/screenshots/initial-screen.png" alt="initial-screen" style="zoom:50%;" />

Select folder containing semanticdb files

<img src="/Users/jpablo/proyectos/playground/type-explorer/docs/screenshots/base-path.png" alt="base-path" style="zoom:50%;" />

Add a type to the diagram

<img src="/Users/jpablo/proyectos/playground/type-explorer/docs/screenshots/select-type.png" alt="select-type" style="zoom:50%;" />

Add all parents

<img src="/Users/jpablo/proyectos/playground/type-explorer/docs/screenshots/inheritance-diagram.png" alt="inheritance-diagram" style="zoom:50%;" />

# License

[License](./LICENSE)

Copyright 2024 Juan Pablo Romero and the type-explorer contributors.
