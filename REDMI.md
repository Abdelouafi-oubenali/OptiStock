#  Documentation Finale – Spring Security & Basic Auth

##  Objectif général

Cette documentation vise à fournir une explication complète des concepts fondamentaux de **Spring Security**, de son architecture moderne (Spring 6+), du fonctionnement de Basic Auth, ainsi qu’une implémentation fonctionnelle adaptée à une API REST sécurisée.

Elle constitue le livrable final théorique du projet.

---

# 1 Fondamentaux de la Sécurité Web

##  Authentification vs Autorisation

* **Authentification** : Procédé permettant de vérifier l’identité d’un utilisateur (username/password, token, certificat…).
* **Autorisation** : Gestion des permissions. Détermine ce que l’utilisateur **peut** faire.

##  Attaques Web courantes

* **Brute Force** : Tentatives répétées pour deviner un mot de passe.
* **XSS** (Cross-Site Scripting) : Injection de scripts dans une page.
* **CSRF** (Cross-Site Request Forgery) : Exécution forcée d’une action par un utilisateur authentifié.
* **Session Fixation** : Imposition d’un ID de session avant authentification.
* **Session Hijacking** : Vol d’un cookie de session.

## 🔒 Importance du HTTPS

HTTPS apporte :

* chiffrement du trafic
* intégrité des données
* authentification serveur

Sans HTTPS, Basic Auth est **extrêmement vulnérable**.

## Defense in Depth

Une application sécurisée repose sur **plusieurs couches** :

* filtrage
* authentification
* autorisation
* hash des mots de passe
* validation des inputs
* protection réseau

## Sécurité Backend dans une API REST

Même si un frontend existe, **aucune requête ne doit être trusted by default**.
Le backend doit :

* valider les droits
* sécuriser les endpoints
* contrôler les identifiants

---

#  Architecture Moderne de Spring Security (Spring 6+)

## Les composants internes essentiels

### **SecurityFilterChain**

Définit les règles de sécurité : filtres, protections CSRF, CORS, règles d’accès.

### **DelegatingFilterProxy**

Intègre les filtres Spring dans le conteneur Servlet.

### **AuthenticationManager**

Coordonne l’authentification en appelant les `AuthenticationProvider`.

### **AuthenticationProvider**

Vérifie les identifiants fournis (mot de passe, token, etc.).

### **UserDetailsService**

Charge un utilisateur à partir d’une base de données ou d’un store mémoire.

### **PasswordEncoder**

Encode les mots de passe (BCrypt recommandé).

### **Rôles vs Authorities**

* **ROLE_ADMIN** (rôle) → devient **ADMIN** (authority)
* Spring ajoute automatiquement le préfixe `ROLE_`.

### Fin du WebSecurityConfigurerAdapter

Depuis Spring Security 5.7+, toutes les configurations se font via des **beans**.

## Schéma – Flux complet d'une requête sécurisée

(*À insérer en présentation*)

---

# 3️⃣ Configuration Moderne Spring Security

## Bean SecurityFilterChain

Définit :

* règles d’autorisation
* CSRF
* CORS
* sessions
* Basic Auth

## PasswordEncoder BCrypt

Garantit un hash sécurisé + sel automatique.

## Gestion des utilisateurs

* **InMemoryUserDetailsManager** : pour le POC
* **UserDetailsService custom** : en production

## Pipeline d’authentification

(*Schéma à ajouter*)

---

# 4️⃣ Basic Auth – Fonctionnement

## Définition

Basic Auth utilise un header :

```
Authorization: Basic base64(username:password)
```

## Fonctionnement Base64

Base64 n’est **pas** du chiffrement → simple encodage.

## Obligation HTTPS

Sans HTTPS → identifiants exposés en clair.

## BasicAuthenticationFilter

Filtre chargé de :

1. lire le header
2. décoder base64
3. vérifier identifiants via AuthenticationManager

## Limites en entreprise

* pas de renouvellement d’accès
* envoi des identifiants à chaque requête
* aucun mécanisme d’expiration

### Tableau des rôles

| Rôle              | Description                        |
| ----------------- | ---------------------------------- |
| ADMIN             | Gestion produits & administrateurs |
| WAREHOUSE_MANAGER | Inventaires & expéditions          |
| CLIENT            | Commandes & suivi                  |

---

# 5️⃣ CSRF, CORS & Sessions

##  CSRF

Utilisé uniquement dans les apps **stateful**.
Désactivé en REST.

## CORS

Autorise un front externe à appeler l’API.

## Sessions

* cookie `JSESSIONID`
* fixation de session empêchée par Spring Security
* logout supporté
* remember-me possible

---

# 6️⃣ Form Login – Fonctionnement (documenté uniquement)

Fonctionnement :

1. formulaire généré automatiquement
2. soumission vers `/login`
3. traitement via **UsernamePasswordAuthenticationFilter**
4. création d’une session
5. génération cookie `JSESSIONID`

### Comparaison Form Login / Basic Auth

| Form Login      | Basic Auth      |
| --------------- | --------------- |
| Stateful        | Stateless       |
| JSESSIONID      | Aucun cookie    |
| CSRF activé     | CSRF inutile    |
| Formulaire HTML | Encodage Base64 |

