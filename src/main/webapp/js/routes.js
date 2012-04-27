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
$(document).ready(function() {
    
    Sammy(function() {
        
        this.get('#/album/:album', function() {
            if ($("h1").attr("id") == this.params['album'] && document.getElementById("fullscreenContainer") != null) {
                return;
            }
            
            ajax({
                url: computeUrl("api/album/" + this.params['album']),
                success: loadAlbum
            });
        }),
        
        this.get('#/album/:album(/.*)?', function() {
            var photoId = this.params['splat'][0];
            var index = $(".photos a.thumbnail").index($("#" + photoId.substr(1)));
            if (index == -1) {
                ajax({
                    url: computeUrl("api/album/" + this.params['album']),
                    success: function(data) {
                        createAlbum(data.album);
                        for (var i = 0 ; i < data.album.subAlbums.length ; i++) {
                            var album = data.album.subAlbums[i];
                            createAlbum(album);
                        }
                        data.downloadUrl = computeUrl("download/album/" + data.album.id);
                        loadTemplate("album", data, null, function() {
                            editTitle($("a.brand").text() + " - " + data.album.name);
                            if (photoId.length > 1) {
                                var index = $(".photos a.thumbnail").index($("#" + photoId.substr(1)));
                                if (index != -1) {
                                    var slideshowData = [];
                                    for (var i = 0 ; i < data.photos.length ; i++) {
                                        slideshowData.push({
                                            "id" : "/album/" + data.album.id + "/" + data.photos[i].id,
                                            "url" : data.photos[i].url,
                                            "caption" : data.photos[i].name
                                        });
                                    }
                                    fullscreen(index, slideshowData);
                                }
                            }
                        });
                    }
                });
            } else if (document.getElementById("fullscreenContainer") == null) {
                var slideshowData = [];
                var photos = $(".photos a.thumbnail");
                for (var i = 0 ; i < photos.length ; i++) {
                    var img = $(photos[i]).find("img");
                    var span = $(photos[i]).find("span");
                    slideshowData.push({
                        "id" : "/album/" + this.params['album'] + "/" + photos[i].id,
                        "url" : span.text(),
                        "caption" : img.attr("title")
                    });
                }
                fullscreen(index, slideshowData);
            }
        }),
        
        /* ******************** */
        /* Aministration access */
        /* ******************** */
        this.post('#/administration/credentials', function() {
            $("#account form input[type=submit]").button('loading');
            
            $("#account form p").addClass("hide");
            $("#account form p").removeClass("alert alert-error alert-success");
            $("#account form p").html("");
            
            $.ajax({
                url: "administration/credentials",
                type : "post",
                data : $("#account form").serializeArray(),
                success: function(data) {
                    loadTemplate("header", {
                        "username" : data.username,
                        "title" : $("a.brand").html()
                    }, ".navbar .container-fluid", headerTemplateLoaded);
                    
                    $("#account form p").text("Identifiants de connexion modifiés avec succès !");
                    $("#account form input[type=submit]").button('reset');
                    $("#account form p").addClass("alert alert-success");
                    $("#account form p").removeClass("hide");
                },
                error: function(xhr) {
                    if (xhr.status == 401) {
                        $("#account form p").text("Erreur durant l'opération, les identifiants rentrés ne correspondent pas.");
                    } else {
                        $("#account form p").text("Erreur pendant l'opération.");
                    }
                    
                    $("#account form input[type=submit]").button('reset');
                    $("#account form p").addClass("alert alert-error");
                    $("#account form p").removeClass("hide");
                }
            });
            return false;
        });
        
        this.post('#/administration/configuration', function() {
            var data = $("#configuration form").serializeArray();
            var force = false;
            for (var i = 0 ; i < data.length ; i++) {
                if (data[i].name == "force" && data[i].value == "true") {
                    force = true;
                    delete data[i];
                }
            }
            
            var targetBtn;
            if (force) {
                targetBtn = $("#configuration button");
            } else {
                targetBtn = $("#configuration input[type=submit]");
            }
            targetBtn.button("loading");
            
            $.ajax({
                url: "administration/configuration",
                type : "post",
                data : data,
                success: function(data) {
                    targetBtn.button("reset");
                    loadTemplate("header", {
                        "username" : $(".navbar-text.pull-right strong").html(), 
                        "title" : data.configuration.title
                    }, ".navbar .container-fluid", headerTemplateLoaded);
                    
                    if (force) {
                        $("#sync form input[type=checkbox]").attr("checked", "checked");
                        $("#sync form").submit();
                    }
                    $("#configuration p").text("Configuration enregistrée avec succès !");
                    $("#configuration p").addClass("alert-success");
                    $("#configuration p").removeClass("hide");
                },
                error: function() {
                    $("#configuration p").text("Erreur durant l'enregistrement de la configuration.");
                    $("#configuration p").addClass("alert-danger");
                    $("#configuration p").removeClass("hide");
                    targetBtn.button("reset");
                }
            });
            return false;
        });
        
        this.post('#/administration/sync', function() {
            $("#sync form input[type=submit]").button("loading");
            $.ajax({
                url: "administration/sync",
                type : "post",
                data : $("#sync form").serializeArray(),
                success: function() {
                    $("#sync form input[type=submit]").button("reset");
                    manageSync({
                        sync:{
                            percent:0
                        }
                    });
                },
                error: function(xhr) {
                    $("#sync form input[type=submit]").button("reset");
                    $("#sync form p.error").addClass("alert alert-error");
                    if (xhr.status == 409) {
                        $("#sync form p.error").text("Veuillez commencer par définir la configuration générale (dont les répertoires de travail) avant de lancer la première synchronisation.");
                    } else {
                        $("#sync form p.error").text("Erreur de communication avec le serveur.");
                    }
                }
            });
            return false;
        });
        
        this.post('#/album', function() {
            $("#alerts .edit.alert-success").fadeOut(250);
            $("#alerts .edit.alert-danger").fadeOut(250);
            $.ajax({
                url: "album",
                type : "post",
                data : $("#edit_album form").serializeArray(),
                success: function(data) {
                    data.album.inEdition = true;
                    loadAlbum(data, function(){
                        $("#alerts .edit.alert-success").fadeIn(250);
                    });
                },
                error : function() {
                    $("#alerts .edit.alert-danger").fadeIn(250);
                }
            });
            return false;
        });
        
        this.put('#/token', function() {
            $.ajax({
                url: "token?label=" + encodeURIComponent(this.params["label"]),
                type : "put",
                success: function(data) {
                    $("#administration_tokens").removeClass("hide");
                    $("#tokens p.alert-warning").addClass("hide");
                    var html = '<tr id="' + data.id + '">';
                    html += '<td class="access_label">' + data.label + '</td>';
                    html += '<td class="albums">Aucun album n\'est visible pour cet accès</td>';
                    html += '<td><a href="' + data.id + '/#/">Lien</a></td>';
                    html += '<td><div class="btn-group">';
                    html += '<button class="btn btn-info"><i class="icon-pencil icon-white"></i>&nbsp;Modifier</button>';
                    html += '<button href="#modal-token-delete" data-toggle="modal" class="btn btn-danger"><i class="icon-remove icon-white"></i>&nbsp;Supprimer</button>';
                    html += '</div></td></tr>';
                    $("#administration_tokens tbody").append(html);
                    $("#form-token-create input[type=text]").val("");
                    handleAdmin();
                }
            });
            return false;
        });
        
        this.post('#/token/:token', function() {
            console.log(this.params["token"]);
            console.log($("#" + this.params["token"] + " .albums-access"));
            console.log($("#" + this.params["token"] + " .albums-access").dynatree("getTree"));
            console.log($("#" + this.params["token"] + " .albums-access").dynatree("getTree").serializeArray());
            $.ajax({
                url: "token/" + this.params["token"],
                type : "post",
                data: $("#" + this.params["token"] + " .album-access-form").serializeArray().concat($("#" + this.params["token"] + " .album-access-form .albums-access").dynatree("getTree").serializeArray()),
                success: function(data) {
                    $("#" + data.id + " .access_label").text(data.label);
                    $("#" + data.id + " .album-access-form .albums-access").hide();
                    $("#" + data.id + " .album-access-form button.btn").show();
                    $("#" + data.id + " .album-access-form span").hide();
                }
            });
            return false;
        });
        
        this.post('#/token', function() {
            $.ajax({
                url: "token",
                type : "post",
                data: $("#edit_token").serializeArray(),
                success: function(data) {
                    $("#" + data.id + " .access_label").text(data.label);
                    $("#edit_token").modal("hide");
                }
            });
            return false;
        });
        
        this.del('#/token', function() {
            var id = $("#modal-token-delete input[type=hidden]").val();
            $.ajax({
                url: "token/" + id,
                type: "delete",
                success: function() {
                    $("#" + id).remove();
                    if ($("#administration_tokens").find("tbody tr").length == 0) {
                        $("#administration_tokens").addClass("hide");
                        $("#tokens p.alert-warning").removeClass("hide");
                    }
                    $("#modal-token-delete").modal("hide");
                },
                error : function(xhr) {
                    $("#modal-token-delete input[type=submit]").button('reset');
                    $("#modal-token-delete p:first-of-type").addClass("alert alert-error");
                    $("#modal-token-delete p:first-of-type").html("Erreur pendant la suppression de l'accès, veuillez réessayer ultérieurement.");
                }
            });
            return false;
        });
        
        this.get('#/administration(/:tab)?', function() {
            editTitle($("a.brand").text() + " - Administration");
            var tabId = this.params['tab'];
            if ($("#administration").length > 0) {
                $(".nav-tabs a[data-target|=\"#" + tabId.substr(1) + "\"]").tab("show");
                $("form .alert").addClass("hide");
                return;
            }
            
            ajax({
                url: "administration",
                success: function(data) {
                    for (var i = 0 ; i < data.albums.length ; i++) {
                        data.albums[i].visibility = data.albums[i].visibility == "PUBLIC";
                    }
                    
                    loadTemplate("administration", data, null, function() {
                        handleAdmin();
                        manageSync(data);
                        
                        if (tabId) {
                            $(".nav-tabs a[data-target|=\"#" + tabId.substr(1) + "\"]").tab("show");
                        } else {
                            $(".nav-tabs a[data-target|=\"#configuration\"]").tab("show");
                        }
                        
                        // Generates trees for tokens management
                        var tokens = data.tokens;
                        for (var tokenIndex = 0 ; tokenIndex < tokens.length ; tokenIndex++) {
                            var treeChildren = [];
                            function test(src, target) {
                                if (!src) {
                                    return;
                                }
                                for (var i = 0 ; i < src.length ; i++) {
                                    var found = false;
                                    var albums = tokens[tokenIndex].albums;
                                    for (var tokenAlbumsIndex = 0 ; tokenAlbumsIndex < albums.length ; tokenAlbumsIndex++) {
                                        if (albums[tokenAlbumsIndex].id == src[i].id) {
                                            found = true;
                                            break;
                                        }
                                    }
                                    
                                    var p = {
                                        title:src[i].name, 
                                        key: src[i].id, 
                                        isFolder: true,
                                        select: found,
                                        children:[]
                                    };
                                    test(src[i].subAlbums, p.children);
                                    target.push(p);
                                }
                            }
                            test(data.albums, treeChildren);
                        
                            $("#" + tokens[tokenIndex].id + " .albums-access").dynatree({
                                autoCollapse: false,
                                persist: false,
                                imagePath: "/skin-vista",
                                checkbox: true, // Show checkboxes.
                                selectMode: 2, // 1:single, 2:multi, 3:multi-hier
                                fx: {
                                    height: "toggle", 
                                    duration: 200
                                }, // Animations, e.g. null or { height: "toggle", duration: 200 }
                                noLink: true,
                                children: treeChildren,
                                debugLevel: 2
                            });
                        }
                       
                        
                    }); // End loading template
                }
            }); // End ajax call
            
        }), // End route
        
        /* ******************* */
        /* Authentication      */
        /* ******************* */
        this.post('#/authenticate', function() {
            $("#login input[type=submit]").button('loading');
            $("#login p").html("");
            var context = this;
            
            var data = {
                "username" : this.params["username"], 
                "password" : this.params["password"]
            };
            ajax({
                url: "authenticate",
                type : "post",
                data : data,
                success: function(username) {
                    $("#login #connect").button('reset');
                    $("#login p").removeClass("alert alert-error");
                    loadTemplates(function() {
                        if (location.hash && location.hash.length > 1) {
                            context.redirect(location.hash);
                        } else {
                            context.redirect("#/");
                        }
                        loadTemplate("header", {
                            "username" : username,
                            "title" : $("a.brand").html()
                        }, ".navbar .container-fluid", headerTemplateLoaded);
                    });
                },
                error: function(xhr) {
                    $("#login #connect").button('reset');
                    $("#login p").addClass("alert alert-error");
                    if (xhr.status == 401) {
                        $("#login p").html("Erreur de connexion: veuillez vérifier vos identifiants de connexion.");
                    } else {
                        $("#login p").html("Erreur pendant la connexion, veuillez réessayer ultérieurement.");
                    }
                }
            });
            return false;
        });
        
        /* ******************* */
        /* Logout              */
        /* ******************* */
        this.get('#/logout', function() {
            var context = this;
            ajax({
                url: "session",
                type : "delete",
                success: function() {
                    loadTemplates(function() {
                        loadTemplate("header", {
                            "username" : null,
                            "title" : $("a.brand").html()
                        }, ".navbar .container-fluid", headerTemplateLoaded);
                        context.redirect("#/");
                    })
                },
                error: function() {
                    context.redirect("#/");
                }
            });
        });
        
        /* ******************* */
        /* Home page           */
        /* ******************* */
        this.get('#/', function() {
            editTitle($("a.brand").text() + " - Accueil");
            ajax({
                url: computeUrl("api/albums"),
                success: function(data) {
                    for (var i = 0 ; i < data.albums.length ; i++) {
                        var album = data.albums[i];
                        createAlbum(album);
                    }
                    loadTemplate("home", data);
                }
            });
        }); // End route
        
    }).run("#/");
    
    function handleAdmin() {
        $("#sync-progress .btn-warning").click(function() {
            $.ajax({
                url: "administration/sync",
                type: "delete",
                success: function() {
                    if (syncTimeout != null) {
                        clearTimeout(syncTimeout);
                        syncTimeout = null;
                    }
                    $("#sync input").removeAttr("disabled");
                    $("#sync-progress .btn-warning").hide();
                    $("#sync-progress").removeClass("alert-info");
                    $("#sync-progress .progress").removeClass("progress-info active");
                    $("#sync-progress").addClass("alert-danger");
                    $("#sync-progress .progress").addClass("progress-danger");
                },
                error: function() {
                    alert("Erreur pendant l'annulation de la synchronisation");
                }
            });
        });
        
        $("#configuration .btn-danger").click(function() {
            $(this).parents("form").find("input[type=hidden]").val(true);
            $(this).parents("form").submit();
        });
        
        $("#administration_tokens .albums button.show-tree").click(function() {
            $(this).hide();
            $(this).parents(".albums").find("span, .albums-access").show();
        });
        
        $("#administration_tokens .albums button.cancel").click(function() {
            $(this).parents(".albums").find(".albums-access").hide();
            $(this).parents(".albums").find("button.btn").show();
            $(this).parents("span").hide();
        });
        
        $("#administration_tokens button.edit").click(function() {
            var id = $(this).parents("tr").attr("id");
            $.ajax({
                url: "token/" + id,
                success: function(data) {
                    $("#edit_token input[type=hidden]").val(data.token.id);
                    $("#edit_token #label").val(data.token.label);
                    $("#edit_token #albums option").removeAttr("selected");
            
                    for (var i = 0 ; i < data.token.albums.length ; i++) {
                        $("#edit_token #albums option[value=" + data.token.albums[i].id + "]").attr("selected", "selected");
                    }
        
                    $("#edit_token").modal();
                }
            });
        });
        
        // Need to refresh binding because of DOM operations
        $("button[type=reset]").click(hideModal);
        $('form.modal').on('hidden', resetModalForm);
        
        $("#administration_tokens button.delete").click(function() {
            var id = $(this).parents("tr").attr("id");
            var name = $(this).parents("tr").find(".access_label").text();
            $("#modal-token-delete input[type=hidden]").val(id);
            $("#modal-token-delete p strong").text(name);
        });
        
    } // End handleAdmin function
    
    function manageSync(data) {
        if (data.sync) {
            $("#sync-progress").show();
            $("#sync-progress").addClass("alert-info");
            $("#sync-progress .progress").addClass("progress-info active");
            $("#sync-progress").removeClass("alert-success alert-danger");
            $("#sync-progress .progress").removeClass("progress-success progress-danger");
            $("#sync-progress h3 #progress-label").text("Synchronisation en cours...");
            $("#sync-progress .btn-warning").show();
            $("#sync input").attr("disabled", "disabled");
            
            var refreshProgressBar = function(data) {
                $("#sync-progress h3 #progress-percentage").html(data.percent + "&nbsp;%");
                $("#sync-progress .bar").css("width", data.percent+"%");
                if (data.percent < 100) {
                    syncTimeout = setTimeout(getSyncStatus, 3000);
                } else {
                    syncTimeout = null;
                    $("#sync input").removeAttr("disabled");
                    $("#sync-progress").removeClass("alert-info");
                    $("#sync-progress h3 #progress-label").text("Synchronisation terminée");
                    $("#sync-progress .progress").removeClass("progress-info active");
                    $("#sync-progress").addClass("alert-success");
                    $("#sync-progress .progress").addClass("progress-success");
                    $("#sync-progress .btn-warning").hide();
                }
            }
                        
            var getSyncStatus = function() {
                $.ajax({
                    url: "administration/sync",
                    success: function(data) {
                        refreshProgressBar(data);
                    }
                });
            }
            refreshProgressBar(data.sync);
        }
    }

});

var syncTimeout = null;
