$(document).ready(function() {
    
    function loadTemplate(template, data, selector, callback) {
        if (!selector) {
            selector = ".container .content";
        }
        $.ajax({
            url: "/templates/" + template + ".tpl?t="+new Date().getTime(),
            success: function(tpl) {
                var html = Mustache.render(tpl, {
                    data : data
                });
                $(selector).html(html);
                if (callback) {
                    callback();
                }
            }
        });
    }
    
    function ajax(object) {
        if (!object.error) {
            object.error = function(xhr) {
                if (xhr.status == 404) {
                    loadTemplate(xhr.status);
                }
            }
        }
        $.ajax(object);
    }
    
    function handleAdmin() {
        $("#administration_albums button").click(function() {
            var id = $(this).parent().parent().attr("id");
            $.ajax({
                url: "album/" + id,
                success: function(data) {
                    console.log(data);
                    $("#edit_album input[type=hidden]").val(data.id);
                    $("#edit_album #name").val(data.name);
                    $("#edit_album #visibility option[value=" + data.visibility.toLowerCase() + "]").attr("selected", "selected");
                    $("#edit_album").modal();
                }
            });
        });
        
        $("#edit_album button[type=reset]").click(function() {
            $("#edit_album").modal("hide");
            $("#edit_album #visibility option").removeAttr("selected");
        });
        
        $("#administration_tokens button").click(function() {
            var id = $(this).parent().parent().attr("id");
            $.ajax({
                url: "token/" + id,
                success: function(data) {
                    console.log(data);
                    $("#edit_token input[type=hidden]").val(data.token.id);
                    $("#edit_token #label").val(data.token.label);
                    
                    for (var i = 0 ; i < data.token.albums.length ; i++) {
                        $("#edit_token #albums option[value=" + data.token.albums[i] + "]").attr("selected", "selected");
                    }
                    
                    $("#edit_token").modal();
                }
            });
        });
        
        $("#edit_token button[type=reset]").click(function() {
            $("#edit_token").modal("hide");
            $("#edit_token #albums option").removeAttr("selected");
        });
    }
    
    Sammy(function() {
        
        this.get("#/photo/:photo", function() {
            ajax({
                url: "deploy/api/photo/" + this.params['photo'],
                success: function(data) {
                    loadTemplate("photo", data);
                }
            });
        });
        
        this.get('#/album/:album', function() {
            ajax({
                url: "api/album/" + this.params['album'],
                success: function(data) {
                    loadTemplate("album", data);
                }
            });
        }),
        
        this.get('#/album/:album(/.*)?', function() {
            var photoId = this.params['splat'][0];
            var index = $(".photos a.thumbnail").index($("#" + photoId.substr(1)));
            if (index == -1) {
                ajax({
                    url: "api/album/" + this.params['album'],
                    success: function(data) {
                        loadTemplate("album", data, null, function(){
                            if (photoId.length > 1) {
                                var index = $(".photos a.thumbnail").index($("#" + photoId.substr(1)));
                                console.log(index);
                                if (index != -1) {
                                    var slideshowData = [];
                                    for (var i = 0 ; i < data.photos.length ; i++) {
                                        slideshowData.push({
                                            "id" : "/album/" + data.album.name + "/" + data.photos[i].id,
                                            "url" : "photo/"+data.photos[i].id,
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
                    slideshowData.push({
                        "id" : "/album/" + this.params['album'] + "/" + photos[i].id,
                        "url" : "photo/"+photos[i].id,
                        "caption" : img.attr("title")
                    });
                }
                fullscreen(index, slideshowData);
            }
        }),
        
        /* ******************** */
        /* Aministration access */
        /* ******************** */
        this.post('#/administration/configuration', function() {
            var context = this;
            $("#configuration-form input").attr("disabled", "disabled");
            var data = {
                "sourceDirectory" : this.params["sourceDirectory"], 
                "targetDirectory" : this.params["targetDirectory"]
            };
            $.ajax({
                url: "administration/configuration",
                type : "post",
                data : data,
                success: function() {
                    $("#configuration-form input").removeAttr("disabled");
                    context.redirect("#/administration");
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
                url: "album",
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
                url: "group?label=" + this.params["label"],
                type : "put",
                success: function() {
                    context.redirect("#/administration");
                }
            });
            return false;
        });
        
        this.post('#/token', function() {
            var context = this;
            var data = {
                "id" : this.params['id'],
                "label" : this.params['label'],
                "albums" : this.params['albums']
            };
            console.log(data);
            $.ajax({
                url: "token",
                type : "post",
                data: data,
                success: function() {
//                    context.redirect("#/administration");
                }
            });
            return false;
        });
        
        this.get('#/administration', function() {
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
                                $("#sync-progress h3 span").html(data.percent + "&nbsp;%");
                                $("#sync-progress .bar").css("width", data.percent+"%");
                                if (data.percent < 100) {
                                    $("#configuration-form input").attr("disabled", "disabled");
                                    setTimeout(getSyncStatus, 2000);
                                } else {
                                    $("#configuration-form input").removeAttr("disabled");
                                    $("#sync-progress").removeClass("alert-info");
                                    $("#sync-progress").addClass("alert-success");
                                    $("#sync-progress .progress").removeClass("progress-info");
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
                url: "session",
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
                url: "api/albums",
                success: function(data) {
                    loadTemplate("home", data);
                }
            });
        }); // End route
        
    }).run("#/");
    
    
    $.getDocHeight = function(){
        return Math.max(
            $(document).height(),
            $(window).height(),
            /* For opera: */
            document.documentElement.clientHeight
            );
    };
            
    function exitFullscreen() {
        var elt = document.getElementById("fullscreenContainer");
        document.body.removeChild(elt);
        var controls = document.getElementById("rs-controls-slideshow-div");
        if (controls) {
            document.body.removeChild(controls);
        }
    }
            
    function createBg() {
        var container = document.createElement("div");
        container.id = "fullscreenContainer";
        container.style.height = $.getDocHeight() + "px";
                
        var elt = document.createElement("div");
        elt.id = "slideshow-div";
        elt.className = "rs-slideshow";
        elt.style.position = "fixed";
        elt.style.top = "0px";
        elt.style.left = "0px";
        elt.style.height = Math.round(window.innerHeight) + "px";
        elt.onclick = exitFullscreen;

        container.appendChild(elt);
        return container;
    }
            
    function fullscreen(index, data) {
        var elt = createBg();
        document.body.appendChild(elt);
        $('#slideshow-div').rsfSlideshow(
        {
            autostart : false,
            transition: 500,
            slides: data,
            controls: {
                previousSlide: {
                    auto: true
                },    //    auto-generate a "previous slide" control
                nextSlide: {
                    auto: true
                }    //    auto-generate a "next slide" control
            },
            effect: 'fade'
        }
        );
        $('#slideshow-div').rsfSlideshow(
            'goToSlide', index
            );
    }
    
});
