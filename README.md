# superscalar-processor

A simulation of a Superscalar Processor

![Schematic](schematic.png)

## Usage

This project is written in Java and built using Apache Ant.

- Check Apache Ant is installed:
  ```
  $ ant -version
  Apache Ant(TM) version 1.10.6 compiled on May 2 2019
  ```
  If not, follow [these instructions](http://ant.apache.org/manual/install.html), or install with your package manager.
- The `$ ant all` command (or simply `$ ant`) will build and run the project. For fine-grained interaction,
  - `$ ant clean` cleans the build directory,
  - `$ ant build` compiles the source tree files,
  - `$ ant build-jar` compiles a JAR from built class files,
  - `$ ant run` runs the project JAR:
    - Specify a custom program file: `$ ant run -Dpf=programs/some-program.asm`.
    - Run in interactive mode: `$ ant run -Dpf=programs/some-program.asm -Di=true`.
    - Omission of the `run` parameter will trigger a build before execution.
- Alternatively, once built, you can interact with the JAR directly:
  - `$ java -jar superscalar-processor.jar` to run the default program, or
  - `$ java -jar superscalar-processor.jar programs/some-program.asm` to specify a custom program file, or
  - `$ java -jar superscalar-processor.jar programs/some-program.asm true` run in interactive mode.


## Built with

- [Java](http://www.java.com/) - Project language
- [Apache Ant](https://ant.apache.org/) - Automates the project build process

## License

[MIT license](./LICENSE)