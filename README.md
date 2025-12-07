# Mini-Compilateur Java - Instruction WHILE

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![NetBeans](https://img.shields.io/badge/NetBeans-1B6AC6?style=for-the-badge&logo=apache-netbeans-ide&logoColor=white)](https://netbeans.apache.org/)

> **Projet TP - Module Compilation**  
> Université A/ Mira de Béjaia - Département d'informatique  
> 3ème année Licence académique - 2024/2025

---

## 📋 Description

Ce projet implémente un **mini-compilateur** pour un sous-ensemble du langage Java. Il réalise deux phases essentielles de la compilation :

1. **Analyse lexicale** : Décomposition du code source en tokens (unités lexicales)
2. **Analyse syntaxique** : Vérification de la structure grammaticale du programme

L'instruction **WHILE** est analysée en détail, tandis que les autres structures de contrôle (if, for) sont reconnues mais non analysées en profondeur.

---

## 👨‍💻 Étudiant

**Nom :** Malek Leiticia  
**Langage cible :** Java  
**Instruction analysée :** WHILE  

---

## 🎯 Fonctionnalités

### ✅ Analyseur Lexical
- Reconnaissance des tokens avec **matrice de transition** pour les identificateurs
- Types de tokens : mots-clés, identificateurs, nombres, opérateurs, séparateurs
- Mots-clés personnalisés : **Malek** et **Leiticia**
- Gestion des commentaires (`//` et `/* */`)
- Détection des erreurs lexicales sans arrêt de l'analyse
- Lecture depuis fichier `.java`

### ✅ Analyseur Syntaxique
- Méthode : **Descente récursive**
- Grammaire **LL(1)** non récursive à gauche
- Analyse détaillée de l'instruction **WHILE**
- Reconnaissance des instructions IF et FOR (ignorées dans l'analyse)
- Gestion des erreurs syntaxiques avec récupération
- Affichage détaillé du processus d'analyse

---

## 📁 Structure du projet

```
MiniCompilateur/
│
├── src/
│   └── minicompilateur/
│       ├── AnalyseurLexical.java      # Analyseur lexical + classe Token
│       └── AnalyseurSyntaxique.java   # Analyseur syntaxique
│
├── test.java                           # Fichier de test
├── README.md                           # Ce fichier
└── dist/
    └── MiniCompilateur.jar             # Exécutable
```

---

## 🚀 Installation et exécution

### Prérequis
- Java JDK 8 ou supérieur
- NetBeans IDE (ou tout IDE Java)

### Compilation

#### Avec NetBeans
1. Ouvrir le projet dans NetBeans
2. Right-click sur le projet → **Clean and Build**
3. Le fichier `.jar` sera créé dans `dist/`

#### En ligne de commande
```bash
javac -d bin src/minicompilateur/*.java
jar cfe MiniCompilateur.jar minicompilateur.AnalyseurSyntaxique -C bin .
```

### Exécution

```bash
java -jar MiniCompilateur.jar
```

**Note :** Le fichier `test.java` doit être dans le même dossier que le `.jar`

---

## 📖 Grammaire

La grammaire définie est **LL(1)** et non récursive à gauche :

```
Programme → Classe
Classe → public class IDENTIFICATEUR { Methode }
Methode → public static void main ( String [] args ) Bloc
Bloc → { Instructions }
Instructions → Instruction Instructions | ε
Instruction → Declaration | Affectation | While | If
...
While → while ( Condition ) Bloc    [ANALYSE DÉTAILLÉE]
...
```

Pour la grammaire complète, voir le rapport PDF.

---

## 🧪 Exemples de tests

### Test 1 : Programme correct

```java
public class Test {
    public static void main(String[] args) {
        int compteur = 0;
        
        while (compteur < 5) {
            compteur++;
        }
    }
}
```

**Résultat :** ✅ PROGRAMME ACCEPTÉ

### Test 2 : Erreur lexicale

```java
public class Test {
    public static void main(String[] args) {
        int @erreur = 10;  // Caractère @ invalide
    }
}
```

**Résultat :** ❌ Erreur ligne 3, colonne 13 : Caractère invalide '@'

### Test 3 : Erreur syntaxique

```java
public class Test {
    public static void main(String[] args) {
        while x < 5 {  // Parenthèses manquantes
            x++;
        }
    }
}
```

**Résultat :** ❌ Erreur syntaxique : Parenthèse ouvrante '(' attendue

---

## 📊 Résultats de l'analyse

Lors de l'exécution, le compilateur affiche :

1. **Tokens reconnus** (analyse lexicale)
2. **Arbre d'analyse** (analyse syntaxique)
3. **Erreurs détectées** (lexicales et syntaxiques)
4. **Verdict final** (Programme accepté ou rejeté)

Exemple de sortie pour l'instruction WHILE :

```
========================================
[WHILE] *** ANALYSE DÉTAILLÉE DE WHILE ***
========================================
[WHILE] Mot-clé 'while' reconnu
[WHILE] Parenthèse ouvrante '(' trouvée
[WHILE] Analyse de la condition...
[WHILE] Condition analysée
[WHILE] Parenthèse fermante ')' trouvée
[WHILE] Analyse du bloc d'instructions...
[WHILE] Bloc analysé avec succès
[WHILE] *** FIN DE L'ANALYSE DE WHILE ***
========================================
```

---

## 🛠️ Technologies utilisées

- **Langage :** Java
- **IDE :** NetBeans
- **Paradigme :** Programmation orientée objet
- **Technique d'analyse :** Descente récursive
- **Grammaire :** LL(1)

---

## 📚 Concepts implémentés

### Analyse Lexicale
- Automate à états finis
- Matrice de transition
- Reconnaissance de patterns
- Gestion des commentaires

### Analyse Syntaxique
- Descente récursive
- Grammaires formelles (BNF)
- Gestion d'erreurs avec récupération
- Arbres de dérivation

---

## 📄 Livrables

- ✅ Code source (Java)
- ✅ Rapport PDF complet
- ✅ Fichier JAR exécutable
- ✅ Fichiers de test
- ✅ Documentation (ce README)

---

## 🎓 Compétences acquises

- Compréhension des phases de compilation
- Maîtrise des automates à états finis
- Pratique de la descente récursive
- Conception de grammaires formelles
- Gestion d'erreurs robuste
- Programmation Java avancée

---

## 📝 Licence

Ce projet est réalisé dans le cadre académique de l'Université A/ Mira de Béjaia.

---

## 📧 Contact

**Étudiant :** Malek Leiticia  
**Université :** A/ Mira de Béjaia  
**Département :** Informatique  
**Année :** 2024-2025  

---

## 🙏 Remerciements

Merci à l'enseignante du module Compilation pour l'encadrement et les consignes du projet.

---

**⭐ Si ce projet vous aide, n'hésitez pas à mettre une étoile !**
