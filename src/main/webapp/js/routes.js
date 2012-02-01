$(document).ready(function() {
    
    $('form').submit(function(evt) {
        var data = $(this).serialize();
        var method = $(this).attr("method");
        var action = $(this).attr("action");
        if (action.indexOf("#/") > -1) {
            action = action.substr(2);
        }
        console.log(action);
        console.log(method);
        console.log(data);
        $.ajax({
            url: action,
            type : method,
            data : data,
            success: function(data) {
                app.navigate(action, {trigger: true});
            }
        });
        
        evt.preventDefault();
    });
    
    var Workspace = Backbone.Router.extend({
        
        routes : {
            'album/:album' : "getAlbum",
            'photo/:photo' : "getPhoto",
            'administration/configuration': "editConfiguration",
            'administration': "administration",
            '': "home"
        },

        home : function() {
            $('h1').html("Accueil");
            $('#photos').hide();
            $('#photo').hide();
            $('#administration').hide();
            
            $.ajax({
                url: "api/albums",
                success: function(data) {
                    var html = "";
                    if (data.length == 0) {
                        html += '<p class="alert"><strong>Attention : </strong>Aucun album n\'a été créé pour le moment !</p>'
                    } else {
                        html += '<ul class="thumbnails">';
                        for (var i = 0 ; i < data.length ; i++) {
                            html += '<li class="span3">';
                            html += '<i class="icon folder-open"></i>&nbsp;<a href="#/album/' + data[i].name + '">' + data[i].name + '</a>';
                            html += '<a class="thumbnail" href="#/album/' + data[i].name + '"><img class="album" src="album/' + data[i].id + '/cover" alt="' + data[i].name + '" style="background-color:#ddd;width:210px;"/></a>';
                            html += '</li>';
                        }
                        html += '</ul>';
                    }
                    
                    $('#albums').html(html);
                    $('#albums').show();
                }
            });
        },
    
        getAlbum : function(album) {
            $('h1').html(album);
            $('#albums').hide();
            $('#photo').hide();
            $('#administration').hide();
            
            $.ajax({
                url: "api/album/" + album,
                success: function(data) {
                    var albums = data['albums'];
                    var photos = data['photos'];
                    var html = '<div class="btn-toolbar" style="margin-top: 18px;">';
                    html += '<div class="btn-group">';
                    if (data.parent) {
                        html += '<a href="#/album/' + data.parent.name + '" class="btn"><i class="icon folder-open"></i>&nbsp;Retour à l\'album : ' + data.parent.name + '</a>';
                    } else {
                        html += '<a href="#/" class="btn"><i class="icon folder-open"></i>&nbsp;Retour à la liste des albums</a>';
                    }
                    html += '</div>';
                    html += '<div class="btn-group">';
                    html += '<a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon download"></i>&nbsp;Télécharger les photos de cet album&nbsp;<span class="caret"></span></a>';
                    html += '<ul class="dropdown-menu">';
                    html += '<li><a href="download/album/' + data.album.id + '/min">Taille réduite des photos (1600px)</a></li>';
                    html += '<li><a href="download/album/' + data.album.id + '">Taille originale des photos</a></li>';
                    html += '</ul>';
                    html += '</div>';
                    html += '</div>';
                    html += '<hr />';
                    
                    if (albums && albums.length > 0) {
                        html += '<h2>Sous-albums</h2>';
                        html += '<ul class="thumbnails">';
                        for (var i = 0 ; i < albums.length ; i++) {
                            html += '<li class="span3">';
                            html += '<i class="icon folder-open"></i>&nbsp;<a href="#/album/' + albums[i].name + '">' + albums[i].name + '</a>';
                            html += '<a class="thumbnail" href="#/album/' + albums[i].name + '"><img class="album" src="album/' + albums[i].id + '/cover" alt="' + albums[i].name + '" style="background-color:#ddd;width:210px;"/></a>';
                            html += '</li>';
                        }
                        html += '</ul>';
                    }
                    if (photos && photos.length > 0) {
                        if (albums && albums.length > 0) {
                            html += '<h2>Photos</h2>';
                        }
                        html += '<ul class="thumbnails">';
                        for (i = 0 ; i < photos.length ; i++) {
                            html += '<li class="span2">';
                            html += '<a class="thumbnail" href="#/photo/' + photos[i].id + '"><img class="album" src="thumbnail/' + photos[i].id + '" alt="' + photos[i].name + '" title="' + photos[i].name + '" style="background-color:#ddd;width:210px;"/></a>';
                            html += '</li>';
                        }
                        html += '</ul>';
                    }
                    
                    $('#photos').fadeOut(function() {
                        $('#photos').html(html);
                        $('#photos').fadeIn();
                    });
                }
            });
        },
    
        getPhoto : function(photo) {
            $('#albums').hide();
            $('#photos').hide();
            $('#administration').hide();
            
            $.ajax({
                url: "deploy/api/photo/" + photo,
                success: function(data) {
                    var photo = data.photo;
                    var album = data.album;
                    $('h1').html(photo.name + "<small>" + album.name + "</small>");
                    var html = '<div class="btn-toolbar" style="margin-top: 18px;">';
                    html += '<div class="btn-group">';
                    html += '<a href="#/album/' + album.name + '" class="btn"><i class="icon folder-open"></i>&nbsp;Retour à l\'album</a>';
                    html += '</div>';
                    html += '<div class="btn-group">';
                    html += '<a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon download"></i>&nbsp;Télécharger la photo&nbsp;<span class="caret"></span></a>';
                    html += '<ul class="dropdown-menu">';
                    html += '<li><a href="download/photo/' + photo.id + '/min">Taille réduite (1600px)</a></li>';
                    html += '<li><a href="download/photo/' + photo.id + '">Taille originale</a></li>';
                    html += '</ul>';
                    html += '</div>';
                    html += '</div>';
                    html += '<hr />';
                    
                    html += '<div class="media-grid" style="text-align: center;">';
                    var imageUrl = 'photo/' + photo.id + '" id="' + photo.name;
                    html += '<a class="thumbnail" href="' + imageUrl + '" style="display:inline-block; float:none;">';
                    html += '<img src="' + imageUrl + '" style="max-height:700px;max-width:100%;" />';
                    html += '</a>';
                    html += '</div>';
                    
                    $('#photo').html(html);
                    $('#photo').fadeIn();
                }
            });
        },

        administration : function() {
            $('h1').html("Administration");
            $('#photos').hide();
            $('#photo').hide();
            $('#albums').hide();
            $('#administration').hide();
            $("#sync-progress").hide();
            
            $.ajax({
                url: "administration",
                success: function(data) {
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
                    
                    var albums = data.albums;
                    var html = '<h2 class="page-header">Liste des albums</h2>';
                    if (albums.length == 0) {
                        html += '<p class="alert">Aucun album n\'a été créé pour le moment !</p>'
                    } else {
                        html += '<table class="table table-striped table-bordered table-condensed">';
                        html += '<thead>';
                        html += '<tr>';
                        html += '<th>Nom de l\'album</th>';
                        html += '<th>Répertoire source</th>';
                        html += '<th>Action</th>';
                        html += '</tr>';
                        html += '</thead>';
                        html += '<tbody>';
                        for (var i = 0 ; i < albums.length ; i++) {
                            html += '<tr>';
                            html += '<td>' + albums[i].name + '</td>';
                            html += '<td>' + albums[i].sourcePath + '</td>';
                            html += '<td></td>';
                            html += '</tr>';
                        }
                        html += '</tbody>';
                        html += '</table>';
                    }
                    
                    $("#sourceDirectory").val(data.configuration["source_path"]);
                    $("#targetDirectory").val(data.configuration["target_path"]);
                    
                    $('#admin_albums').html(html);
                    $('#administration').show();
                }
            });
        },

        editConfiguration : function(sourceDirectory, targetDirectory) {
//            $("#configuration-form input").attr("disabled", "disabled");
            var data = {
                "sourceDirectory" : sourceDirectory, 
                "targetDirectory" : targetDirectory
            };
            //            $.ajax({
            //                url: "administration/configuration",
            //                type : "post",
            //                data : data,
            //                success: function(data) {
            //                    $("#configuration-form input").removeAttr("disabled");
            //                    app.navigate("administration");
            //                }
            //            });
            app.navigate("administration", {
                trigger:true
            });
        }

    });
    
    var app = new Workspace();
    Backbone.history.start();    
});
