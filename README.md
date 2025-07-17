# CommandPrompt

A lightweight and extensible Java framework for building command-line interfaces using annotations.  
Easily bind methods to commands and run them interactively from a customizable prompt.

📄 This README is also available in [🇹🇷 Turkish](README.tr.md)

---

## ✨ Features

- Annotation-based command registration (`@Command`, `@Commands`)
- Support for multiple aliases per command
- Helpful error messages for unknown commands or argument mismatches
- Builder-based configuration (prompt text, suffix, error messages)
- Uses Java Reflection to collect and invoke command methods at runtime
---

## 🚀 Getting Started

### 1. Create your command class
> If you annotate a method with `@Command` and leave the value element empty, the command name will default to the method name. Write the operations as void methods and annotate them with @Command. If a method is annotated with multiple `@Commands`, each name can be used to trigger the method.

```java
import commandprompt.annotation.Command;
import commandprompt.annotation.Commands;

public class MyCommands {

    @Commands({@Command("hello"), @Command("hi")})
    private void greet() {
        System.out.println("Hello!");
    }

    @Command("echo")
    private void echo(String message) {
        System.out.println(message);
    }

    @Command("help")
    private void help() {
        System.out.println("Try: hello, echo, help");
    }
}
```

### 2. Register it and run the prompt
> Create your CommandPrompt instance as shown below, and pass an instance of the class where you defined your commands to the register method.
```java
import commandprompt.CommandPrompt;

public class Main {
    public static void main(String[] args) {
        CommandPrompt prompt = new CommandPrompt.Builder()
            .setPrompt("cli")
            .setSuffix(">")
            .register(new MyCommands())
            .build();

        prompt.run();
    }
}
```

## ⚙️ Customization

You can configure the prompt using the builder:

| Method                                      | Purpose                            |
|---------------------------------------------|------------------------------------|
| `setPrompt(String prompt)`                  | Sets prompt prefix                 |
| `setSuffix(String suffix)`                  | Sets prompt suffix                 |
| `setWrongNumberOfArgumentsMessage(...)`     | Error message for wrong args      |
| `setInvalidCommandMessage(...)`             | Error message for bad command     |
| `setParameterStringTypeErrorMessage(...)`   | Message for type mismatch         |

## 🛠 Under the Hood

This project uses Java Reflection API to:
- Scan registered objects for methods annotated with `@Command`.
- Extract command names and parameter information.
- Dynamically invoke methods when a matching command is entered.
