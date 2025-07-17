package commandprompt;

import commandprompt.annotation.Command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class CommandPrompt {
    private Object m_registerObject;
    private final ArrayList<CommandInfo> m_commandInfo = new ArrayList<>();
    private String m_prompt = "shell";
    private String m_suffix = ">";
    private String m_parameterStringTypeErrorMessage = "Message parameters must be string!";
    private String m_wrongNumberOfArgumentsMessage = "Wrong number of arguments!";
    private String m_invalidCommandMessage = "Invalid command! Type help to see all commands.";
    private String m_startupMessage = "";

    private CommandPrompt()
    {
    }

    static class CommandInfo {
        private final String m_name;
        private final String m_description;
        private final String m_usage;
        private final Method m_method;
        private final int m_numberOfParameters;

        public CommandInfo(String name, String description, String usage, Method method, int numberOfParameters) {
            m_name = name;
            m_description = description;
            m_usage = usage;
            m_method = method;
            m_numberOfParameters = numberOfParameters;
        }

        public String getName() {
            return m_name;
        }

        public String getDescription() {
            return m_description;
        }

        public String getUsage() {
            return m_usage;
        }
    }

    public static class Builder {
        private final CommandPrompt m_commandPrompt = new CommandPrompt();

        public Builder setSuffix(String suffix)
        {
            m_commandPrompt.m_suffix = suffix;
            return this;
        }

        public Builder setPrompt(String prompt)
        {
            m_commandPrompt.m_prompt = prompt;
            return this;
        }

        public Builder setParameterStringTypeErrorMessage(String parameterStringTypeErrorMessage)
        {
            m_commandPrompt.m_parameterStringTypeErrorMessage = parameterStringTypeErrorMessage;
            return this;
        }

        public Builder setWrongNumberOfArgumentsMessage(String wrongNumberOfArgumentsMessage)
        {
            m_commandPrompt.m_wrongNumberOfArgumentsMessage = wrongNumberOfArgumentsMessage;
            return this;
        }

        public Builder setInvalidCommandMessage(String invalidCommandMessage)
        {
            m_commandPrompt.m_invalidCommandMessage = invalidCommandMessage;
            return this;
        }

        public Builder setStartupMessage(String startupMessage)
        {
            m_commandPrompt.m_startupMessage = startupMessage;
            return this;
        }

        public Builder register(Object registerObject)
        {
            m_commandPrompt.registerObject(registerObject);
            return this;
        }

        public CommandPrompt build()
        {
            return m_commandPrompt;
        }
    }

    private boolean areStringParameters(Parameter[] parameters)
    {
        for (Parameter parameter : parameters)
            if (parameter.getType() != String.class)
                return false;

        return true;
    }

    private void registerCommand(Command[] commands, Method method)
    {
        String name;
        String description;
        String usage;
        Parameter[] parameters;

        for (var command : commands) {
            name = (command.name().isBlank() ? method.getName() : command.name()).toUpperCase(Locale.ENGLISH);
            description = command.description();
            usage = command.usage();

            parameters = method.getParameters();

            if (!areStringParameters(parameters))
                throw new IllegalArgumentException(m_parameterStringTypeErrorMessage);

            m_commandInfo.add(new CommandInfo(name, description, usage, method, parameters.length));
        }
    }

    public void registerObject(Object registerObject)
    {
        try {
            var method = registerObject.getClass().getMethod("setCommandPrompt", CommandPrompt.class);
            method.setAccessible(true);
            method.invoke(registerObject, this);
        }
        catch (NoSuchMethodException ignore) {

        }
        catch (Exception e) {
            throw new RuntimeException("Failed to inject CommandPrompt into registered object", e);
        }

        m_registerObject = registerObject;

        var classRegisterObject = registerObject.getClass();
        var methods = classRegisterObject.getDeclaredMethods();

        for (var method : methods) {
            var commands = method.getDeclaredAnnotationsByType(Command.class);

            if (commands.length == 0)
                continue;

            registerCommand(commands, method);
        }
    }

    private void runCommands(String[] args) throws InvocationTargetException, IllegalAccessException
    {
        String[] parameters = Arrays.copyOfRange(args, 1, args.length);
        boolean commandFound = false;
        boolean argsMatched = false;

        for (var commandInfo : m_commandInfo) {
            if (commandInfo.m_name.equals(args[0])) {
                commandFound = true;
                argsMatched = true;


                if (commandInfo.m_numberOfParameters != parameters.length) {
                    argsMatched = false;
                    continue;
                }

                commandInfo.m_method.setAccessible(true);
                commandInfo.m_method.invoke(m_registerObject, (Object[]) parameters);
                commandInfo.m_method.setAccessible(false);
                break;
            }
        }

            if (!commandFound)
                System.out.println(m_invalidCommandMessage);
            else if (!argsMatched)
                System.out.println(m_wrongNumberOfArgumentsMessage);

    }
    public void run()
    {
        System.out.print(m_startupMessage);
        Scanner kb = new Scanner(System.in);

        try {
            while (true) {
                System.out.print(m_prompt + m_suffix);
                String command = kb.nextLine().toUpperCase();

                if (command.isBlank())
                    continue;

                runCommands(command.split("[ \t]+"));
            }
        }
        catch(InvocationTargetException | IllegalAccessException e){
            throw new RuntimeException(e);
        }

    }

    public List<CommandInfo> getCommandInfos() {
        return m_commandInfo;
    }
}
