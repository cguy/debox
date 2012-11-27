/*
 * #%L
 * debox-photos
 * %%
 * Copyright (C) 2012 Debox
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
var fr = {
    common: {
        warning: "Attention",
        error: "Erreur",
        close: "Fermer",
        mandatory: "Obligatoire",
        loading : "Chargement en cours ...",
        back2home: "Retour à l'accueil",
        photo: "photo",
        photos: "photos",
        "public": "Public",
        "private": "Privé",
        cancel: "Annuler",
        validate: "Valider",
        modify: "Modifier",
        "delete": "Supprimer",
        deletion: "Suppression",
        deletion_in_progress: "Suppression en cours...",
        creation_in_progress: "Création en cours...",
        modification_in_progress: "Modification en cours...",
        no_album: "Aucun album n'a été créé pour le moment !",
        noPhotos: "Aucune photo",
        date: "Date",
        mail: "Adresse e-mail",
        firstname: "Prénom",
        lastname: "Nom de famille",
        password: "Mot de passe",
        password_confirm: "Confirmation du mot de passe",
        connection: "Connexion",
        next: "Suivant",
        previous: "Précédent"
    },
    registration : {
        title: "Créer un compte",
        subtitle: "Inscrivez-vous et créez votre photothèque",
        providerPrefix: "M'inscrire avec mon compte ",
        altChoice: "ou créez votre compte avec votre adresse e-mail",
        placeholder: {
            firstname: "Votre prénom",
            lastname: "Votre nom de famille",
            mail: "Veuillez saisir votre adresse e-mail",
            password: "Veuillez saisir votre mot de passe"
        },
        finish: "Terminer la création de mon compte",
        errors : {
            internal: "Une erreur est survenue lors de l'enregistrement de votre compte, veuillez réessayer ou contacter le responsable du site.",
            alreadyRegistered: "Impossible de finir votre inscription, l'adresse e-mail saisie est déjà utilisée.",
        },
        note: "Note : votre prénom et votre nom ne sont utilisés que pour afficher votre identité sur des médias qui vont sont liés. Vous n'êtes pas obligé de saisir votre réelle identité, bien que ça soit conseillé pour que vos proches vous reconnaissent.",
        success: "Votre compte a bien été créé, vous pouvez d'ores et déjà accéder à <a href=\"#/account\">votre compte</a> ou commencer à <a href=\"#/administration/upload\">ajouter vos photos</a>."
    },
    signin : {
        title: "Connexion à votre compte",
        subtitle: "Connectez-vous et accéder à votre photothèque ou celle de vos amis",
        providerPrefix: "Me connecter avec mon compte ",
        altChoice: "ou connectez-vous avec vos identifiants"
    },
    about : {
        tooltip: "En savoir plus"
    },
    error403: {
        title: "Authentification nécessaire",
        message: "Vous devez vous connecter pour accéder à la ressource demandée."
    },
    error404: {
        title: "Ressource non trouvée",
        message: "La ressource demandée n'existe pas."
    },
    error500: {
        title: "Erreur",
        message: "Une erreur interne au serveur est survenue."
    },
    account : {
        title: "Mon compte",
        albums: {
            title: "Liste des albums",
            tab: "Mes albums photos",
            photo_number: "Nombre de photos ",
            downloadable: "Téléchargeable",
            not_downloadable: "N'est pas téléchargeable"
        },
        settings: {
            tab: "Réglages",
            title: "Réglages & paramètres",
            hosting: {
                title: "Stockage des photos",
                source: "Stockage des photos",
                add: "Je veux préciser où trouver mes photos déjà présentes sur Internet"
            },
            albums: {
                title: "Paramètres par défaut des albums"
            },
            photos: {
                title: "Paramètres par défaut des photos"
            }
        },
        upload: {
            title: "Ajouter des photos",
            tab: "Ajout de photos",
            add: "Ajouter des photos",
            createAlbum: "Créer un nouvel album",
            mandatory: "Veuillez choisir ou créer l'album dans lequel vous voulez ajouter vos photos.",
            action: "Commencer le chargement des photos",
            "album": "Choisissez l'album dans lequel vous voulez ajouter des photos",
            "photos": "Ajoutez vos photos",
            targetDirectory : "Les photos seront ajoutées à l'album : ",
            "start": "Commencer le chargement",
            "errors": {
                "albumCreation": "Une erreur est survenue pendant la création de l'album.",
                "maxFileSize": "Le fichier est trop gros",
                "minFileSize": "Le fichier est trop petit",
                "acceptFileTypes": "Ce type de fichier n'est pas accepté",
                "maxNumberOfFiles": "Vous avez dépassé le nombre de fichiers autorisé",
                "uploadedBytes": "Le nombre d'octets envoyés dépasse la taille du fichier",
                "emptyResult": "Fichier vide"
            },
            "form": {
                title: "Création d'un album",
                "albumName": "Nom de l'album :",
                namePlaceHolder: "Veuillez entrer le nom du nouvel album",
                "subAlbum": "Sous-album :",
                "parent": {
                    "false": "L'album ne sera pas un sous-album, il sera accessible directement dans la liste des albums.",
                    "true": "L'album sera un sous-album, choisissez l'album qui contiendra ce sous-album.",
                    "error": "Pour que l'album créé soit un sous-album, vous devez spécifier l'album qui contiendra ce sous-album."
                }
            },
            "error": "Une erreur est survenue",
            "existingAlbum": "Je veux ajouter des photos dans un album existant",
            "newAlbum": "Je veux créer un nouvel album"
        },
        tokens: {
            title: "Gestion des accès visiteurs",
            tab: "Gestion des accès visiteurs",
            
            thirdparty : {
                title: "Ajouter des comptes tiers pour gérer les accès de vos contacts",
                provider : {
                    name: "Nom du service",
                    identifier: "Nom du compte",
                    deletion: "Supprimer l'accès à ce compte",
                    remove: {
                        title: "Suppression de l'accès à un compte tiers",
                        message: "Êtes-vous sûr de vouloir supprimer l'accès à ce compte ?"
                    }
                }
            },
            
            token_list: "Liste des accès visiteurs",
            label: "Libellé",
            albums: "Albums",
            link2share: "Lien à partager",
            actions: "Actions",
            visible_albums: "Voir les albums visibles via cet accès",
            public_album: " <span class=\"bold\">(album public)</span>",
            link: "Lien",
            no_token: "Aucun accès visiteur n'a été créé !",
            new_token: "Créer un nouvel accès visiteur",
            create_token: "Créer l'accès",
            new_token_label: "Libellé du nouvel accès",
            edit: {
                title: "Modifier un accès visiteur",
                label: "Libellé de l'accès visiteur",
                success: "La liste des albums visibles via cet accès a été modifée avec succès.",
                error: "Erreur durant la modification de la liste des albums visibles via cet accès."
            },
            "delete": {
                title: "Supprimer un accès visiteur",
                message: "Êtes-vous sûr de vouloir supprimer l'accès visiteur"
            },
            reinit: {
                label: "Réinitialiser",
                label_in_progress: "Réinitialisation en cours",
                title: "Réinitialiser un accès visiteur",
                message: "Êtes-vous sûr de vouloir Réinitialiser l'accès visiteur",
                description: "Un nouveau lien sera généré, interdisant l'accès à ceux qui utilisent le lien précédent. Le nouveau lien donnera accès aux mêmes albums que l'ancien.",
                success: "L'accès a été réinitialisé avec succès.",
                error404: "L'accès que essayez de réinitialiser n'existe pas.",
                error: "Erreur pendant la réinitialisation de l'accès, veuillez réessayer ultérieurement."
            }
        },
        personaldata: {
            tab: "Mes informations personnelles",
            title: "Mes informations personnelles",
            edit: "Modifier mon identité",
            username: "Nom d'utilisateur :",
            firstname: "Prénom :",
            lastname: "Nom de famille :",
            old_password: "Ancien mot de passe :",
            new_password: "Nouveau mot de passe :",
            passwordChange: "Modifier mon mot de passe",
            accountDeletion: {
                title: "Supprimer mon compte",
                message: "Êtes-vous sûr / sure de vouloir supprimer votre compte ? Si vous supprimez votre compte, l'intégralité des informations qui vous sont liées seront également supprimées (photos, albums, commentaires, etc.)",
                irreversible: "Attention, cette action est irréversible !"
            }
        }
    },
    administration: {
        title: "Administration",
        processing: "Traitement en cours...",
        config: {
            title: "Configuration générale",
            tab: "Configuration générale",
            galery_title: "Titre de la galerie photos :",
            galery_title_placeholder: "Exemple : Galerie photos personnelle",
            directory: "Répertoire distant qui contiendra les photos ajoutées par les utilisateurs :",
            directory_placeholder: "Exemple : /home/user/photos/",
            save: "Enregistrer"
        },
        sync: {
            title: "Synchroniser les répertoires",
            tab: "Synchronisation des répertoires",
            in_progress: "Synchronisation en cours",
            cancel: "Annuler la synchronisation",
            choice_mode: "Veuillez choisir le mode de synchronisation et confirmer la demande ",
            fastest: "Le plus rapide ",
            fastest_description: "Aucune pré-génération des vignettes. Les vignettes seront générées lors de leur premier accès.",
            normal: "Normal ",
            normal_description: "Pré-génération des vignettes pour les nouvelles photos.",
            longest: "Le plus long ",
            longest_description: "Regénération des vignettes existantes + création des vignettes pour les nouvelles photos.",
            force_check_dates: "Forcer la vérification des dates des photos existantes",
            warning: "Ce dernier mode de synchronisation supprimera toutes les vignettes existantes avant de les regénérer.",
            launch: "Lancer la synchronisation"
        }
    },
    album: {
        from_date: "du",
        to_date: "au",
        on_date: "le",
        back2album: "Retour à l'album ",
        back2albums: "Retour à la liste des albums",
        download: "Télécharger les photos de cet album",
        reduced_size: "Taille réduite des photos",
        original_size: "Taille originale des photos",
        subalbums: "Sous-albums",
        no_photos: "Il n'y a aucune photo disponible pour cet album.",
        admin: {
            title: "",
            edit: {
                modify_this: "Modifier cet album",
                close_notif_zone: "Fermer la zone de modification",
                modify: "Modifier l'album",
                album_name: "Nom de l'album",
                album_description: "Description de l'album",
                album_visibility: "Visibilité de l'album",
                album_authorized_tokens: "Accès visiteurs",
                album_authorized_tokens_placeholder: "Sélectionnez les accès visiteur",
                download: "Téléchargement",
                download_description: "Les photos de cet album sont téléchargeables par les personnes ayant accès à cet album.",
                actions: "Actions",
                choose_cover: {
                    button: "Choisir une couverture pour l'album",
                    title: "Choix de la couverture de l'album",
                    tooltip: "Cliquez sur la photo pour qu'elle devienne la couverture de cette album",
                    photos: "Photos",
                    success: "La couverture de l'album a été modifiée avec succès.",
                    error: "Une erreur est survenue lors de la modification de la couvreture de cet album."
                },
                cancel_cover_choice: "Annuler le choix de la couverture",
                regenerate_thumbnails: "Regénérer les vignettes de cet album",
                success: "L'album a été modifié avec succès.",
                error: "Une erreur est survenue lors de la modification de l'album."
            },
            "delete" : {
                action: "Supprimer l'album",
                confirm : {
                    title: "Confirmation",
                    body: "Êtes-vous sûr / sure de vouloir supprimer cet album ?"
                },
                error: "Une erreur est survenue durant la suppression de l'album."
            }
        }
    },
    header: {
        album_list: "Liste des albums",
        administration: "Administration",
        settings: "Mon compte",
        disconnection: "Déconnexion",
        connection: "Connexion",
        connection_others: "Vous pouvez également vous connecter avec votre compte :",
        connection_in_progress: "Connexion en cours ...",
        username: "Nom d'utilisateur",
        password: "Mot de passe",
        register: "Créer un compte"
    },
    home: {
        title: "Accueil"
    },
    photo: {
        title: "Titre de la photo",
        thumbnails: {
            admin: {
                cover_choice: "Choix d'une photo de couverture"
            }
        },
        "delete" : {
            title: "Suppression de la photo",
            confirm: "Êtes-vous sûr / sure de vouloir supprimer cette photo ?"
        },
        edit: {
            title: "Modification d'une photo",
            placeholder: "Veuillez rentrer le titre de la photo"
        }
    },
    comments: {
        show: "Afficher les commentaires",
        hide: "Cacher les commentaires",
        placeholder: "Laissez un commentaire ...",
        remove: "Supprimer ce commentaire",
        confirm: "Êtes-vous sûr de vouloir supprimer ce commentaire ?",
        empty: {
            album: "Aucun commentaire n'a encore été laissé sur cet album",
            photo: "Aucun commentaire n'a encore été laissé sur cette photo"
        }
    },
    slideshow: {
        exit: "Quitter le diaporama"
    },
    install : {
        title: "Installer debox sur votre serveur",
        introduction: {
            title: "Introduction",
            presentation: "L'installation de debox sur votre serveur se fera en plusieurs étapes :",
            steps : [
                "Configuration de la base de données MySQL",
                "Configuration de l'espace de travail (stockage & travail sur les photos)",
                "Création du compte administrateur"
            ]
        },
        steps: [
            {
                title: "Étape n°1 / 3 : Configuration de la base de données MySQL",
                form: {
                    host : {
                        label: "Adresse du serveur MySQL :",
                        default : "Valeur par défaut : 127.0.0.1"
                    },
                    port : {
                        label: "Port d'accès au serveur MySQL :",
                        default : "Valeur par défaut : 3306"
                    },
                    name : {
                        label: "Nom de la base de données :",
                        default : "Valeur par défaut : debox"
                    },
                    username: {
                        label : "Nom de l'utilisateur MySQL :",
                        default : "Valeur par défaut : root"
                    },
                    password: {
                        label : "Mot de passe de l'utilisateur MySQL :",
                        default : "Valeur par défaut : Pas de mot de passe"
                    },
                    next: "Valider la connexion à la base de données"
                }
            },
            {
                title: "Étape n°2 / 3 : Configuration de l'espace de travail",
                introduction : "Vous devez spécifier un répertoire local sur votre serveur qui contiendra notamment les photos téléchargées sur votre application debox.\n\
                                Ce répertoire doit pouvoir être créé par votre serveur d'application. Si ce répertoire est déjà créé, \n\
                                votre serveur d'application doit pouvoir en lire et écrire son contenu.",
                note: "Attention, le répertoire local de travail doit être vide afin que l'application debox puisse créer l'arborescence nécessaire à son bon fonctionnement.",
                path: {
                    label : "Répertoire de travail :"
                },
                next: "Vérifier l'accès au répertoire saisi"
            },
            {
                title: "Étape n°3 / 3 : Création du compte administrateur",
                form: {
                    firstname : {
                        label: "Prénom : ",
                        placeholder : "Nécessaire pour afficher votre identité"
                    },
                    lastname : {
                        label: "Nom de famille : ",
                        placeholder : "Nécessaire pour afficher votre identité"
                    },
                    username : {
                        label: "Votre adresse e-mail : ",
                        placeholder : "Nécessaire pour vous connecter à votre application debox"
                    },
                    password: {
                        label : "Votre mot de passe de connexion : ",
                        placeholder : "Nécessaire pour vous connecter à votre application debox"
                    },
                    next: "Valider la création du compte administrateur"
                }
            },
            {
                title: "Installation de votre application debox terminée avec succès",
                introduction : "Vous avez terminé l'installation de votre application debox, vous pouvez dès maintenant vous connecter avec votre compte par le lien ci-dessous :",
                go : "Me connecter à mon compte"
            }
        ]
    }
};