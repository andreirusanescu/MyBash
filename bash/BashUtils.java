package bash;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

enum Commands {
    CD("cd"),
    LS("ls"),
    ECHO("echo"),
    HISTORY("history");

    private final String text;

    Commands(final String newText) {
        this.text = newText;
    }

    @Override
    public String toString() {
        return text;
    }
}

public class BashUtils {

    // Implement some inner classes here: Echo, Cd, Ls, History

    // example: class Cd { ... }

    // Decide if they should be static or non-static.

    // TODO 4 Create Echo class
    public static class Echo implements CommandSubscriber {

        @Override
        public void executeCommand(Command c) {
            if (c.getCommand().startsWith("echo")) {
                System.out.println(c.getCommand().substring("echo ".length()));
            }
        }
    }

    // TODO 5 Create Cd class
    public static class Cd implements CommandSubscriber {
        private Path oldPath = null;
        @Override
        public void executeCommand(Command c) {
            if (c.getCommand().startsWith("cd")) {
                Path newPath = null;
                String path = c.getCommand().substring("cd ".length());
                if (path.isEmpty() || path.equals("~")) {
                    newPath = Paths.get(System.getProperty("user.home"));
                } else if (path.equals(".")) {
                   newPath = c.getBash().getCurrentDirectory();
                } else if (path.equals("-")) {
                    if (oldPath == null) {
                        System.out.println("OLDPWD not set");
                        return;
                    }
                    Path aux = c.getBash().getCurrentDirectory();
                    c.getBash().setCurrentDirectory(oldPath);
                    oldPath = aux;
                    return;
                } else {
                    newPath = c.getBash().getCurrentDirectory().resolve(Paths.get(path)).normalize();
                }

                if (Files.exists(newPath) && Files.isDirectory(newPath)) {
                    oldPath = c.getBash().getCurrentDirectory();
                    c.getBash().setCurrentDirectory(newPath);
                } else if (!Files.exists(newPath) && path.startsWith("..")) {
                    oldPath = c.getBash().getCurrentDirectory();
                    c.getBash().setCurrentDirectory(newPath.getRoot());
                } else {
                    System.out.println("Invalid directory: " + path);
                }
            }
        }
    }

    // TODO 6 Create the Ls class

    public static class Ls implements CommandSubscriber {

        private void listFiles(final File folder) {
            for (File f : folder.listFiles()) {
                System.out.print(f.getName());
                if (f.isDirectory()) {
                    System.out.println("\\");
                } else {
                    System.out.println();
                }
            }
        }

        @Override
        public void executeCommand(Command c) {
            String command = c.getCommand().trim();
            if (command.equals("ls")) {
                File folder = c.getBash().getCurrentDirectory().toFile();
                listFiles(folder);
            }
        }
    }

    // TODO 7 Create the History class

    public static class History implements CommandSubscriber {
        @Override
        public void executeCommand(Command c) {
            if (c.getCommand().startsWith("history")) {
                System.out.println(c.getBash().getHistory());
            }
        }
    }
}
