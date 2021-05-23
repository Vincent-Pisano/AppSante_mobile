# AppSante_mobile

Ce projet a été réalisé dans le cadre du cours de **Programmation Mobile 420-456-AL**

AppSante est une application mobile permettant la distribution efficace de permit à ses membres.

Tout droit réservé - RENAUD Vincent

# Utilisation 

Description des étapes d'utilisations de l'application.

## 1. Mise en place de l'environnement

### Pour faire fonctionner l'application, vous aurez besoin de :
* L'application Spring **AppSante** d'active (420-445-AL)
* L'application Spring **Ramq** d'active (420-445-AL)
* Un serveur **Bitnami** Fonctionnel
			
### Dans la classe .StartApplication (com.renaud.appsante.service) :
* Indiquez votre **clé client** 
* Indiquez votre **server** (Bitnami)

## 2.  explication des Activities

### 1. Main Activity
Main Activity est un "Splash screen",  elle n'est utile que pour un effet visuel

#### Chemin(s) :

*Envoie automatiquement l'utilisateur dans **Login Activity** après 3 secondes*

---
### 2. Login Activity
Login Activity va tester la connexion de l'utilisateur en fonction des données entrées :
1. Email
2. Password

Les données sont présentent dans le serveur Bitnami.

#### Chemin(s) :

*En cas d'absence de compte, l'utilisateur pourra être redirigé vers **SignUp Activity***
*Si la connexion est un succès, l'utilisateur sera envoyer vers **Dashboard Activity***

---
### 3. SignUp Activity

SignUp Activity va permettre la création d'un compte dans la base de données du serveur Bitnami
Les données **doivent** être valides avec les citoyens enregistrés dans la base de données de **l'application Ramq**, accessible depuis des **Appels HTTP à l'application Spring AppSante**

**/!\ Toutes les données sont obligatoires pour la créations d'un compte /!\**

Les données valides auprès de la Ramq sont :
1. Nas
2. prénom
3. nom de famille
4. date de naissance
5. mot de passe

Les données restantes sont :

1. courriel
2. numéro de téléphone

#### Chemin(s) :

*En cas de possession d'un compte, l'utilisateur pourra être redirigé vers **Login Activity***
*Si la connexion est un succès, l'utilisateur sera envoyer vers **Dashboard Activity***

---

### 4. Dashboard Activity
Dashboard Activity est l'activité principale de l'application, elle s'occupe de 3 fonctionnalité :

#### 1. Recherche et affichage d'un permis
On va chercher si un permit existe dans la base de données du serveur Bitnami :
- s'il existe un permit : on affiche ses informations ainsi que son code QR
 *PS : l'utilisateur peut **enregistrer** son permis dans les fichiers de son téléphone en cliquant sur le code QR du permit* 
- s'il n'existe pas de permit: on signale à l'utilisateur qu'il n'a pas de permit enregistré à son nom 

#### 2. Demande de Permit
Envoie une vérification à la Ramq et enregistre le permis dans la base de données du serveur Bitnami si un permis à été trouvée, effectue ensuite l'étape 1.

Si aucun permis n'a été trouvé, on informe l'utilisateur
#### 3. Accès au Drawer Menu
*voir description Drawer menu ci dessous*

---
### 5. Profile Activity
Profile Activity permet à l'utilisateur d'accéder à ses informations personnelles
#### 1. Affichage des informations
On affiche les informations du compte de l'utilisateur, sauf son mot de passe qu'on laisse caché

#### 2. Accès au Drawer Menu
*voir description Drawer menu ci dessous*

---
### 6. SPECIAL Drawer Menu
Drawer Menu n'est pas une activité mais un menu accessible depuis les Activités Dashboard et Profile

*Une icon de menu est disponible au dessus de l'icon de l'application permettant de faire apparaitre un menu glissant depuis le coté gauche de l'écran.*

Les options disponibles sont :
1. allez au home (DashboardActivity)
2. faire une demande de permis
3. allez au profile (ProfileActivity)
4. se déconnecter de l'application (MainActivity)
5. Partager l'application
6. Noter l'application

## Support

Pour toutes questions, je suis joignable par Teams/MIO

## Statuts 

Le développement de cette application ne dépassera pas la date de remise limite de ce travail, où le 02 juin 2021