---

# 7️⃣ Architecture interne : UserDetails, Providers, Encoders

## UserDetails

Représente un utilisateur Spring.

## UserDetailsService

Charge l’utilisateur.

## AuthenticationProvider

Vérifie l’identité.

## BCryptPasswordEncoder

Applique hash + sel.

##  Bonnes pratiques

* Jamais de mot de passe en clair
* Toujours hashé
* Rotation régulière

---

# 8️⃣ Implémentation Basic Auth – POC

## Configuration

* SecurityFilterChain
* BCrypt encoder
* 3 rôles : ADMIN, WAREHOUSE_MANAGER, CLIENT

## Endpoints protégés

| Endpoint          | Rôle              |
| ----------------- | ----------------- |
| /api/products/**  | ADMIN             |
| /api/inventory/** | WAREHOUSE_MANAGER |
| /api/orders/**    | CLIENT            |
| /api/shipments/** | WAREHOUSE_MANAGER |
| /api/admin/**     | ADMIN             |

---

# 9️⃣ Tests & Validation

## cURL

```
curl -u admin:password http://localhost:8080/api/products
```

##  Postman

Authorization → Basic Auth

##  Statuts HTTP

* **200** : OK
* **401** : identifiants invalides
* **403** : rôle insuffisant

---

#* Conclusion Générale

Ce document synthétise l’ensemble des concepts, mécanismes internes et bonnes pratiques nécessaires pour comprendre et mettre en œuvre une sécurité complète avec Spring Security en mode **Basic Auth**. Il constitue la base théorique indispensable avant la montée en compétence vers des mécanismes plus avancés : JWT, OAuth2, Docker et CI/CD.



















## JWT documentation

JWT (JSON Web Token) est un **mécanisme d’authentification et d’autorisation** utilisé principalement dans les applications web et les API REST.

Un JWT est un token **auto‑contenu**, **signé cryptographiquement**, qui permet au serveur de vérifier l’identité et les droits d’un utilisateur sans stocker de session.

Un JWT est composé de **trois parties** séparées par des points (`.`) :

```
HEADER . PAYLOAD . SIGNATURE
```

---

## 1. HEADER

Le **Header** décrit la manière dont le token est signé.

Il contient généralement :

* `typ` : le type du token (JWT)
* `alg` : l’algorithme de signature utilisé

Exemple :

```json
{
  "typ": "JWT",
  "alg": "HS256"
}
```

👉 Le Header est encodé en **Base64Url**.

---

## 2. PAYLOAD

Le **Payload** contient les **claims**, c’est‑à‑dire les informations sur l’utilisateur et le token.

Exemple :

```json
{
  "sub": "123",
  "email": "user@test.com",
  "role": "ADMIN",
  "iat": 1700000000,
  "exp": 1700003600
}
```

### Types de claims

* **Registered claims** (standards) :

    * `sub` : identifiant de l’utilisateur
    * `iat` : date de création
    * `exp` : date d’expiration
    * `iss` : émetteur du token

* **Public claims** :

    * rôles, permissions, etc.

* **Private claims** :

    * données spécifiques à l’application

⚠️ Le Payload **n’est pas chiffré**, seulement encodé. Il ne faut jamais y mettre des données sensibles (mot de passe, numéro de carte, etc.).

---

## 3. SIGNATURE

La **Signature** garantit l’intégrité et l’authenticité du token.

Elle est calculée comme suit :

```
HMACSHA256(
  base64UrlEncode(HEADER) + "." + base64UrlEncode(PAYLOAD),
  SECRET_KEY
)
```

👉 Si le Header ou le Payload est modifié, la signature devient invalide.

---

## 4. Fonctionnement du JWT

### Étape 1 : Authentification

L’utilisateur envoie ses identifiants (login / mot de passe).

### Étape 2 : Génération du JWT

Si les identifiants sont valides, le serveur génère un JWT et le renvoie au client.

### Étape 3 : Stockage côté client

Le token est stocké :

* dans un **HttpOnly Cookie** (recommandé)
* ou dans le stockage local du navigateur

### Étape 4 : Requêtes suivantes

Le client envoie le token dans l’en‑tête HTTP :

```
Authorization: Bearer <JWT>
```

### Étape 5 : Vérification côté serveur

Le serveur :

1. vérifie la signature
2. vérifie l’expiration (`exp`)
3. lit les permissions

---

## 5. Avantages du JWT

* Stateless (pas de session côté serveur)
* Performant et scalable
* Adapté aux API REST
* Compatible avec les microservices

---

## 6. Inconvénients

* Impossible à invalider facilement avant expiration
* Risque en cas de vol du token
* Taille plus grande qu’un identifiant de session

---

## 7. Bonnes pratiques de sécurité

* Toujours utiliser HTTPS
* Durée de vie courte pour l’Access Token
* Utiliser un Refresh Token
* Stocker le token dans un cookie HttpOnly
* Utiliser des algorithmes sécurisés (RS256)

---

## Conclusion

JWT est une solution moderne et efficace pour gérer l’authentification et l’autorisation dans les applications web, à condition de respecter les bonnes pratiques de sécurité.
