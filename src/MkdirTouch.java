import java.io.File;
import java.io.IOException;

public class MkdirTouch {

    // Méthode pour créer des répertoires spécifiés.
    public static void mkdir(String[] args) {
        String currentDir = System.getProperty("user.dir"); // Obtenir le répertoire de travail actuel
        if (args.length == 0) { // Vérifier si des arguments sont passés
            System.out.println("Usage: mkdir <directory>"); // Afficher un message d'usage si aucun argument n'est fourni
            return;
        }
        String directory = args[0]; // Récupérer le premier argument comme nom du répertoire à créer
        File newDir = new File(directory); // Créer un objet File avec le nom du répertoire
        if (!newDir.isAbsolute()) { // Vérifier si le chemin spécifié est absolu
            newDir = new File(currentDir, directory); // Si non, le rendre absolu en l'ajoutant au répertoire de travail actuel
        }
        if (!newDir.exists()) { // Vérifier si le répertoire n'existe pas encore
            boolean created = newDir.mkdirs(); // Créer le répertoire en créant tous les répertoires nécessaires
            if (created) {
                System.out.println("Répertoire créé avec succès : " + newDir.getAbsolutePath()); // Afficher un message de succès
            } else {
                System.out.println("Erreur lors de la création du répertoire : " + newDir.getAbsolutePath()); // Afficher un message d'erreur en cas d'échec
            }
        } else {
            System.out.println("Le répertoire existe déjà : " + newDir.getAbsolutePath()); // Informer que le répertoire existe déjà
        }
    }


    // Méthode pour créer des fichiers spécifiés.
    public static void touch(String[] filenames) {
        // Parcourir tous les noms de fichiers à créer
        for (int i = 0; i < filenames.length; i++) {
            String filename = filenames[i]; // Récupérer le nom du fichier à créer
            File file = new File(filename); // Créer un objet File avec le nom du fichier
            // Vérifier si le chemin est absolu ou relatif
            if (!file.isAbsolute()) {
                String currentDir = System.getProperty("user.dir"); // Obtenir le répertoire de travail actuel
                file = new File(currentDir, filename); // Si le chemin n'est pas absolu, le rendre absolu en l'ajoutant au répertoire de travail actuel
            }
            // Vérifier si le fichier existe déjà
            if (file.exists()) {
                System.out.println("Le fichier existe déjà : " + file.getAbsolutePath()); // Informer que le fichier existe déjà
            } else {
                // Essayer de créer le fichier en utilisant createNewFile()
                try {
                    if (file.createNewFile()) { // Tenter de créer le fichier
                        System.out.println("Fichier créé avec succès : " + file.getAbsolutePath()); // Afficher un message de succès
                    } else {
                        System.out.println("Échec de la création du fichier : " + file.getAbsolutePath()); // Afficher un message d'échec si la création a échoué
                    }
                } catch (IOException e) {
                    System.out.println("Erreur lors de la création du fichier : " + file.getAbsolutePath()); // Afficher un message d'erreur s'il y a une exception
                    e.printStackTrace(); // Imprimer la trace de l'exception pour le débogage
                }
            }
        }
    }
}
