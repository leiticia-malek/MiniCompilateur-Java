/*
 * ====================================================================
 * MINI-COMPILATEUR JAVA - ANALYSEUR SYNTAXIQUE
 * Université A/ Mira de Béjaia - 3ème année Licence
 * Module : Compilation
 * 
 * Étudiant : Malek Leiticia
 * Instruction analysée : WHILE (Descente Récursive)
 * 
 * Grammaire LL(1) :
 * Programme → Classe
 * Classe → public class IDENTIFICATEUR { Methode }
 * Methode → public static void main ( String [] args ) Bloc
 * Bloc → { Instructions }
 * Instructions → Instruction Instructions | ε
 * Instruction → Declaration | Affectation | While | If
 * Declaration → Type IDENTIFICATEUR = Expression ;
 * Type → int | double | float | boolean | String
 * Affectation → IDENTIFICATEUR OpAffect ;
 * OpAffect → = Expression | ++ | --
 * While → while ( Condition ) Bloc
 * If → if ( Condition ) Bloc
 * Condition → Expression OpComp Expression | Expression
 * OpComp → == | != | < | > | <= | >= | && | ||
 * Expression → Terme Suite_Expression
 * Suite_Expression → + Terme Suite_Expression | - Terme Suite_Expression | ε
 * Terme → Facteur Suite_Terme
 * Suite_Terme → * Facteur Suite_Terme | / Facteur Suite_Terme | % Facteur Suite_Terme | ε
 * Facteur → NOMBRE | IDENTIFICATEUR | IDENTIFICATEUR ++ | IDENTIFICATEUR -- | ( Expression )
 * ====================================================================
 */
package compilateur;

import java.util.ArrayList;

/**
 * Analyseur Syntaxique par Descente Récursive
 * Implémente la grammaire LL(1) non récursive à gauche
 */
public class AnalyseurSyntaxique {
    
    // ==================== ATTRIBUTS ====================
    
    private int i;                          // Position actuelle dans les tokens
    private ArrayList<Token> tokens;        // Liste des tokens à analyser
    private boolean error;                  // Indicateur d'erreur
    private ArrayList<String> erreurs;      // Liste des messages d'erreurs
    
    // ==================== CONSTRUCTEUR ====================
    
