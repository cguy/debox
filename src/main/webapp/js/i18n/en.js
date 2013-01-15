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
        video: "video",
        videos: "videos",
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
        previous: "Previous",
        and: "and"
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
            longest_description: "Override existing thumbnails + create thumbnails for new photos.",
            force_check_dates: "Force the shooting date check for existing photos",
            warning: "This last one will delete all existing thumbnails before create them.",
            launch: "Process"
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
        }
    },
    album: {
        from_date: "from",
        to_date: "to",
        on_date: "the",
        back2album: "Back to the album ",
        back2albums: "Back to the list of albums",
        download: "Download this album",
        reduced_size: "Small size of photos",
        original_size: "Original size of photos",
        subalbums: "Sub-albums",
        no_photos: "There is not any available photos for this album.",
        admin: {
            title: "",
            edit: {
                modify_this: "Edit this album",
                close_notif_zone: "Close the edition area",
                modify: "Edit",
                album_name: "Name",
                album_description: "Description",
                album_visibility: "Privacy",
                album_authorized_tokens: "Visitors accesses",
                album_authorized_tokens_placeholder: "Select authorized visitors",
                download: "Download",
                download_description: "These album photos are downloadable by any people who has access to this album.",
                actions: "Actions",
                choose_cover: {
                    button: "Choose the album cover",
                    title: "Album cover choice",
                    tooltip: "Click this photo for it becomes the album cover",
                    photos: "Photos",
                    success: "The album cover has been successfully edited.",
                    error: "An error occurred during the choice of album cover."
                },
                cancel_cover_choice: "Cancel the album cover choice",
                regenerate_thumbnails: "Regenerate the photos thumbnails",
                success: "The album has been successfully edited.",
                error: "An error occured editing the album."
            },
            "delete" : {
                action: "Delete the album",
                confirm : {
                    title: "Confirm",
                    body: "Are you sure you want to delete this album?"
                },
                error: "An error occured during album deletion."
            }
        }
    },
    header: {
        album_list: "Albums",
        administration: "Administration",
        settings: "My account",
        disconnection: "Log out",
        connection: "Log in",
        connection_others: "You could also log in with your account:",
        connection_in_progress: "Authenticating in progress...",
        username: "Username",
        password: "Password",
        register: "Register"
    },
    home: {
        title: "Home"
    },
    photo: {
        title: "Photo title",
        "delete" : {
            title: "Photo deletion",
            confirm: "Are you sure you want to delete this photo?"
        },
        edit: {
            title: "Photo edition",
            placeholder: "Please enter the photo title"
        }
    },
    comments: {
        title: "Comments",
        show: "Show comments",
        hide: "Hide comments",
        placeholder: "Leave a comment",
        remove: "Delete this comment",
        confirm: "Are your sure you want to delete this comment?",
        empty: {
            album: "There is not any comment for this album",
            photo: "There is not any comment for this photo"
        }
    },
    slideshow: {
        exit: "Exit the slideshow"
    },
    install : {
        title: "Install debox on your server",
        introduction: {
            title: "Introduction",
            presentation: "debox installation on your server will be in several steps:",
            steps : [
                "MySQL database configuration",
                "Workspace configuration (photos hosting & management)",
                "Creation of the administrator account"
            ]
        },
        steps: [
            {
                title: "Step 1 / 3 : MySQL database configuration",
                form: {
                    host : {
                        label: "MySQL server host:",
                        default : "Default value: 127.0.0.1"
                    },
                    port : {
                        label: "MySQL server access port:",
                        default : "Default value: 3306"
                    },
                    name : {
                        label: "Database name:",
                        default : "Default value: debox"
                    },
                    username: {
                        label : "MySQL username:",
                        default : "Default value: root"
                    },
                    password: {
                        label : "MySQL password:",
                        default : "Default value: <empty>"
                    },
                    next: "Check database connection"
                }
            },
            {
                title: "Step n°2 / 3 : Workspace configuration",
                introduction : "You must define a local directory on your server that will contain uploaded photos.\n\
                                This directory should be creatable by your application server. If this directory already exists, your application server should write and read it.",
                note: "Warning, working local directory must be empty so that the debox application can create the tree necessary for its proper functioning.",
                path: {
                    label : "Local directory :"
                },
                next: "Check local directory access"
            },
            {
                title: "Step 3 / 3 : Creation of administrator account",
                form: {
                    firstname : {
                        label: "First name: ",
                        placeholder : "Necessary to display your identity"
                    },
                    lastname : {
                        label: "Last name: ",
                        placeholder : "Necessary to display your identity"
                    },
                    username : {
                        label: "Your mail: ",
                        placeholder : "Mandatory to log-in the application"
                    },
                    password: {
                        label : "Your account password: ",
                        placeholder : "Mandatory to log-in the application"
                    },
                    next: "Create the administrator account"
                }
            },
            {
                title: "debox application has been successfully installed",
                introduction : "You finished installation, you could now log-in with the following link:",
                go : "Log-in"
            }
        ]
    }
};