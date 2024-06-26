import java.io.*;
import java.nio.file.*;

public class EchoCatNano {

    // Méthode pour afficher le contenu d'un fichier en interprétant les caractères d'échappement
    public static void cat(String filename) throws IOException {
        File file = new File(filename); // Créer un objet File avec le nom du fichier spécifié
        if (!file.exists()) { // Vérifier si le fichier n'existe pas avec le chemin absolu
            // Si le fichier n'existe pas, essayer avec le chemin relatif depuis le répertoire actuel
            String currentDirectory = System.getProperty("user.dir"); // Obtenir le répertoire de travail actuel
            file = new File(currentDirectory, filename); // Créer un objet File avec le chemin relatif
            if (!file.exists()) { // Vérifier à nouveau si le fichier existe après tentative avec chemin relatif
                System.err.println("Le fichier spécifié n'existe pas !"); // Afficher un message d'erreur si le fichier n'existe pas
                return; // Sortir de la méthode car le fichier n'a pas été trouvé
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Ouvrir un flux de lecture sur le fichier à l'aide de BufferedReader pour efficacité
            String line; // Déclarer une variable pour stocker chaque ligne lue
            while ((line = reader.readLine()) != null) { // Lire chaque ligne du fichier jusqu'à la fin
                System.out.println(processEscapedCharacters(line)); // Afficher la ligne lue en interprétant les caractères d'échappement
            }
        }
    }

    // Méthode qui imite la commande echo du shell
    public static void echo(String input) {
        // Traite les caractères d'échappement avant d'afficher la chaîne
        String processedInput = processEscapedCharacters(input);
        // Affiche la chaîne de caractères traitée dans la console
        System.out.println(processedInput);
    }

    // Méthode pour traiter les caractères d'échappement dans une chaîne de texte
    public static String processEscapedCharacters(String input) {
        return input.replace("\\n", "\n")   // Nouvelle ligne
                .replace("\\t", "\t")       // Tabulation
                .replace("\\r", "\r")       // Retour chariot
                .replace("\\\\", "\\")      // Backslash
                .replace("\\\"", "\"")      // Guillemet double
                .replace("\\'", "'");       // Apostrophe
    }

    // Méthode pour exécuter Nano avec le fichier spécifié
    public static void nano(String filename) {
        String currentDirectory = System.getProperty("user.dir");// Obtient le répertoire de travail actuel de l'application Java
        Path filePath = Paths.get(currentDirectory, filename); // Construit le chemin absolu du fichier en utilisant le répertoire actuel et le nom de fichier spécifié
        ProcessBuilder processBuilder = new ProcessBuilder("nano", filePath.toString());// Crée un constructeur de processus pour démarrer Nano avec le fichier spécifié
        processBuilder.directory(new File(currentDirectory));// Définit explicitement le répertoire de travail du processus Nano pour qu'il soit le même que celui de l'application Java
        processBuilder.inheritIO();// Redirige les entrées/sorties standard du processus Nano vers le terminal actuel
        try {
            Process process = processBuilder.start();// Démarre le processus Nano avec les paramètres spécifiés dans le constructeur de processus
            int exitCode = process.waitFor();// Attend que le processus Nano se termine et récupère le code de sortie
            System.out.println("Nano exited with code: " + exitCode);// Affiche le code de sortie de Nano
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();// Affiche les erreurs d'entrée/sortie ou d'interruption
        }
    }
}

