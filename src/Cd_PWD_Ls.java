 
import java.io.File;  
public class Cd_PWD_Ls {  
    // Méthode cd  pour changer de répertoire
    public static void cd(String[] args) {  
        String userHome = System.getProperty("user.home");  // Obtenir le répertoire personnel de l'utilisateur
        String currentDir = System.getProperty("user.dir");  // Obtenir le répertoire actuel

        if (args.length == 0 || args[0].equals("~")) {  // Si aucun argument ou "~" est donné
            System.setProperty("user.dir", userHome);  // Changer pour le répertoire personnel de l'utilisateur
            System.out.println("Changed to user home directory: " + userHome);  // Afficher le nouveau répertoire
            return;  // Quitter la méthode
        }

        String directory = args[0];  // Obtenir le répertoire à partir des arguments
        if (directory.equals("..")) {  // Si l'argument est ".."
            String parentDir = new File(currentDir).getParent();  // Obtenir le répertoire parent
            if (parentDir != null) {  // Si le répertoire parent existe
                System.setProperty("user.dir", parentDir);  // Changer pour le répertoire parent
                System.out.println("Changed to parent directory: " + parentDir);  // Afficher le nouveau répertoire
            } else {
                System.out.println("Already in root directory");  // Afficher si déjà dans le répertoire racine
            }
        } else if (directory.equals(".")) {  // Si l'argument est "."
            System.out.println("Staying in current directory: " + currentDir);  // Afficher le répertoire actuel
        } else {  // Pour tout autre argument
            File newDir = new File(directory);  // Créer un objet File pour le nouveau répertoire
            if (!newDir.isAbsolute()) {  // Si le chemin n'est pas absolu
                newDir = new File(currentDir, directory);  // Créer un objet File avec le répertoire actuel comme parent
            }
            if (newDir.exists() && newDir.isDirectory()) {  // Si le répertoire existe et est un répertoire
                System.setProperty("user.dir", newDir.getAbsolutePath());  // Changer pour le nouveau répertoire
                System.out.println("Changed to directory: " + newDir.getAbsolutePath());  // Afficher le nouveau répertoire
            } else {
                System.out.println("Directory not found: " + directory);  // Afficher si le répertoire n'est pas trouvé
            }
        }
    }

    // Méthode pwd : Affiche le répertoire courant
    public static void pwd() {
        String currentDirectory = System.getProperty("user.dir");  // Obtenir le répertoire actuel
        System.out.println(currentDirectory);  // Afficher le répertoire actuel
    }

    //Méthode ls : Liste les fichiers et répertoires dans le répertoire spécifié
    public static void ls(String[] args) {
        String currentDir = System.getProperty("user.dir");  // Obtenir le répertoire actuel
        if (args.length == 0) {  // Si aucun argument
            listFiles(currentDir);  // Lister les fichiers dans le répertoire actuel
        } else if (args[0].equals("*")) {  // Si l'argument est "*"
            listFilesRecursive(currentDir);  // Lister les fichiers récursivement dans le répertoire actuel
        } else {
            String directory = args[0];  // Obtenir le répertoire à partir des arguments
            File dir = new File(currentDir, directory);  // Créer un objet File pour le répertoire
            if (dir.exists() && dir.isDirectory()) {  // Si le répertoire existe et est un répertoire
                listFiles(dir.getAbsolutePath());  // Lister les fichiers dans le répertoire spécifié
            } else {
                System.out.println("Directory not found: " + directory);  // Afficher si le répertoire n'est pas trouvé
            }
        }
    }
    
    // Méthode pour lister les fichiers dans un répertoire
    private static void listFiles(String directoryPath) {  
        File dir = new File(directoryPath);  // Créer un objet File pour le répertoire
        File[] files = dir.listFiles();  // Obtenir la liste des fichiers et répertoires
        if (files != null) {  // Si la liste n'est pas nulle
            for (File file : files) {  // Pour chaque fichier ou répertoire
                System.out.println(file.getName());  // Afficher le nom de chaque fichier ou répertoire
            }
        }
    }
    
    // Méthode pour lister les fichiers récursivement
    private static void listFilesRecursive(String directoryPath) {  
        File dir = new File(directoryPath);  // Créer un objet File pour le répertoire
        File[] files = dir.listFiles();  // Obtenir la liste des fichiers et répertoires
        if (files != null) {  // Si la liste n'est pas nulle
            for (File file : files) {  // Pour chaque fichier ou répertoire
                System.out.println(file.getAbsolutePath());  // Afficher le chemin absolu de chaque fichier ou répertoire
                if (file.isDirectory()) {  // Si c'est un répertoire
                    listFilesRecursive(file.getAbsolutePath());  // Lister récursivement les sous-répertoires
                }
            }
        }
    }

}

