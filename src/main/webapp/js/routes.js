$(document).ready(function() {
    
    Sammy(function() {
        
        //        this.get("#/photo/:photo", function() {
        //            ajax({
        //                url: baseUrl + "deploy/api/photo/" + this.params['photo'],
        //                success: function(data) {
        //                    loadTemplate("photo", data);
        //                }
        //            });
        //        });
        
        this.get('#/album/:album', function() {
            if ($("h1 a").text() == this.params['album']) {
                return;
            }
            
            ajax({
                url: computeUrl(baseUrl + "api/album/" + this.params['album']),
                success: function(data) {
                    var plural = (data.album.photosCount > 1) ? "s" : ""
                    data.album.photosCount = data.album.photosCount + "&nbsp;photo" + plural;
                    for (var i = 0 ; i < data.albums.length ; i++) {
                        var album = data.albums[i];
                        plural = (album.photosCount > 1) ? "s" : "";
                        album.photosCount = album.photosCount + "&nbsp;photo" + plural;
                    }
                    loadTemplate("album", data, null, function(){
                        editTitle($("a.brand").text() + " - " + data.album.name);
                    });
                }
            });
        }),
        
        this.get('#/album/:album(/.*)?', function() {
            var photoId = this.params['splat'][0];
            var index = $(".photos a.thumbnail").index($("#" + photoId.substr(1)));
            if (index == -1) {
                ajax({
                    url: computeUrl(baseUrl + "api/album/" + this.params['album']),
                    success: function(data) {
                        loadTemplate("album", data, null, function(){
                            if (photoId.length > 1) {
                                var index = $(".photos a.thumbnail").index($("#" + photoId.substr(1)));
                                if (index != -1) {
                                    var slideshowData = [];
                                    for (var i = 0 ; i < data.photos.length ; i++) {
                                        slideshowData.push({
                                            "id" : "/album/" + data.album.name + "/" + data.photos[i].id,
                                            "url" : baseUrl + data.photos[i].url,
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
            $("#modal-account input[type=submit]").button('loading');
            $("#modal-account p").html("");
            var context = this;
            
            $.ajax({
                url: baseUrl + "administration/credentials",
                type : "post",
                data : $("#modal-account").serializeArray(),
                success: function(data) {
                    $("#modal-account").modal("hide");
                    $("#modal-account input[type=submit]").button('reset');
                    $("#modal-account p").removeClass("alert alert-error");
                    
                    loadTemplate("header", {
                        "username" : data.username,
                        "title" : $("a.brand").html()
                    }, ".navbar .container");
                    context.redirect("#/administration");
                },
                error: function(xhr) {
                    $("#modal-account input[type=submit]").button('reset');
                    $("#modal-account p").addClass("alert alert-error");
                    if (xhr.status == 401) {
                        $("#modal-account p").html("Erreur durant l'opération, les identifiants rentrés ne correspondent pas.");
                    } else {
                        $("#modal-account p").html("Erreur pendant l'opération.");
                    }
                }
            });
            return false;
        });
        
        this.post('#/administration/configuration', function() {
            var context = this;
            $("#modal-configuration input[type=submit]").button("loading");
            $.ajax({
                url: baseUrl + "administration/configuration",
                type : "post",
                data : $("#modal-configuration").serializeArray(),
                success: function(data) {
                    $("#modal-configuration input[type=submit]").button("reset");
                    loadTemplate("header", {
                        "username" : $(".navbar-text.pull-right strong").html(), 
                        "title" : data.title
                        }, ".navbar .container");
                    $("#modal-configuration").modal("hide");
                    context.redirect("#/administration");
                },
                error: function() {
                    $("#modal-configuration input[type=submit]").button("reset");
                    alert("Error");
                }
            });
            return false;
        });
        
        this.post('#/administration/sync', function() {
            var context = this;
            $("#modal-sync input[type=submit]").button("loading");
            $.ajax({
                url: baseUrl + "administration/sync",
                type : "post",
                data : $("#modal-configuration").serializeArray(),
                success: function(data) {
                    $("#modal-sync input[type=submit]").button("reset");
                    
                    
                    $("#modal-sync").modal("hide");
                    context.redirect("#/administration");
                },
                error: function(xhr) {
                    $("#modal-sync input[type=submit]").button("reset");
                    $("#modal-sync p.error").addClass("alert alert-error");
                    
                    if (xhr.status == 409) {
                        $("#modal-sync p.error").text("Veuillez commencer par définir la configuration générale (dont les répertoires de travail) avant de lancer la première synchronisation.");
                    } else {
                        $("#modal-sync p.error").text("Erreur de communication avec le serveur.");
                    }
                }
            });
            return false;
        });
        
        this.post('#/album', function() {
            $.ajax({
                url: baseUrl + "album",
                type : "post",
                data : $("#edit_album").serializeArray(),
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
                url: baseUrl + "token?label=" + encodeURIComponent(this.params["label"]),
                type : "put",
                success: function(data) {
                    $("#administration_tokens").removeClass("hide");
                    $("#tokens p.alert-warning").addClass("hide");
                    var html = '<tr id="' + data.id + '">';
                    html += '<td class="label">' + data.label + '</td>';
                    html += '<td class="albums">Aucun album n\'est visible pour cet accès</td>';
                    html += '<td><a href="' + baseUrl + data.id + '/#/">Lien</a></td>';
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
                url: baseUrl + "token",
                type : "post",
                data: $("#edit_token").serializeArray(),
                success: function(data) {
                    $("#" + data.id + " .label").text(data.label);
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
                url: baseUrl + "token/" + id,
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
        
        this.get('#/administration', function() {
            editTitle($("a.brand").text() + " - Administration");
            ajax({
                url: baseUrl + "administration",
                success: function(data) {
                    
                    for (var i = 0 ; i < data.albums.length ; i++) {
                        data.albums[i].visibility = data.albums[i].visibility == "PUBLIC";
                    }
                    
                    loadTemplate("administration", data, null, function() {
                        handleAdmin();
                        
                        if (data.sync) {
                            $("#sync-progress").show();
                            var refreshProgressBar = function(data) {
                                $("#sync-progress h3 span").html(data.percent + "&nbsp;%");
                                $("#sync-progress .bar").css("width", data.percent+"%");
                                if (data.percent < 100) {
                                    $("#configuration-form input").attr("disabled", "disabled");
                                    setTimeout(getSyncStatus, 2000);
                                } else {
                                    $("#configuration-form input").removeAttr("disabled");
                                    $("#sync-progress").removeClass("alert-info");
                                    $("#sync-progress").addClass("alert-success");
                                    $("#sync-progress .progress").removeClass("progress-info active");
                                    $("#sync-progress .progress").addClass("progress-success");
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
                url: baseUrl + "authenticate",
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
                    }, ".navbar .container");
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
                url: baseUrl + "session",
                type : "delete",
                success: function(data) {
                    loadTemplate("header", {
                        "username" : null,
                        "title" : $("a.brand").html()
                    }, ".navbar .container");
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
                url: computeUrl(baseUrl + "api/albums"),
                success: function(data) {
                    for (var i = 0 ; i < data.length ; i++) {
                        var album = data[i];
                        var plural = (album.photosCount > 1) ? "s" : ""
                        album.photosCount = album.photosCount + "&nbsp;photo" + plural;
                    }
                    loadTemplate("home", data);
                }
            });
        }); // End route
        
    }).run("#/");
    
    function handleAdmin() {
        $("#administration_albums button").click(function() {
            var id = $(this).parents("tr").attr("id");
            $.ajax({
                url: baseUrl + "album/" + id,
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
                url: baseUrl + "token/" + id,
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
            var name = $(this).parents("tr").find(".label").text();
            $("#modal-token-delete input[type=hidden]").val(id);
            $("#modal-token-delete p strong").text(name);
        });
        
        $("button[type=reset]").click(function() {
            $(this).parents(".modal").find("p.alert-error").text("");
            $(this).parents(".modal").find("p.alert-error").removeClass("alert alert-error");
            $(this).parents(".modal").modal("hide");
        });
                
        $(".thumbnails.admin a").click(function(event) {
            $(".admin_part").hide();
        });
                
        $("a[href=#albums]").click(function(event) {
            $("#albums").show();
                    
            // DO NOT REMOVE, avoid hash part change
            event.preventDefault();
        });
                
        $("a[href=#tokens]").click(function(event) {
            $("#tokens").show();
                    
            // DO NOT REMOVE, avoid hash part change
            event.preventDefault();
        });
    }
    
});
