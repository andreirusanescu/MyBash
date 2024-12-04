package bash;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public final class Bash {
    private Path currentDirectory;
    private final StringBuffer history;
    private final CommandPublisher publisher;
    private static final String EXIT = "exit";
    private final String userName = System.getProperty("user.name");

    public Bash() {
        // TODO 2 Initialize history and currentDirectory;
         history = new StringBuffer();
         currentDirectory = Paths.get(System.getProperty("user.dir"));

        // TODO 2 Instantiate a new command publisher
         publisher = new BashCommandPublisher();

        // TODO 4 & 5 & 6 & 7
        // CommandSubscribers know how to execute the commands.
        // Subscribe some to the Command publisher.
        BashUtils.Echo echo = new BashUtils.Echo();
        BashUtils.Cd cd = new BashUtils.Cd();
        BashUtils.Ls ls = new BashUtils.Ls();
        BashUtils.History historyCMD = new BashUtils.History();
        publisher.subscribe(echo);
        publisher.subscribe(cd);
        publisher.subscribe(ls);
        publisher.subscribe(historyCMD);
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(userName + "@myBash:" + getCurrentDirectory() + "$ ");
            // TODO 3 Read commands from the command line
            String input = scanner.nextLine();

            // TODO 3 If we read the "exit" string then we should stop the program.
            if (input.equals(EXIT)) {
                break;
            }

            // TODO 3 Create an anonymous class which extends Thread.
            // It has to implement the 'run' method. From the 'run' method publish the command
            // so that the CommandSubscribers start executing it.
            // Don't forget to start the thread by calling the 'start' method on it!
            Thread t = new Thread() {
                public void run() {
                    synchronized (publisher) {
                        publisher.publish(new Command(input, Bash.this));
                    }
                    synchronized (history) {
                        history.append(input).append(" | ");
                    }
                }
            };
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        scanner.close();
    }

    // TODO 1: Create an inner class which implements the CommandPublisher interface.
    // 1. The class should contain an ArrayList of CommandSubscribers
    // 2. The class should implement the subscribe and publish methods.
    private class BashCommandPublisher implements CommandPublisher {
        private ArrayList<CommandSubscriber> subscribers;
        public BashCommandPublisher() {
            subscribers = new ArrayList<>();
        }
        @Override
        public void subscribe(CommandSubscriber subscriber) {
            subscribers.add(subscriber);
        }

        @Override
        public void publish(Command command) {
            for (CommandSubscriber subscriber : subscribers) {
                subscriber.executeCommand(command);
            }
        }
    }

    public Path getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(Path currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public StringBuffer getHistory() {
        return history;
    }
}
