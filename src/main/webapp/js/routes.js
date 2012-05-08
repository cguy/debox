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
        // Comment this line to enable SammyJS logging
        this.log = function() {}
        
        this.get('#/album/:album', function() {
            if ($("h1").attr("id") == this.params['album'] && document.getElementById("fullscreenContainer") != null) {
                return;
            }
            
            ajax({
                url: computeUrl("album/" + this.params['album']),
                success: loadAlbum
            });
        }),
        
        this.get('#/album/:album(/.*)?', function() {
            var photoId = this.params['splat'][0];
            var index = $(".photos a.thumbnail").index($("#" + photoId.substr(1)));
            if (index == -1) {
                ajax({
                    url: computeUrl("album/" + this.params['album']),
                    success: function(data) {
                        createAlbum(data.album);
                        for (var i = 0 ; i < data.subAlbums.length ; i++) {
                            var album = data.subAlbums[i];
                            createAlbum(album);
                        }
                        data.album.downloadUrl = computeUrl("download/album/" + data.album.id);
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
                    var span = $(photos[i]).find("span");
                    slideshowData.push({
                        "id" : "/album/" + this.params['album'] + "/" + photos[i].id,
                        "url" : span.text(),
                        "caption" : $(photos[i]).attr("title")
                    });
                }
                fullscreen(index, slideshowData);
                
            } else {
                var currentIndex = $('#slideshow-div').rsfSlideshow("currentSlideKey");
                // We are displaying first photo and we want to see the previous one
                if (currentIndex == 0 && index == $(".photos a.thumbnail").length - 1) {
                    $('#slideshow-div').rsfSlideshow(
                        'showSlide', index, "slideRight"
                    );
                
                // We are displaying last photo and we want to see the next one
                } else if (index == 0 && currentIndex == $(".photos a.thumbnail").length - 1) {
                    $('#slideshow-div').rsfSlideshow(
                        'showSlide', index, "slideLeft"
                    );
                        
                // We want to see the next one
                } else if (currentIndex < index) {
                    $('#slideshow-div').rsfSlideshow(
                        'showSlide', index, "slideLeft"
                    );
                        
                // We want to see the previous one
                } else if (currentIndex > index) {
                    $('#slideshow-div').rsfSlideshow(
                        'showSlide', index, "slideRight"
                    );
                }
            }
        }),
        
        /* ******************** */
        /* Aministration access */
        /* ******************** */
        this.before({except: null}, function() {
            if (this.verb == "get") {
                $("html, body").animate({scrollTop: 0}, 0);
                if (this.path.indexOf("#/administration") == -1) {
                    delete allAlbums;
                }
                var regex = new RegExp("^\/#\/album\/([a-zA-Z0-9_-]+)\/([a-zA-Z0-9_-]+)");
                if (!regex.test(this.path) && document.getElementById("fullscreenContainer") != null) {
                    exitFullscreen();
                }
            }
        });
        
        this.post('#/account/:accountId', function() {
            $("#account form input[type=submit]").button('loading');
            
            $("#account form p").addClass("hide");
            $("#account form p").removeClass("alert alert-error alert-success");
            $("#account form p").html("");
            
            $.ajax({
                url: "account/" + this.params['accountId'],
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
                url: "configuration",
                type : "post",
                data : data,
                success: function(data) {
                    targetBtn.button("reset");
                    loadTemplate("header", {
                        "username" : $(".navbar-text.pull-right strong").html(), 
                        "title" : data.title
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
        
        this.post('#/album/:albumId', function() {
            $("#alerts .edit.alert-success").fadeOut(250);
            $("#alerts .edit.alert-danger").fadeOut(250);
            $.ajax({
                url: "album/" + this.params["albumId"],
                type : "post",
                data : $("#edit_album form").serializeArray(),
                success: function(data) {
                    data.inEdition = true;
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
                    
                    var treeChildren = [];
                    prepareDynatree(allAlbums, [], treeChildren, null);
                    
                    data.i18n = lang;
                    var html = templates["admin.tokens.row"].render(data, templates);
                    $("#administration_tokens tbody").append(html);
                    
                    initDynatree(data.id, treeChildren);
                    
                    $("#form-token-create input[type=text]").val("");
                    loadFunctions("tokens");
                }
            });
            return false;
        });
        
        this.post('#/token/:token', function() {
            var token = this.params["token"];
            $("#" + token + " .alert-success").hide();
            $("#" + token + " .alert-error").hide();
            $.ajax({
                url: "token/" + token,
                type : "post",
                data: $("#" + token + " .album-access-form").serializeArray().concat($("#" + token + " .album-access-form .albums-access").dynatree("getTree").serializeArray()),
                success: function(data) {
                    $("#" + data.id + " .access_label").text(data.label);
                    $("#" + data.id + " .album-access-form .albums-access").hide();
                    $("#" + data.id + " .album-access-form button.btn").show();
                    $("#" + data.id + " .alert-success").show();
                    $("#" + data.id + " .album-access-form span").hide();
                },
                error : function() {
                    $("#" + token + " .alert-error").show();
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
            var tab = this.params["tab"];
            if (tab) {
                tab = tab.substr(1);
                if (tab == "synchronization") {
                    loadAdministrationTab("synchronization");
                
                } else {
                    ajax({
                        url: tab,
                        success: function(data) {
                            loadAdministrationTab(tab, data);
                        }
                    });
                }
            } else {
                this.redirect("#/administration/configuration");
            }
        });
        
        this.get('#/administration(/:tab)?', function() {
            return false;
            editTitle($("a.brand").text() + " - Administration");
            var tabId = this.params['tab'];
            if ($("#administration").length > 0) {
                $(".nav-tabs a[data-target|=\"#" + tabId.substr(1) + "\"]").tab("show");
                $("form .alert-error, form .alert-success").addClass("hide");
                return;
            }
            
            ajax({
                url: "administration",
                success: function(data) {
                    allAlbums = data.albums;
                    
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
                            prepareDynatree(data.albums, tokens[tokenIndex].albums, treeChildren, null);
                            initDynatree(tokens[tokenIndex].id, treeChildren);
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
            $("#login p").html("").removeClass("alert alert-error");
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
                    $("#connect").button('reset');
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
                    $("#connect").button('reset');
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
                url: computeUrl("albums"),
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
    
});

var syncTimeout = null;
