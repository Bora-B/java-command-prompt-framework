package commandprompt;

public class CommandPromptApp {
    public static void main(String[] args)
    {
        var commandPrompt = new CommandPrompt.Builder().register(new CommandRegister())
                .setStartupMessage("Type HELP to see all commands.\nFor more information on a spesific command, type HELP command-name\n")
                .build();

        commandPrompt.run();
    }
}
