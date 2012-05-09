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
        deletion_in_progress: "Suppression en cours...",
        modification_in_progress: "Modification en cours...",
        no_album: "Aucun album n'a été créé pour le moment !"
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
    administration: {
        title: "Administration",
        processing: "Traitement en cours...",
        config: {
            title: "Configuration générale",
            tab: "Configuration générale",
            galery_title: "Titre de la galerie photos",
            galery_title_placeholder: "Exemple : Galerie photos personnelle",
            source_directory: "Répertoire source (contenant les photos au format original) ",
            source_directory_placeholder: "Exemple : /home/user/photos/",
            target_directory: "Répertoire de travail (qui contiendra notamment les vignettes des photos) ",
            target_directory_placeholder: "Exemple : /home/user/thumbnails/",
            save: "Enregistrer",
            save_and_sync: "Enregistrer et synchroniser les répertoires"
        },
        albums: {
            title: "Liste des albums",
            tab: "Gestion des albums photos",
            photo_number: "Nombre de photos ",
            downloadable: "Téléchargeable",
            not_downloadable: "N'est pas téléchargeable"
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
        },
        tokens: {
            title: "Gestion des accès visiteurs",
            tab: "Gestion des accès visiteurs",
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
            }
        },
        account: {
            title: "Modifier mes identifiants de connexion",
            tab: "Gestion des identifiants de connexion",
            username: "Nom d'utilisateur ",
            old_password: "Ancien mot de passe ",
            new_password: "Nouveau mot de passe ",
            password_repeat: "Confirmation du nouveau mot de passe "
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
                album_visibility: "Visibilité de l'album",
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
            }
        }
    },
    footer: "&COPY;&nbsp;Toutes les photos sont soumises au droit d'auteur. Il est interdit de les réutiliser sans l'accord explicite de leur auteur - Ce site Internet utilise le projet libre <a href='http://projects.debux.org/projects/debox-photos'>debox-photos</a>",
    header: {
        album_list: "Liste des albums",
        administration: "Administration",
        disconnection: "Déconnexion",
        connection: "Connexion",
        username: "Nom d'utilisateur",
        password: "Mot de passe"
    },
    home: {
        title: "Accueil"
    },
    photo: {
        thumbnails: {
            admin: {
                cover_choice: "Choix d'une photo de couverture"
            }
        }
    }
};