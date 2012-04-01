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
            if ($("h1 a").text() == this.params['album']) {
                return;
            }
            
            ajax({
                url: computeUrl("api/album/" + this.params['album']),
                success: function(data) {
                    var plural = (data.album.photosCount > 1) ? "s" : ""
                    data.album.photosCount = data.album.photosCount + "&nbsp;photo" + plural;
                    for (var i = 0 ; i < data.albums.length ; i++) {
                        var album = data.albums[i];
                        plural = (album.photosCount > 1) ? "s" : "";
                        album.photosCount = album.photosCount + "&nbsp;photo" + plural;
                    }
                    data.minDownloadUrl = computeUrl("download/album/" + data.album.id + "/min");
                    data.downloadUrl = computeUrl("download/album/" + data.album.id);
                    data.album.visibility = data.album.visibility == "PUBLIC";
                    
                    loadTemplate("album", data, null, function() {
                        editTitle($("a.brand").text() + " - " + data.album.name);
                        $("button.edit-album").click(function() {
                            $("#edit_album").toggleClass("visible");
                        });
                        $("button.choose-cover").click(function() {
                            $("#photos").fadeOut(500, function(){
                                ajax({
                                    url: "api/album/" + data.album.id + "/photos?target=cover",
                                    success: function(photos) {
                                        loadTemplate("photo.thumbnails.admin", photos, "#cover-photos", function(){
                                            $('#cover-photos *[rel|=tooltip]').tooltip('hide');
                                            $("#cover-photos").fadeIn(500, function(){
                                                $(document.body).animate({scrollTop: $('#cover-photos').offset().top - 50}, 500);
                                            });
                                            $('#cover-photos .thumbnail').click(function() {
                                                var id;
                                                if ($(this).hasClass("thumbnail")) {
                                                    id = $(this).attr("id");
                                                } else {
                                                    id = $(this).parents(".thumbnail").attr("id");
                                                }
                                                ajax({
                                                    url: "album/" + data.album.id + "/cover",
                                                    type : "post",
                                                    data : {photoId:id},
                                                    success: function() {
                                                        $("#cover-photos").fadeOut(250, function() {
                                                            $('#cover-photos *[rel|=tooltip]').tooltip('hide');
                                                            $("#cover-photos").html("");
                                                            $("#photos").fadeIn(250);
                                                            $("#edit_album").prepend("");
                                                        });
                                                    }
                                                });
                                            });
                                        });
                                    }
                                });
                            });
                        });

                    });
                }
            });
        }),
        
        this.get('#/album/:album(/.*)?', function() {
            var photoId = this.params['splat'][0];
            var index = $(".photos a.thumbnail").index($("#" + photoId.substr(1)));
            if (index == -1) {
                ajax({
                    url: computeUrl("api/album/" + this.params['album']),
                    success: function(data) {
                        var plural = (data.album.photosCount > 1) ? "s" : ""
                        data.album.photosCount = data.album.photosCount + "&nbsp;photo" + plural;
                        for (var i = 0 ; i < data.albums.length ; i++) {
                            var album = data.albums[i];
                            plural = (album.photosCount > 1) ? "s" : "";
                            album.photosCount = album.photosCount + "&nbsp;photo" + plural;
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
                                            "id" : "/album/" + data.album.name + "/" + data.photos[i].id,
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
            $("#account input[type=submit]").button('loading');
            $("#account p").html("");
            var context = this;
            
            $.ajax({
                url: "administration/credentials",
                type : "post",
                data : $("#account").serializeArray(),
                success: function(data) {
                    $("#account").modal("hide");
                    $("#account input[type=submit]").button('reset');
                    $("#account p").removeClass("alert alert-error");
                    
                    loadTemplate("header", {
                        "username" : data.username,
                        "title" : $("a.brand").html()
                    }, ".navbar .container-fluid");
                    context.redirect("#/administration");
                },
                error: function(xhr) {
                    $("#account input[type=submit]").button('reset');
                    $("#account p").addClass("alert alert-error");
                    if (xhr.status == 401) {
                        $("#account p").html("Erreur durant l'opération, les identifiants rentrés ne correspondent pas.");
                    } else {
                        $("#account p").html("Erreur pendant l'opération.");
                    }
                }
            });
            return false;
        });
        
        this.post('#/administration/configuration', function() {
            var context = this;
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
                    }, ".navbar .container-fluid");
                    
                    if (force) {
                        $("#sync form input[type=checkbox]").attr("checked", "checked");
                        $("#sync form").submit();
                    } else {
                        context.redirect("#/administration");
                    }
                },
                error: function() {
                    targetBtn.button("reset");
                    alert("Error");
                }
            });
            return false;
        });
        
        this.post('#/administration/sync', function() {
            var context = this;
            $("#sync form input[type=submit]").button("loading");
            $.ajax({
                url: "administration/sync",
                type : "post",
                data : $("#sync form").serializeArray(),
                success: function(data) {
                    $("#sync form input[type=submit]").button("reset");
                    $("#sync form").modal("hide");
                    context.redirect("#/administration");
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
            $.ajax({
                url: "album",
                type : "post",
                data : $("#edit_album form").serializeArray(),
                success: function(data) {
                    $("#edit_album").modal("hide");
                    $("#"+data.id + " .name strong").text(data.name);
                    if (data.visibility == "PUBLIC") {
                        $("#"+data.id + " .visibility").html("<i class=\"icon-ok\"></i>&nbsp;Public");
                    } else {
                        $("#"+data.id + " .visibility").html("<i class=\"icon-ban-circle\"></i>&nbsp;Privé");
                    }
                    if (data.downloadable) {
                        $("#"+data.id + " .downloadable").html("<i class=\"icon-ok\"></i>&nbsp;Oui");
                    } else {
                        $("#"+data.id + " .downloadable").html("<i class=\"icon-ban-circle\"></i>&nbsp;Non");
                    }
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
        
        this.post('#/token', function() {
            $.ajax({
                url: "token",
                type : "post",
                data: $("#edit_token").serializeArray(),
                success: function(data) {
                    $("#" + data.id + " .access_label").text(data.label);
                    var albums = "";
                    if (data.albums.length) {
                        for (var i = 0 ; i < data.albums.length ; i++) {
                            albums += '<a href="#/album/' + data.albums[i].name + '">' + data.albums[i].name + '</a><br />';
                        }
                        $("#" + data.id + " .albums").html(albums);
                    } else {
                        $("#" + data.id + " .albums").text("Aucun album n'est visible pour cet accès");
                    }
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
            var tabId = this.params['tab'];
            if ($("#administration").length > 0) {
                $(".nav-tabs a[data-target|=\"#" + tabId.substr(1) + "\"]").tab("show");
                return;
            }
            
            editTitle($("a.brand").text() + " - Administration");
            ajax({
                url: "administration",
                success: function(data) {
                    
                    for (var i = 0 ; i < data.albums.length ; i++) {
                        data.albums[i].visibility = data.albums[i].visibility == "PUBLIC";
                    }
                    
                    loadTemplate("administration", data, null, function() {
                        handleAdmin();
                        
                        if (data.sync) {
                            $("#sync-progress").show();
                            var refreshProgressBar = function(data) {
                                $("#sync-progress h3 #progress-percentage").html(data.percent + "&nbsp;%");
                                $("#sync-progress .bar").css("width", data.percent+"%");
                                if (data.percent < 100) {
                                    $("#configuration-form input").attr("disabled", "disabled");
                                    syncTimeout = setTimeout(getSyncStatus, 2000);
                                    $("#synx-progress .btn-warning").show();
                                } else {
                                    syncTimeout = null;
                                    $("#configuration-form input").removeAttr("disabled");
                                    $("#sync-progress").removeClass("alert-info");
                                    $("#sync-progress").addClass("alert-success");
                                    $("#sync-progress h3 #progress-label").text("Synchronisation terminée");
                                    $("#sync-progress .progress").removeClass("progress-info active");
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
                        
                        if (tabId) {
                            console.log(tabId);
                            $(".nav-tabs a[data-target|=\"#" + tabId.substr(1) + "\"]").tab("show");
                        } else {
                            console.log(tabId);
                            $(".nav-tabs a[data-target|=\"#configuration\"]").tab("show");
                        }
                    });
                }
            });
        }),
        
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
                    $("#login input[type=submit]").button('reset');
                    $("#login p").removeClass("alert alert-error");
                    if (location.hash && location.hash.length > 1) {
                        context.redirect(location.hash);
                    } else {
                        context.redirect("#/");
                    }
                    $("#login").modal("hide");
                    loadTemplate("header", {
                        "username" : username,
                        "title" : $("a.brand").html()
                    }, ".navbar .container-fluid");
                },
                error: function(xhr) {
                    $("#login input[type=submit]").button('reset');
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
                success: function(data) {
                    loadTemplate("header", {
                        "username" : null,
                        "title" : $("a.brand").html()
                    }, ".navbar .container-fluid");
                    context.redirect("#/");
                },
                error: function(xhr) {
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
                        var plural = (album.photosCount > 1) ? "s" : ""
                        album.photosCount = album.photosCount + "&nbsp;photo" + plural;
                    }
                    loadTemplate("home", data);
                }
            });
        }); // End route
        
    }).run("#/");
    
    function handleAdmin() {
        $("#sync-progress .btn-warning").click(function(){
            $.ajax({
                url: "administration/sync",
                type: "delete",
                success: function(data) {
                    if (syncTimeout != null) {
                        clearTimeout(syncTimeout);
                        syncTimeout = null;
                    }
                    $("#sync-progress .btn-warning").hide();
                    $("#sync-progress").removeClass("alert-info");
                    $("#sync-progress").addClass("alert-danger");
                    $("#sync-progress .progress").removeClass("progress-info active");
                    $("#sync-progress .progress").addClass("progress-danger");
                },
                error: function(xhr) {
                    alert("Errur pendant l'annulation de la synchronisation");
                }
            });
        });
        
        $("#configuration .btn-danger").click(function(){
            $(this).parents("form").find("input[type=hidden]").val(true);
            $(this).parents("form").submit();
        });
        
        $("#administration_albums button.actions").click(function() {
            var id = $(this).parents("tr").attr("id");
            //add date the query just to avoid the cache
            var d = new Date();
            $("#actions_album_thumbnail").css("background-image", "url('" + "album/" + id  + "/cover?" + d.getTime() + "')");
            $("#actions_album input[type=hidden]").val(id);
            $("#actions_album").modal();
        });
        
        $("#administration_albums button.edit").click(function() {
            var id = $(this).parents("tr").attr("id");
            $.ajax({
                url: "album/" + id,
                success: function(data) {
                    $("#edit_album input[type=hidden]").val(data.id);
                    $("#edit_album #name").val(data.name);
                    $("#edit_album #visibility option").removeAttr("selected");
                    $("#edit_album #visibility option[value=" + data.visibility.toLowerCase() + "]").attr("selected", "selected");
                    if (data.downloadable) {
                        $("#edit_album #downloadable").attr("checked", "checked");
                    } else {
                        $("#edit_album #downloadable").removeAttr("checked");
                    }
                    $("#edit_album").modal();
                }
            });
        });
        
        $("#administration_tokens button.btn-info").click(function() {
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
        
        $("#administration_tokens button.btn-danger").click(function() {
            var id = $(this).parents("tr").attr("id");
            var name = $(this).parents("tr").find(".access_label").text();
            $("#modal-token-delete input[type=hidden]").val(id);
            $("#modal-token-delete p strong").text(name);
        });
        
        // Need to refresh binding because of DOM operations
        $("button[type=reset]").click(hideModal);
        $('form.modal').on('hidden', resetModalForm);

        $("#actions_album_random_cover").click(function(event) {
            var id = $("#actions_album input[type=hidden]").val();
            $.ajax({
                url: "album/" + id + "/cover",
                type : "post",
                success: function() {
                    //add date the query just to avoid the cache
                    var d = new Date();
                    $("#actions_album_thumbnail").css("background-image", "url('" + "album/" + id  + "/cover?" + d.getTime() + "')");
                }
            });
            return false;
        });
        
    }
    
    $("#login button[type=reset]").click(hideModal);
    $('form.modal').on('hidden', resetModalForm);
    
});

var syncTimeout = null;