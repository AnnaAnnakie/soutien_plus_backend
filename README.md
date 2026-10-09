# Spring Boot Project

Ce projet utilise le framework **Spring Boot**, une solution puissante pour développer rapidement des applications web ou backend en Java. Spring Boot simplifie la configuration et l'intégration des composants nécessaires au fonctionnement d'une application.

## Fonctionnement de Spring Boot

Dans Spring Boot, chaque classe Java peut représenter une entité de la base de données en utilisant l'annotation `@Entity`. Cela permet de mapper directement une classe aux tables de la base de données, simplifiant ainsi la gestion des données.

### Points clés :

1. **Annotations :**  
   Spring Boot repose sur les annotations pour simplifier les tâches complexes. Par exemple :  
   - `@Entity` : Définit une classe comme une entité de base de données.  
   - `@Repository`, `@Service`, `@Controller` : Déclarent les couches de l'application.  
   - Lombok, avec l'annotation `@Data`, génère automatiquement les getters, setters, et autres méthodes utiles (comme `toString` et `equals`), réduisant ainsi le code boilerplate.

2. **Spring Data JPA :**  
   Simplifie l'accès à la base de données grâce à des interfaces comme `JpaRepository`, qui permettent de réaliser des opérations CRUD sans écrire de requêtes SQL explicites.

3. **Spring Security :**  
   Fournit des outils robustes pour sécuriser l'application, comme la gestion des utilisateurs et des rôles, ainsi que l'authentification et l'autorisation.

4. **JWT (JSON Web Token) :**  
   Permet de gérer l'authentification avec des tokens sécurisés, adaptés aux applications RESTful.

## Technologies et dépendances utilisées

Voici les principales dépendances de ce projet :

1. **Spring Security** : Pour gérer la sécurité, y compris l'authentification et l'autorisation.
2. **Lombok** : Pour réduire le code boilerplate en générant automatiquement les méthodes comme `getters`, `setters`, etc.
3. **Spring Web** : Pour créer des API RESTful.
4. **Spring Data JPA** : Pour interagir facilement avec la base de données.
5. **PostgreSQL** : La base de données relationnelle utilisée dans ce projet.
6. **JWT (JSON Web Token)** : Pour sécuriser les API avec des tokens.

## Structure du projet

1. **Entités (Entities)** :  
   Chaque entité représente une table dans la base de données et est annotée avec `@Entity`.

2. **Dépôts (Repositories)** :  
   Les interfaces héritent de `JpaRepository` pour gérer les opérations CRUD.

3. **Services** :  
   La logique métier est centralisée ici. Annotés avec `@Service`, ces composants gèrent les traitements entre les contrôleurs et les dépôts.

4. **Contrôleurs (Controllers)** :  
   Exposent les endpoints RESTful pour interagir avec l'application. Annotés avec `@RestController`.

5. **Sécurité** :  
   La configuration de Spring Security permet de protéger les ressources de l'application. JWT est utilisé pour gérer les sessions utilisateur.

## Exécution du projet

### Prérequis :
- **Java 17+**
- **Maven** (ou Gradle)
- **PostgreSQL** installé et configuré

### Étapes :
1. Clonez le projet :  
   ```bash
   git clone <URL_DU_REPO>
