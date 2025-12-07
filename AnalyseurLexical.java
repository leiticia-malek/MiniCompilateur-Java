/*
 * ====================================================================
 * MINI-COMPILATEUR JAVA - ANALYSEUR LEXICAL
 * Université A/ Mira de Béjaia - 3ème année Licence
 * Module : Compilation
 * 
 * Étudiant : Malek Leiticia
 * Instruction analysée : WHILE
 * ====================================================================
 */
package compilateur;

import java.io.*;
import java.util.*;

// ====================================================================
// Classe Token - Représente une unité lexicale
// ====================================================================
class Token {
    String type;      // Type du token
    String valeur;    // Valeur du token
    int ligne;        // Numéro de ligne
    int colonne;      // Numéro de colonne
    
    public Token(String type, String valeur, int ligne, int colonne) {
        this.type = type;
        this.valeur = valeur;
        this.ligne = ligne;
        this.colonne = colonne;
    }
    
    @Override
    public String toString() {
        return String.format("%-20s | %-15s | L:%-3d | C:%-3d", 
                            type, valeur, ligne, colonne);
    }
}

// ====================================================================
// Classe AnalyseurLexical - Analyseur lexical
// ====================================================================
public class AnalyseurLexical {
    
    // ==================== ATTRIBUTS ====================
    
    private String code;                     // Code source
    private int position;                    // Position actuelle
    private int ligne;                       // Ligne actuelle
    private int colonne;                     // Colonne actuelle
    private ArrayList<Token> tokens;         // Liste des tokens
    private ArrayList<String> erreurs;       // Liste des erreurs
    
    // Mots-clés Java (incluant Malek et Leiticia)
    private static final String[] MOTS_CLES = {
        "public", "private", "protected", "static", "final",
        "class", "void", "int", "double", "float", "boolean", "String",
        "if", "else", "while", "do", "for", "switch", "case",
        "break", "continue", "return", "new", "this",
        "Malek", "Leiticia"  // Mots-clés personnalisés (nom et prénom)
    };
    
    // ========== MATRICE DE TRANSITION POUR IDENTIFICATEURS ==========
    // États : 0=initial, 1=identificateur, -1=erreur
    // Colonnes : 0=lettre, 1=chiffre, 2=underscore, 3=autres
    private static final int[][] MATRICE = {
        {  1, -1,  1, -1 },  // État 0
        {  1,  1,  1, -1 }   // État 1
    };
    
    // ==================== CONSTRUCTEUR ====================
    
    public AnalyseurLexical(String code) {
        this.code = code;
        this.position = 0;
        this.ligne = 1;
        this.colonne = 1;
        this.tokens = new ArrayList<>();
        this.erreurs = new ArrayList<>();
    }
    
    // ==================== LECTURE DEPUIS FICHIER ====================
    
