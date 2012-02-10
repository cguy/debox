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
                return false;
            }
            
            ajax({
                url: computeUrl(baseUrl + "api/album/" + this.params['album']),
                success: function(data) {
                    var plural = (data.album.photosCount > 1) ? "s" : ""
                    data.album.photosCount = data.album.photosCount + "&nbsp;photo" + plural;
                    for (var i = 0 ; i < data.albums.length ; i++) {
                        var album = data.albums[i];
                        var plural = (album.photosCount > 1) ? "s" : ""
                        album.photosCount = album.photosCount + "&nbsp;photo" + plural;
                    }
                    loadTemplate("album", data);
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
                success: function() {
                    $("#modal-account").modal("hide");
                    $("#modal-account input[type=submit]").button('reset');
                    $("#modal-account p").removeClass("alert alert-error");
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
            console.log($("#modal-configuration").serializeArray());
            $("#modal-configuration input[type=submit]").button("loading");
            $.ajax({
                url: baseUrl + "administration/configuration",
                type : "post",
                data : $("#modal-configuration").serializeArray(),
                success: function() {
                    $("#modal-configuration input[type=submit]").button("reset");
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
        
        this.post('#/album', function() {
            var context = this;
            var data = {
                "id" : this.params["id"],
                "name" : this.params["name"],
                "visibility" : this.params["visibility"]
            };
            $.ajax({
                url: baseUrl + "album",
                type : "post",
                data : data,
                success: function() {
                    $("#edit_album").modal("hide");
                    context.redirect("#/administration");
                }
            });
            return false;
        });
        
        this.put('#/group', function() {
            var context = this;
            $.ajax({
                url: baseUrl + "group?label=" + this.params["label"],
                type : "put",
                success: function() {
                    context.redirect("#/administration");
                }
            });
            return false;
        });
        
        this.post('#/token', function() {
            var context = this;
            $.ajax({
                url: baseUrl + "token",
                type : "post",
                data: $("#edit_token").serializeArray(),
                success: function() {
                    $("#edit_token").modal("hide");
                    context.redirect("#/administration");
                }
            });
            return false;
        });
        
        this.get('#/administration', function() {
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
                },
                error: function(xhr) {
                    loadTemplate(xhr.status);
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
                        "username" : username
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
                    loadTemplate("header", null, ".navbar .container");
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
    
});