    public AnalyseurSyntaxique(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.i = 0;
        this.error = false;
        this.erreurs = new ArrayList<>();
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================
    
    /**
     * Retourne le token actuel
     */
    private Token tokenActuel() {
        if (i < tokens.size()) {
            return tokens.get(i);
        }
        return null;
    }
    
    /**
     * Vérifie si le token actuel a la valeur attendue
     */
    private boolean verifierValeur(String valeur) {
        Token token = tokenActuel();
        return token != null && token.valeur.equals(valeur);
    }
    
    /**
     * Vérifie si le token actuel a le type attendu
     */
    private boolean verifierType(String type) {
        Token token = tokenActuel();
        return token != null && token.type.equals(type);
    }
    
    /**
     * Ajoute un message d'erreur
     */
    private void ajouterErreur(String message) {
        Token token = tokenActuel();
        if (token != null) {
            String err = "Erreur syntaxique ligne " + token.ligne + 
                        ", colonne " + token.colonne + " : " + message;
            erreurs.add(err);
            System.err.println(err);
        } else {
            String err = "Erreur syntaxique : " + message + " (fin de fichier)";
            erreurs.add(err);
            System.err.println(err);
        }
        error = true;
    }
    
    /**
     * Consomme un token avec une valeur spécifique
     */
    private void consommer(String valeur) {
        if (verifierValeur(valeur)) {
            i++;
        } else {
            ajouterErreur("Attendu '" + valeur + "', obtenu '" + 
                         (tokenActuel() != null ? tokenActuel().valeur : "EOF") + "'");
        }
    }
    
    /**
     * Consomme un token avec un type spécifique
     */
    private void consommerType(String type) {
        if (verifierType(type)) {
            i++;
        } else {
            ajouterErreur("Attendu type " + type + ", obtenu " + 
                         (tokenActuel() != null ? tokenActuel().type : "EOF"));
        }
    }
    
    // ==================== MÉTHODE PRINCIPALE ====================
    
    /**
     * Méthode Z - Point d'entrée de l'analyse syntaxique
     */
    public void Z() {
        System.out.println("\n>>> Début de l'analyse syntaxique...\n");
        
        i = 0;
        error = false;
        erreurs.clear();
        
        Programme();
        
        // Vérification finale : on doit être à EOF
        if (verifierType("EOF") && i == tokens.size() - 1 && !error) {
            System.out.println("\n✓ PROGRAMME ACCEPTÉ");
            System.out.println("Le programme est syntaxiquement correct.\n");
        } else {
            System.err.println("\n✗ PROGRAMME REJETÉ");
            if (!verifierType("EOF")) {
                System.err.println("Tokens inattendus après la fin du programme");
            }
            System.err.println();
        }
    }
    
    // ==================== RÈGLES DE GRAMMAIRE ====================
    
    /**
     * Règle : Programme → Classe
     */
    private void Programme() {
        System.out.println("[PROGRAMME] Analyse du programme");
        Classe();
    }
    
    /**
     * Règle : Classe → public class IDENTIFICATEUR { Methode }
     */
    private void Classe() {
        System.out.println("[CLASSE] Analyse de la classe");
        
        consommer("public");
        consommer("class");
        
        if (verifierType("IDENTIFICATEUR") || verifierType("MOT_CLE")) {
            System.out.println("[CLASSE] Nom de classe : " + tokenActuel().valeur);
            i++;
        } else {
            ajouterErreur("Nom de classe attendu");
        }
        
        consommerType("ACCOLADE_OUVRANTE");
        Methode();
        consommerType("ACCOLADE_FERMANTE");
    }
    
    /**
     * Règle : Methode → public static void main ( String [] args ) Bloc
     */
    private void Methode() {
        System.out.println("[METHODE] Analyse de la méthode main");
        
        consommer("public");
        consommer("static");
        consommer("void");
        consommer("main");
        consommerType("PAREN_OUVRANT");
        consommer("String");
        consommerType("CROCHET_OUVRANT");
        consommerType("CROCHET_FERMANT");
        
        if (verifierType("IDENTIFICATEUR")) {
            i++;
        } else {
            ajouterErreur("Nom de paramètre attendu");
        }
        
        consommerType("PAREN_FERMANT");
        Bloc();
    }
    
    /**
     * Règle : Bloc → { Instructions }
     */
    private void Bloc() {
        consommerType("ACCOLADE_OUVRANTE");
        Instructions();
        consommerType("ACCOLADE_FERMANTE");
    }
    
    /**
     * Règle : Instructions → Instruction Instructions | ε
     */
    private void Instructions() {
        while (tokenActuel() != null && 
               !verifierType("ACCOLADE_FERMANTE") &&
               !verifierType("EOF")) {
            Instruction();
        }
        // ε (epsilon) : sortie de boucle
    }
    
    /**
     * Règle : Instruction → Declaration | Affectation | While | If
     */
    private void Instruction() {
        Token token = tokenActuel();
        
        if (token == null) {
            return;
        }
        
        // While (analyse détaillée)
        if (verifierValeur("while")) {
            While();
        }
        // If (reconnu mais ignoré)
        else if (verifierValeur("if")) {
            System.out.println("[IF] Instruction IF reconnue (ignorée)");
            IgnorerIf();
        }
        // For (reconnu mais ignoré)
        else if (verifierValeur("for")) {
            System.out.println("[FOR] Instruction FOR reconnue (ignorée)");
            IgnorerFor();
        }
        // Declaration (commence par un type)
        else if (verifierValeur("int") || verifierValeur("double") || 
                 verifierValeur("float") || verifierValeur("boolean") || 
                 verifierValeur("String")) {
            Declaration();
        }
        // Affectation (commence par un identificateur)
        else if (verifierType("IDENTIFICATEUR") || verifierType("MOT_CLE")) {
            Affectation();
        }
        else {
            ajouterErreur("Instruction invalide : '" + token.valeur + "'");
            i++;
        }
    }
    
    /**
     * Règle : Declaration → Type IDENTIFICATEUR = Expression ;
     */
    private void Declaration() {
        System.out.println("[DECLARATION] Analyse d'une déclaration");
        
        Type();
        
        if (verifierType("IDENTIFICATEUR") || verifierType("MOT_CLE")) {
            System.out.println("[DECLARATION] Variable : " + tokenActuel().valeur);
            i++;
        } else {
            ajouterErreur("Nom de variable attendu");
        }
        
        consommerType("AFFECTATION");
        Expression();
        consommerType("POINT_VIRGULE");
    }
    
    /**
     * Règle : Type → int | double | float | boolean | String
     */
    private void Type() {
        if (verifierValeur("int") || verifierValeur("double") || 
            verifierValeur("float") || verifierValeur("boolean") || 
            verifierValeur("String")) {
            i++;
        } else {
            ajouterErreur("Type de données invalide");
        }
    }
    
    /**
     * Règle : Affectation → IDENTIFICATEUR OpAffect ;
     */
    private void Affectation() {
        System.out.println("[AFFECTATION] Analyse d'une affectation");
        
        if (verifierType("IDENTIFICATEUR") || verifierType("MOT_CLE")) {
            i++;
        } else {
            ajouterErreur("Identificateur attendu");
            return;
        }
        
        OpAffect();
        consommerType("POINT_VIRGULE");
    }
    
    /**
     * Règle : OpAffect → = Expression | ++ | --
     */
    private void OpAffect() {
        if (verifierType("AFFECTATION")) {
            i++;
            Expression();
        } else if (verifierType("INCREMENT") || verifierType("DECREMENT")) {
            i++;
        } else {
            ajouterErreur("Opérateur d'affectation attendu (=, ++, --)");
        }
    }
    
    /**
     * Règle : While → while ( Condition ) Bloc
     * INSTRUCTION PRINCIPALE - ANALYSE DÉTAILLÉE
     */
    private void While() {
        System.out.println("\n========================================");
        System.out.println("[WHILE] *** ANALYSE DÉTAILLÉE DE WHILE ***");
        System.out.println("========================================");
        
        if (!verifierValeur("while")) {
            ajouterErreur("Mot-clé 'while' attendu");
            return;
        }
        i++;
        System.out.println("[WHILE] Mot-clé 'while' reconnu");
        
        if (!verifierType("PAREN_OUVRANT")) {
            ajouterErreur("Parenthèse ouvrante '(' attendue");
            System.err.println("[WHILE] Erreur : parenthèse ouvrante manquante");
            return;
        }
        i++;
        System.out.println("[WHILE] Parenthèse ouvrante '(' trouvée");
        
        System.out.println("[WHILE] Analyse de la condition...");
        Condition();
        System.out.println("[WHILE] Condition analysée");
        
        if (!verifierType("PAREN_FERMANT")) {
            ajouterErreur("Parenthèse fermante ')' attendue");
            System.err.println("[WHILE] Erreur : parenthèse fermante manquante");
            return;
        }
        i++;
        System.out.println("[WHILE] Parenthèse fermante ')' trouvée");
        
        System.out.println("[WHILE] Analyse du bloc d'instructions...");
        Bloc();
        System.out.println("[WHILE] Bloc analysé avec succès");
        
        System.out.println("[WHILE] *** FIN DE L'ANALYSE DE WHILE ***");
        System.out.println("========================================\n");
    }
    
    /**
     * Ignore l'instruction if
     */
    private void IgnorerIf() {
        i++; // if
        
        if (verifierType("PAREN_OUVRANT")) {
            i++;
            int profondeur = 1;
            while (tokenActuel() != null && profondeur > 0) {
                if (verifierType("PAREN_OUVRANT")) profondeur++;
                if (verifierType("PAREN_FERMANT")) profondeur--;
                i++;
            }
        }
        
        if (verifierType("ACCOLADE_OUVRANTE")) {
            i++;
            int profondeur = 1;
            while (tokenActuel() != null && profondeur > 0) {
                if (verifierType("ACCOLADE_OUVRANTE")) profondeur++;
                if (verifierType("ACCOLADE_FERMANTE")) profondeur--;
                i++;
            }
        }
    }
    
    /**
     * Ignore l'instruction for
     */
    private void IgnorerFor() {
        i++; // for
        
        if (verifierType("PAREN_OUVRANT")) {
            i++;
            int profondeur = 1;
            while (tokenActuel() != null && profondeur > 0) {
                if (verifierType("PAREN_OUVRANT")) profondeur++;
                if (verifierType("PAREN_FERMANT")) profondeur--;
                i++;
            }
        }
        
        if (verifierType("ACCOLADE_OUVRANTE")) {
            i++;
            int profondeur = 1;
            while (tokenActuel() != null && profondeur > 0) {
                if (verifierType("ACCOLADE_OUVRANTE")) profondeur++;
                if (verifierType("ACCOLADE_FERMANTE")) profondeur--;
                i++;
            }
        }
    }
    
    /**
     * Règle : Condition → Expression OpComp Expression | Expression
     */
    private void Condition() {
        Expression();
        
        // OpComp optionnel
        if (verifierType("EGAL") || verifierType("DIFFERENT") || 
            verifierType("INFERIEUR") || verifierType("SUPERIEUR") || 
            verifierType("INFERIEUR_EGAL") || verifierType("SUPERIEUR_EGAL") ||
            verifierType("ET_LOGIQUE") || verifierType("OU_LOGIQUE")) {
            i++;
            Expression();
        }
    }
    
    /**
     * Règle : Expression → Terme Suite_Expression
     */
    private void Expression() {
        Terme();
        Suite_Expression();
    }
    
    /**
     * Règle : Suite_Expression → + Terme Suite_Expression 
     *                          | - Terme Suite_Expression 
     *                          | ε
     */
    private void Suite_Expression() {
        if (verifierType("PLUS") || verifierType("MOINS")) {
            i++;
            Terme();
            Suite_Expression(); // Appel récursif
        }
        // ε (epsilon) : ne rien faire
    }
    
    /**
     * Règle : Terme → Facteur Suite_Terme
     */
    private void Terme() {
        Facteur();
        Suite_Terme();
    }
    
    /**
     * Règle : Suite_Terme → * Facteur Suite_Terme 
     *                      | / Facteur Suite_Terme 
     *                      | % Facteur Suite_Terme 
     *                      | ε
     */
    private void Suite_Terme() {
        if (verifierType("FOIS") || verifierType("DIVISION") || verifierType("MODULO")) {
            i++;
            Facteur();
            Suite_Terme(); // Appel récursif
        }
        // ε (epsilon) : ne rien faire
    }
    
    /**
     * Règle : Facteur → NOMBRE 
     *                 | IDENTIFICATEUR 
     *                 | IDENTIFICATEUR ++ 
     *                 | IDENTIFICATEUR -- 
     *                 | ( Expression )
     */
    private void Facteur() {
        Token token = tokenActuel();
        
        if (token == null) {
            ajouterErreur("Facteur attendu");
            return;
        }
        
        // Nombre
        if (verifierType("NOMBRE_ENTIER") || verifierType("NOMBRE_DECIMAL")) {
            i++;
        }
        // Identificateur
        else if (verifierType("IDENTIFICATEUR") || verifierType("MOT_CLE")) {
            i++;
            // Vérifier si suivi de ++ ou --
            if (verifierType("INCREMENT") || verifierType("DECREMENT")) {
                i++;
            }
        }
        // ( Expression )
        else if (verifierType("PAREN_OUVRANT")) {
            i++;
            Expression();
            consommerType("PAREN_FERMANT");
        }
        else {
            ajouterErreur("Facteur invalide : nombre, identificateur ou (expression) attendu");
        }
    }
    
    // ==================== AFFICHAGE ====================
    
    /**
     * Affiche les erreurs syntaxiques
     */
    public void afficherErreurs() {
        if (erreurs.isEmpty()) {
           if (error) {
            // Il y a eu une erreur mais la liste est vide ?
            System.out.println("⚠ Erreurs détectées pendant l'analyse\n");
        } else {
            System.out.println("✓ Aucune erreur syntaxique détectée\n");
        }
        } else {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("                  ERREURS SYNTAXIQUES DÉTECTÉES");
            System.out.println("=".repeat(70));
            for (String err : erreurs) {
                System.out.println("✗ " + err);
            }
            System.out.println("=".repeat(70));
            System.out.println("Total : " + erreurs.size() + " erreurs\n");
        }
    }
    
    /**
     * Retourne true s'il y a des erreurs
     */
    public boolean aDesErreurs() {
        return !erreurs.isEmpty();
    }
    
    // ==================== MAIN POUR TESTER ====================
    
    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║   MINI-COMPILATEUR JAVA - ANALYSEUR SYNTAXIQUE    ║");
        System.out.println("║   Étudiant : Malek Leiticia                       ║");
        System.out.println("║   Instruction : WHILE (Descente Récursive)        ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        
        // Créer l'analyseur lexical
        AnalyseurLexical lexer = new AnalyseurLexical("");
        String code = lexer.lireFichier("test.java");//chemin relatif
        
        if (code == null) {
            System.err.println("Erreur : Impossible de lire test.java\n");
            return;
        }
        
        System.out.println("Fichier : test.java");
        System.out.println("Taille : " + code.length() + " caractères\n");
        
        // Analyse lexicale
        lexer = new AnalyseurLexical(code);
        lexer.analyser();
        lexer.afficherTokens();
        lexer.afficherErreurs();
        
        // Si erreurs lexicales, ne pas continuer
        if (lexer.aDesErreurs()) {
            System.err.println("⚠ Impossible de continuer : erreurs lexicales détectées\n");
            return;
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("     PASSAGE À L'ANALYSE SYNTAXIQUE");
        System.out.println("=".repeat(70) + "\n");
        
        // Analyse syntaxique
        AnalyseurSyntaxique parser = new AnalyseurSyntaxique(lexer.getTokens());
        parser.Z();
        parser.afficherErreurs();
    }
}

