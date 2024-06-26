import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Shell {
    private List<String> commandHistory; // Liste pour stocker l'historique des commandes
    private BufferedReader reader; // BufferedReader pour lire les entrées utilisateur

    // Constructeur de la classe Shell
    public Shell() {
        commandHistory = new ArrayList<>(); // Initialiser la liste de l'historique des commandes
        reader = new BufferedReader(new InputStreamReader(System.in)); // Initialiser le lecteur d'entrée
    }

    // Méthode pour démarrer le shell
    public void start() {
        System.out.println("\u001B[36mWelcome to MiniShell!\u001B[0m"); // Message de bienvenue en cyan
        while (true) { // Boucle infinie pour le shell
            System.out.print("\u001B[32mminishell>\u001B[0m "); // Prompt de commande en vert
            try {
                String commandLine = reader.readLine().trim(); // Lire la commande utilisateur
                if (commandLine.isEmpty()) { // Si la commande est vide, continuer
                    continue;
                }
                commandHistory.add(commandLine); // Ajouter la commande à l'historique
                if (commandLine.equalsIgnoreCase("exit")) { // Condition de sortie
                    System.out.println("Exiting MiniShell...");
                    break; // Sortir de la boucle
                } else if (commandLine.equalsIgnoreCase("history")) { // Commande pour afficher l'historique
                    printHistory(); // Appeler la méthode pour afficher l'historique
                } else if (commandLine.startsWith("run ")) { // Vérifier si la commande est pour exécuter un script
                    String scriptFileName = commandLine.substring(4).trim(); // Extraire le nom du fichier script
                    File scriptFile = new File(scriptFileName); // Créer un objet File pour le script
                    if (!scriptFile.exists()) { // Vérifier si le fichier n'existe pas
                        String currentDirectory = System.getProperty("user.dir"); // Obtenir le répertoire de travail actuel
                        scriptFile = new File(currentDirectory, scriptFileName); // Rechercher le fichier dans le répertoire actuel
                    }
                    if (!scriptFile.exists()) { // Si le fichier n'existe toujours pas
                        System.out.println("Error: Script file not found: " + scriptFileName); // Afficher une erreur
                    } else {
                        String scriptFilePath = scriptFile.getAbsolutePath(); // Obtenir le chemin absolu du fichier

                        System.out.println("Executing script file: " + scriptFilePath); // Message de débogage

                        executeScript(scriptFilePath); // Exécuter le script
                    }
                } else { // Sinon, exécuter la commande
                    executeCommand(commandLine); // Appeler la méthode pour exécuter la commande
                }

            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage()); // Gérer les erreurs d'entrée/sortie
            }
        }
    }

    // Méthode pour exécuter une commande
    private void executeCommand(String commandLine) {
        List<String> tokens = parseCommand(commandLine); // Analyser la commande en tokens

        if (tokens.isEmpty()) { // Si aucun token, retourner
            return;
        }

        String command = tokens.get(0); // Obtenir le premier token comme commande
        tokens.remove(0); // Supprimer le premier token
        String[] args = tokens.toArray(new String[0]); // Convertir les tokens restants en tableau

        // Vérifier les opérateurs de redirection
        if (commandLine.contains(">>")) {
            executeAppendRedirect(commandLine);
        } else if (commandLine.contains(">")) {
            executeOutputRedirect(commandLine);
        } else if (commandLine.contains("<")) {
            executeInputRedirect(commandLine);
        } else { // Si aucune redirection
            switch (command) { // Vérifier la commande
                case "cd":
                    Cd_PWD_Ls.cd(args); // Appeler la méthode cd
                    break;
                case "pwd":
                    Cd_PWD_Ls.pwd(); // Appeler la méthode pwd
                    break;
                case "ls":
                    Cd_PWD_Ls.ls(args); // Appeler la méthode ls
                    break;
                case "echo":
                    EchoCatNano.echo(String.join(" ", args)); // Appeler la méthode echo
                    break;
                case "cat":
                    try {
                        EchoCatNano.cat(args[0]); // Appeler la méthode cat
                    } catch (IOException e) {
                        System.err.println("Error executing cat command: " + e.getMessage()); // Gérer les erreurs
                    }
                    break;
                case "nano":
                    EchoCatNano.nano(args[0]); // Appeler la méthode nano
                    break;
                case "mkdir":
                    MkdirTouch.mkdir(args); // Appeler mkdir
                    break;
                case "touch":
                    MkdirTouch.touch(args); // Appeler touch
                    break;
                case "ps":
                    System.out.println(ps()); // Appeler la méthode ps
                    break;
                default:
                    System.out.println("Command not found"); // Si la commande n'est pas trouvée
                    break;
            }
        }
    }

    // Méthode pour exécuter la commande ps
    public static String ps() {
        StringBuilder result = new StringBuilder(); // Crée un StringBuilder pour stocker la sortie de la commande
        ProcessBuilder processBuilder = new ProcessBuilder(); // Crée un ProcessBuilder pour exécuter des commandes système
        processBuilder.command("bash", "-c", "ps"); // Définit la commande à exécuter (ici, la commande ps via bash)

        try {
            Process process = processBuilder.start(); // Démarre le processus
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); // Crée un BufferedReader pour lire la sortie du processus

            String line;
            while ((line = reader.readLine()) != null) { // Lit chaque ligne de la sortie du processus
                result.append(line).append("\n"); // Ajoute chaque ligne à la chaîne résultante
            }

            int exitCode = process.waitFor(); // Attend la fin du processus et récupère le code de sortie
            if (exitCode != 0) { // Vérifie si le code de sortie indique une erreur
                throw new RuntimeException("Erreur dans l'exécution de la commande ps. Code de sortie: " + exitCode); // Lève une exception en cas d'erreur
            }

        } catch (IOException | InterruptedException e) { // Gère les exceptions IOException et InterruptedException
            e.printStackTrace(); // Imprime la trace de l'exception pour le débogage
        }

        return result.toString(); // Retourne la chaîne résultante contenant la sortie de la commande ps
    }

    // Méthode pour exécuter un script
    private void executeScript(String scriptFileName) {
        File scriptFile = new File(scriptFileName); // Créer un objet File pour le script
        if (!scriptFile.exists()) { // Vérifier si le fichier n'existe pas
            System.err.println("Error: Script file not found: " + scriptFileName); // Afficher une erreur
            return;
        }
        if (!scriptFile.canExecute()) { // Vérifier si le fichier n'a pas les droits d'exécution
            try {
                Process chmodProcess = new ProcessBuilder("chmod", "+x", scriptFileName) // Donner les droits d'exécution
                        .directory(new File(System.getProperty("user.dir"))) // Définir le répertoire de travail
                        .start(); // Démarrer le processus
                int chmodExitCode = chmodProcess.waitFor(); // Attendre la fin du processus
                if (chmodExitCode != 0) {
                    System.err.println("Error: Failed to set execute permissions for script file: " + scriptFileName); // Afficher une erreur
                    return;
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Error setting execute permissions: " + e.getMessage()); // Gérer les erreurs
                return;
            }
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", scriptFileName); // Créer un processus pour exécuter le script

            processBuilder.directory(new File(System.getProperty("user.dir"))); // Définir le répertoire de travail

            Process process = processBuilder.start(); // Démarrer le processus

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); // Lire la sortie du processus
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Afficher chaque ligne de la sortie
            }
            int exitCode = process.waitFor(); // Attendre la fin du processus
            if (exitCode != 0) {
                System.err.println("Error: Script execution failed with exit code " + exitCode); // Afficher une erreur
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing script: " + e.getMessage()); // Gérer les erreurs
        }
    }

    // Méthode pour analyser une commande
    private List<String> parseCommand(String commandLine) {
        List<String> tokens = new ArrayList<>(); // Initialiser la liste des tokens
        StringBuilder currentToken = new StringBuilder(); // StringBuilder pour construire chaque token
        boolean insideQuotes = false; // Pour gérer les guillemets

        for (int i = 0; i < commandLine.length(); i++) {
            char c = commandLine.charAt(i);

            if (c == '"') {
                insideQuotes = !insideQuotes; // Basculer l'état des guillemets
            } else if (c == ' ' && !insideQuotes) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString()); // Ajouter le token à la liste des tokens
                    currentToken.setLength(0); // Réinitialiser le StringBuilder
                }
            } else {
                currentToken.append(c); // Construire le token caractère par caractère
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString()); // Ajouter le dernier token à la liste des tokens
        }

        return tokens; // Retourner la liste des tokens
    }

    // Méthode pour afficher l'historique des commandes
    private void printHistory() {
        System.out.println("Command History:"); // Afficher l'historique des commandes
        for (int i = 0; i < commandHistory.size(); i++) {
            System.out.println(i + 1 + ". " + commandHistory.get(i)); // Afficher chaque commande avec son numéro
        }
    }

    // Méthode pour exécuter une redirection de sortie vers un fichier
    private void executeOutputRedirect(String commandLine) {
        String[] parts = commandLine.split(">"); // Diviser la commande par >
        String commandToExecute = parts[0].trim(); // Obtenir la commande
        String filename = parts[1].trim(); // Obtenir le nom du fichier

        File file = new File(filename); // Créer un objet File pour le fichier
        if (!file.isAbsolute()) { // Si le chemin n'est pas absolu
            String currentDirectory = System.getProperty("user.dir"); // Obtenir le répertoire de travail actuel
            file = new File(currentDirectory, filename); // Créer le fichier dans le répertoire actuel
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(commandToExecute.trim().split("\\s+")); // Diviser correctement la commande
            processBuilder.directory(new File(System.getProperty("user.dir"))); // S'assurer que le répertoire de travail est correct
            processBuilder.redirectOutput(file); // Rediriger la sortie pour écraser le fichier
            Process process = processBuilder.start(); // Démarrer le processus
            process.waitFor(); // Attendre la fin du processus
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing command: " + e.getMessage()); // Gérer les erreurs
        }
    }

    // Méthode pour exécuter une redirection de sortie en ajoutant au fichier
    private void executeAppendRedirect(String commandLine) {
        String[] parts = commandLine.split(">>"); // Diviser la commande par >>
        String commandToExecute = parts[0].trim(); // Obtenir la commande
        String filename = parts[1].trim(); // Obtenir le nom du fichier

        File file = new File(filename); // Créer un objet File pour le fichier
        if (!file.isAbsolute()) { // Si le chemin n'est pas absolu
            String currentDirectory = System.getProperty("user.dir"); // Obtenir le répertoire de travail actuel
            file = new File(currentDirectory, filename); // Créer le fichier dans le répertoire actuel
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(commandToExecute.trim().split("\\s+")); // Diviser correctement la commande
            processBuilder.directory(new File(System.getProperty("user.dir"))); // S'assurer que le répertoire de travail est correct
            processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(file)); // Rediriger la sortie pour ajouter au fichier
            Process process = processBuilder.start(); // Démarrer le processus
            process.waitFor(); // Attendre la fin du processus
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing command: " + e.getMessage()); // Gérer les erreurs
        }
    }

    // Méthode pour exécuter une redirection d'entrée depuis un fichier
    private void executeInputRedirect(String commandLine) {
        String[] parts = commandLine.split("<"); // Diviser la commande par <
        String commandToExecute = parts[0].trim(); // Obtenir la commande
        String filename = parts[1].trim(); // Obtenir le nom du fichier

        File file = new File(filename); // Créer un objet File pour le fichier
        if (!file.isAbsolute()) { // Si le chemin n'est pas absolu
            String currentDirectory = System.getProperty("user.dir"); // Obtenir le répertoire de travail actuel
            file = new File(currentDirectory, filename); // Créer le fichier dans le répertoire actuel
        }

        try {
            StringBuilder fileContent = new StringBuilder(); // StringBuilder pour le contenu du fichier
            BufferedReader fileReader = new BufferedReader(new FileReader(file)); // Lire le fichier
            String line;
            while ((line = fileReader.readLine()) != null) {
                fileContent.append(line).append(System.lineSeparator()); // Ajouter chaque ligne au contenu
            }
            fileReader.close(); // Fermer le lecteur de fichier

            ProcessBuilder processBuilder = new ProcessBuilder(commandToExecute.trim().split("\\s+")); // Diviser correctement la commande
            Process process = processBuilder.start(); // Démarrer le processus

            OutputStream processInput = process.getOutputStream(); // Obtenir l'entrée standard du processus
            processInput.write(fileContent.toString().getBytes()); // Écrire le contenu du fichier dans l'entrée
            processInput.flush(); // Vider le flux
            processInput.close(); // Fermer le flux

            InputStream processOutput = process.getInputStream(); // Obtenir la sortie standard du processus
            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(processOutput)); // Lire la sortie
            String processLine;
            while ((processLine = processOutputReader.readLine()) != null) {
                System.out.println(processLine); // Afficher chaque ligne de la sortie
            }

            int exitCode = process.waitFor(); // Attendre la fin du processus
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing command: " + e.getMessage()); // Gérer les erreurs
        }
    }

    // Méthode principale pour démarrer le programme
    public static void main(String[] args) {
        Shell shell = new Shell(); // Créer une instance de Shell
        shell.start(); // Démarrer le shell
    }
}