    /**
     * Procédure pour lire le contenu d'un fichier
     */
    public String lireFichier(String cheminFichier) {
        StringBuilder contenu = new StringBuilder();
        
        try {
            FileReader fileReader = new FileReader(cheminFichier);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String ligne;
            while ((ligne = bufferedReader.readLine()) != null) {
                contenu.append(ligne);
                contenu.append("\n");
            }
            
            bufferedReader.close();
            fileReader.close();
            
            return contenu.toString();
            
        } catch (FileNotFoundException e) {
            System.err.println("Erreur : Fichier '" + cheminFichier + "' introuvable");
            return null;
        } catch (IOException e) {
            System.err.println("Erreur de lecture : " + e.getMessage());
            return null;
        }
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================
    
    // Retourne le caractère actuel
    private char charActuel() {
        return (position < code.length()) ? code.charAt(position) : '\0';
    }
    
    // Regarde le caractère suivant
    private char suivant() {
        return (position + 1 < code.length()) ? code.charAt(position + 1) : '\0';
    }
    
    // Avance d'un caractère
    private void avancer() {
        if (charActuel() == '\n') {
            ligne++;
            colonne = 1;
        } else {
            colonne++;
        }
        position++;
    }
    
    // Ignore les espaces
    private void ignorerEspaces() {
        while (charActuel() == ' ' || charActuel() == '\t' || 
               charActuel() == '\n' || charActuel() == '\r') {
            avancer();
        }
    }
    
    // Retourne l'indice de colonne pour la matrice
    private int indiceMatrice(char c) {
        if (Character.isLetter(c)) return 0;
        if (Character.isDigit(c)) return 1;
        if (c == '_') return 2;
        return 3;
    }
    
    // Vérifie si c'est un mot-clé (incluant Malek et Leiticia)
    private boolean estMotCle(String mot) {
        for (String mc : MOTS_CLES) {
            if (mc.equals(mot)) return true;
        }
        return false;
    }
    
    // Ajoute une erreur
    private void ajouterErreur(String message) {
        erreurs.add("Erreur ligne " + ligne + ", colonne " + colonne + " : " + message);
    }
    
    // ==================== RECONNAISSANCE DES TOKENS ====================
    
    /**
     * Reconnaît un identificateur avec la matrice de transition
     */
    private void reconnaitreIdentificateur() {
        int ligneDebut = ligne;
        int colonneDebut = colonne;
        int posDebut = position;
        int etat = 0;
        
        // Parcours avec matrice
        while (charActuel() != '\0') {
            int col = indiceMatrice(charActuel());
            int nouvelEtat = MATRICE[etat][col];
            
            if (nouvelEtat == -1) break;
            
            etat = nouvelEtat;
            avancer();
        }
        
        // Extraire le mot
        String mot = code.substring(posDebut, position);
        
        // Déterminer le type (MOT_CLE inclut Malek et Leiticia)
        String type = estMotCle(mot) ? "MOT_CLE" : "IDENTIFICATEUR";
        
        tokens.add(new Token(type, mot, ligneDebut, colonneDebut));
    }
    
    /**
     * Reconnaît un nombre
     */
    private void reconnaitreNombre() {
        int ligneDebut = ligne;
        int colonneDebut = colonne;
        int posDebut = position;
        String type = "NOMBRE_ENTIER";
        
        // Partie entière
        while (Character.isDigit(charActuel())) {
            avancer();
        }
        
        // Partie décimale
        if (charActuel() == '.' && Character.isDigit(suivant())) {
            type = "NOMBRE_DECIMAL";
            avancer();
            while (Character.isDigit(charActuel())) {
                avancer();
            }
        }
        
        String valeur = code.substring(posDebut, position);
        tokens.add(new Token(type, valeur, ligneDebut, colonneDebut));
    }
    
    /**
     * Reconnaît une chaîne
     */
    private void reconnaitreChaine() {
        int ligneDebut = ligne;
        int colonneDebut = colonne;
        char guillemet = charActuel();
        avancer();
        
        int posDebut = position;
        
        while (charActuel() != '\0' && charActuel() != guillemet) {
            if (charActuel() == '\\') {
                avancer();
            }
            avancer();
        }
        
        if (charActuel() == '\0') {
            ajouterErreur("Chaîne non fermée");
            return;
        }
        
        String valeur = code.substring(posDebut, position);
        avancer();
        tokens.add(new Token("CHAINE", valeur, ligneDebut, colonneDebut));
    }
    
    /**
     * Ignore commentaire ligne //
     */
    private void ignorerCommentaireLigne() {
        while (charActuel() != '\0' && charActuel() != '\n') {
            avancer();
        }
    }
    
    /**
     * Ignore commentaire bloc
     */
    private void ignorerCommentaireBloc() {
        int ligneDebut = ligne;
        avancer(); avancer();
        
        while (charActuel() != '\0') {
            if (charActuel() == '*' && suivant() == '/') {
                avancer(); avancer();
                return;
            }
            avancer();
        }
        
        ajouterErreur("Commentaire non fermé (débuté ligne " + ligneDebut + ")");
    }
    
    /**
     * Reconnaît un opérateur avec TYPE SPÉCIFIQUE
     */
    private void reconnaitreOperateur() {
        int ligneDebut = ligne;
        int colonneDebut = colonne;
        char c = charActuel();
        String type = "";
        String valeur = "";
        
        // Opérateurs doubles
        if (c == '=' && suivant() == '=') {
            type = "EGAL";
            valeur = "==";
            avancer(); avancer();
        } 
        else if (c == '!' && suivant() == '=') {
            type = "DIFFERENT";
            valeur = "!=";
            avancer(); avancer();
        } 
        else if (c == '<' && suivant() == '=') {
            type = "INFERIEUR_EGAL";
            valeur = "<=";
            avancer(); avancer();
        } 
        else if (c == '>' && suivant() == '=') {
            type = "SUPERIEUR_EGAL";
            valeur = ">=";
            avancer(); avancer();
        } 
        else if (c == '+' && suivant() == '+') {
            type = "INCREMENT";
            valeur = "++";
            avancer(); avancer();
        } 
        else if (c == '-' && suivant() == '-') {
            type = "DECREMENT";
            valeur = "--";
            avancer(); avancer();
        } 
        else if (c == '&' && suivant() == '&') {
            type = "ET_LOGIQUE";
            valeur = "&&";
            avancer(); avancer();
        } 
        else if (c == '|' && suivant() == '|') {
            type = "OU_LOGIQUE";
            valeur = "||";
            avancer(); avancer();
        }
        // Opérateurs simples
        else {
            valeur = "" + c;
            avancer();
            
            if (c == '+') type = "PLUS";
            else if (c == '-') type = "MOINS";
            else if (c == '*') type = "FOIS";
            else if (c == '/') type = "DIVISION";
            else if (c == '%') type = "MODULO";
            else if (c == '=') type = "AFFECTATION";
            else if (c == '<') type = "INFERIEUR";
            else if (c == '>') type = "SUPERIEUR";
            else if (c == '!') type = "NON_LOGIQUE";
            else {
                ajouterErreur("Opérateur invalide '" + c + "'");
                return;
            }
        }
        
        tokens.add(new Token(type, valeur, ligneDebut, colonneDebut));
    }
    
    /**
     * Reconnaît un séparateur avec TYPE SPÉCIFIQUE
     */
    private void reconnaitreSeparateur() {
        int ligneDebut = ligne;
        int colonneDebut = colonne;
        char c = charActuel();
        String type = "";
        avancer();
        
        if (c == '(') type = "PAREN_OUVRANT";
        else if (c == ')') type = "PAREN_FERMANT";
        else if (c == '{') type = "ACCOLADE_OUVRANTE";
        else if (c == '}') type = "ACCOLADE_FERMANTE";
        else if (c == '[') type = "CROCHET_OUVRANT";
        else if (c == ']') type = "CROCHET_FERMANT";
        else if (c == ';') type = "POINT_VIRGULE";
        else if (c == ',') type = "VIRGULE";
        else if (c == '.') type = "POINT";
        else {
            ajouterErreur("Séparateur invalide '" + c + "'");
            return;
        }
        
        tokens.add(new Token(type, "" + c, ligneDebut, colonneDebut));
    }
    
    // ==================== ANALYSE PRINCIPALE ====================
    
    /**
     * Lance l'analyse lexicale complète
     */
    public void analyser() {
        System.out.println(">>> Début de l'analyse lexicale...\n");
        
        while (charActuel() != '\0') {
            ignorerEspaces();
            
            if (charActuel() == '\0') break;
            
            char c = charActuel();
            
            // Commentaire ligne
            if (c == '/' && suivant() == '/') {
                ignorerCommentaireLigne();
                continue;
            }
            
            // Commentaire bloc
            if (c == '/' && suivant() == '*') {
                ignorerCommentaireBloc();
                continue;
            }
            
            // Identificateur
            if (Character.isLetter(c) || c == '_') {
                reconnaitreIdentificateur();
                continue;
            }
            
            // Nombre
            if (Character.isDigit(c)) {
                reconnaitreNombre();
                continue;
            }
            
            // Chaîne
            if (c == '"' || c == '\'') {
                reconnaitreChaine();
                continue;
            }
            
            // Séparateurs
            if ("(){}[];,.".indexOf(c) != -1) {
                reconnaitreSeparateur();
                continue;
            }
            
            // Opérateurs
            if ("+-*/%=<>!&|".indexOf(c) != -1) {
                reconnaitreOperateur();
                continue;
            }
            
            // Caractère invalide
            ajouterErreur("Caractère invalide '" + c + "'");
            avancer();
        }
        
        // Ajouter le token EOF (End Of File)
        tokens.add(new Token("EOF", "EOF", ligne, colonne));
        
        System.out.println(">>> Analyse terminée.\n");
    }
    
    // ==================== AFFICHAGE ====================
    
    /**
     * Affiche tous les tokens
     */
    public void afficherTokens() {
        System.out.println("=" .repeat(70));
        System.out.println("                    TOKENS RECONNUS");
        System.out.println("=" .repeat(70));
        System.out.println(String.format("%-20s | %-15s | %-6s | %-6s", 
                                        "TYPE", "VALEUR", "LIGNE", "COL"));
        System.out.println("-" .repeat(70));
        
        for (Token t : tokens) {
            System.out.println(t);
        }
        
        System.out.println("=" .repeat(70));
        System.out.println("Total : " + tokens.size() + " tokens\n");
    }
    
    /**
     * Affiche les erreurs
     */
    public void afficherErreurs() {
        if (erreurs.isEmpty()) {
            System.out.println("✓ Aucune erreur lexicale\n");
        } else {
            System.out.println("=" .repeat(70));
            System.out.println("                    ERREURS LEXICALES");
            System.out.println("=" .repeat(70));
            for (String err : erreurs) {
                System.out.println("✗ " + err);
            }
            System.out.println("=" .repeat(70));
            System.out.println("Total : " + erreurs.size() + " erreurs\n");
        }
    }
    
    // ==================== GETTERS ====================
    
    public ArrayList<Token> getTokens() {
        return tokens;
    }
    
    public ArrayList<String> getErreurs() {
        return erreurs;
    }
    
    public boolean aDesErreurs() {
        return !erreurs.isEmpty();
    }
    
    // ==================== MAIN POUR TESTER ====================
    
    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║     MINI-COMPILATEUR JAVA - ANALYSEUR LEXICAL     ║");
        System.out.println("║     Étudiant : Malek Leiticia                     ║");
        System.out.println("║     Instruction : WHILE                            ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        
        // Créer une instance de l'analyseur
        AnalyseurLexical lexer = new AnalyseurLexical("");
        
        // Lire le fichier test.java avec la procédure
        String code = lexer.lireFichier("test.java");
        
        if (code == null) {
            System.err.println("\nCréez un fichier test.java dans le dossier du projet\n");
            return;
        }
        
        System.out.println("Fichier : test.java");
        System.out.println("Taille : " + code.length() + " caractères\n");
        
        // Créer un nouvel analyseur avec le code chargé
        lexer = new AnalyseurLexical(code);
        
        // Analyser
        lexer.analyser();
        
        // Afficher résultats
        lexer.afficherTokens();
        lexer.afficherErreurs();
        
        // Résumé
        if (lexer.aDesErreurs()) {
            System.out.println("⚠ Compilation terminée avec erreurs\n");
        } else {
            System.out.println("✓ Compilation réussie\n");
        }
    }
}

