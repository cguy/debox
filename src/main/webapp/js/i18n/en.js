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
        warning: "Warning",
        error: "Error",
        close: "Close",
        mandatory: "Mandatory",
        loading : "Loadgin...",
        back2home: "Back to home",
        photo: "photo",
        photos: "photos",
        "public": "Public",
        "private": "Private",
        cancel: "Cancel",
        validate: "Validate",
        modify: "Edit",
        "delete": "Delete",
        deletion: "Deletion",
        deletion_in_progress: "Deletion in progress...",
        creation_in_progress: "Creation in progress...",
        modification_in_progress: "Modification in progress...",
        no_album: "No albums have been created yet!",
        noPhotos: "No photo",
        date: "Date",
        mail: "Mail address",
        firstname: "First name",
        lastname: "Last name",
        password: "Password",
        password_confirm: "Password confirm",
        connection: "Login",
        next: "Next",
        previous: "Previous"
    },
    registration : {
        title: "Register",
        subtitle: "Register and create your gallery",
        providerPrefix: "Register with my account ",
        altChoice: "or register with your mail address",
        placeholder: {
            firstname: "Your first name",
            lastname: "Your last name",
            mail: "Please enter your mail address",
            password: "Please enter your password"
        },
        finish: "Finish registration",
        errors : {
            internal: "An error occured during regristration, please try again or contact the webmaster.",
            alreadyRegistered: "Unable to finish registration, the entered mail addresse is already used."
        },
        note: "Note : your first name and your last name are only used to show your identity on media you interact with. You do not have to enter your real identity, although it is advisable for allowing your friends recognize you.",
        success: "Your account has been successfully created, you can now access to <a href=\"#/account\">your account</a> or start to <a href=\"#/administration/upload\">add your photos</a>."
    },
    signin : {
        title: "Login",
        subtitle: "Login and access to your gallery or your friends galleries",
        providerPrefix: "Login with my account ",
        altChoice: "or login with your credentials"
    },
    about : {
        tooltip: "About"
    },
    error403: {
        title: "Restricted access",
        message: "You have to authenticate to access to the wanted resource."
    },
    error404: {
        title: "Not found",
        message: "The wanted resource doesn't exist"
    },
    error500: {
        title: "Error",
        message: "An internal server error occured."
    },
    account : {
        title: "My account",
        albums: {
            title: "Albums",
            tab: "My albums",
            photo_number: "Photos count",
            downloadable: "Downloadable",
            not_downloadable: "Is not downloadable"
        },
        settings: {
            tab: "Settings",
            title: "Settings",
            hosting: {
                title: "Photos hosting",
                source: "Photos hosting",
                add: "I want to specify where are my photos on the Internet"
            },
            albums: {
                title: "Default albums settings"
            },
            photos: {
                title: "Default photos settings"
            }
        },
        upload: {
            title: "Add photos",
            tab: "Add photos",
            add: "Add photos",
            createAlbum: "Create a new album",
            mandatory: "Please choose an existing album or create a new one.",
            action: "Start photos upload",
            "album": "Please select the album that you want to add photos",
            "photos": "Add your photos",
            targetDirectory : "Photos will be added into the folllowing album : ",
            "start": "Start upload",
            "errors": {
                "albumCreation": "An error occurred during the album creation.",
                "maxFileSize": "The file is too big",
                "minFileSize": "The file is too small",
                "acceptFileTypes": "This file type is not supported",
                "maxNumberOfFiles": "You have exceeded the number of files allowed",
                "uploadedBytes": "The number of bytes sent exceeds the file size",
                "emptyResult": "Empty file"
            },
            "form": {
                title: "Album creation",
                "albumName": "Album name:",
                namePlaceHolder: "Please enter the new album name",
                "subAlbum": "Sub-album:",
                "parent": {
                    "false": "The album will not be a sub-album, it will be available in the album list.",
                    "true": "The album will be a sub-album, please choose the album that will contain this sub-album.",
                    "error": "To create a sub-album, you must specify the parent album."
                }
            },
            "error": "An error occured",
            "existingAlbum": "I want to add photos in an existing album",
            "newAlbum": "I want to create a new album"
        },
        tokens: {
            title: "Visitors accesses management",
            tab: "Visitors accesses",
            
            thirdparty : {
                title: "Add thirdparty accounts to manage your contacts accesses",
                provider : {
                    name: "Service name",
                    identifier: "Account name",
                    deletion: "Delete this thirdparty account",
                    remove: {
                        title: "Delete a thirdparty account",
                        message: "Are you sure you want to delete this thirdparty account?"
                    }
                }
            },
            
            token_list: "Visitors accesses",
            label: "Label",
            albums: "Albums",
            link2share: "Link to share",
            actions: "Actions",
            visible_albums: "Visible albums with this access",
            public_album: " <span class=\"bold\">(public album)</span>",
            link: "Link",
            no_token: "There is not any visitor access yet.Aucun accès visiteur n'a été créé !",
            new_token: "Create a new access",
            create_token: "Create the access",
            new_token_label: "New access label",
            edit: {
                title: "Edit an access",
                label: "Access label",
                success: "Visible albums list linked to this access has been successfully edited.",
                error: "An error occured during visible albums list modification."
            },
            "delete": {
                title: "Delete a visitor access",
                message: "Are you sure you want to delete this visitor access?"
            },
            reinit: {
                label: "Reset",
                label_in_progress: "Reset in progress",
                title: "Reset a visitor access",
                message: "Are you sure you want to reset this visitor access?",
                description: "A new link will be generated, deleting the old access. The new link will allow access to the same albums list than the old one.",
                success: "The access has been successfully reset.",
                error404: "The access you try to reset doesn't exist.",
                error: "An error occured during the access reset, please retry later."
            }
        },
        personaldata: {
            tab: "My personal data",
            title: "My personal data",
            edit: "Modify my identity",
            username: "Username:",
            firstname: "First name:",
            lastname: "Last name:",
            old_password: "Old password:",
            new_password: "New password:",
            passwordChange: "Edit my password",
            accountDeletion: {
                title: "Delete my account",
                message: "Are you sure you want to delete your account? If you delete your account, all data linked to your account will be delete (photos, albums, comments, etc.).",
                irreversible: "Warning, this action is irreversible!"
            }
        }
    },
    administration: {
        title: "Administration",
        processing: "Processing...",
        config: {
            title: "Overall configuration",
            tab: "Overall configuration",
            galery_title: "Gallery title:",
            galery_title_placeholder: "Example : Personal photo gallery",
            directory: "Directory that will contain photos added by the users:",
            directory_placeholder: "Example : /home/user/photos/",
            save: "Save"
        },
        sync: {
            title: "Synchronize directories",
            tab: "Synchronize directories",
            in_progress: "Synchronizing ...",
            cancel: "Cancel the synchronization",
            choice_mode: "Please choose the synchronization mode, then launch process ",
            fastest: "Fastest ",
            fastest_description: "Thumbnails will be generated only at first access.",
            normal: "Normal ",
            normal_description: "Thumbnails will be generated only for new photos.",
            longest: "Longest ",
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
        title: "Commentaires",
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