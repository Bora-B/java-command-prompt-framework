package commandprompt;

import commandprompt.annotation.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CommandRegister {
    private CommandPrompt m_prompt;

    private void helpCallback(CommandPrompt.CommandInfo ci)
    {
        System.out.println(ci.getDescription()); System.out.println(ci.getUsage());
        System.out.println();
    }

    @Command(name = "help", description = "Shows information on a spesific command.", usage = "HELP command-name")
    private void help(String command)
    {
        m_prompt.getCommandInfos().stream().filter(ci -> ci.getName().equals(command))
                .forEach(this::helpCallback);
    }

    public void setCommandPrompt(CommandPrompt prompt)
    {
        m_prompt = prompt;
    }

    @Command(name = "help", description = "Shows all commands.", usage = "HELP")
    private void help()
    {
        for (var cmd : m_prompt.getCommandInfos())
            System.out.printf("%-20s%s\n", cmd.getName(), cmd.getDescription());
    }

    @Command(description = "Clears the screen.", usage = "CLEAR")
    private void clear()
    {
        System.out.println("cls");
    }

    @Command
    private void list()
    {
        list(".");
    }

    @Command(name = "ls")
    @Command
    private void list(String strPath)
    {
        Path path = Path.of(strPath);

        if (Files.exists(path))
            if (Files.isDirectory(path)) {
                File file1 = new File(strPath);
                File[] files = file1.listFiles();

                for (File file : files)
                    System.out.printf("%s %s\n", file.getName(), file.isFile() ? file.length() : "<DIR>");
            }

    }

    @Command(description = "Copies a file.", usage = "COPY src dest")
    private void copy(String src, String dest)
    {
        Path sourcePath = Paths.get(src);
        Path destinationPath = Paths.get(dest);

        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied from " + src + " to " + dest);
        } catch (IOException e) {
            System.err.println("Failed to copy file: " + e.getMessage());
        }
    }
}
