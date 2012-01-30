$("img").error(function(){
    }).attr("src", "img/folder.png");
;

$('.album').error(function() {
    alert('Handler for .error() called.')
}).attr("src", "img/folder.png");

$(document).ready(function() {
    $("#login-cancel").click(function(){
        $("#login-modal").modal('hide');
    });
                
    $("#add-album-cancel").click(function(){
        $("#add-album-modal").modal('hide');
    });
    
    
                
    Sammy(function() {
        /* ******************* */
        /* Browse an album     */
        /* ******************* */
        this.get('#/album/:album', function() {
            $('h1').html(this.params['album']);
            $('#albums').hide();
            $('#photo').hide();
            $('#administration').hide();
            
            $.ajax({
                url: "api/album/" + this.params['album'],
                success: function(data) {
                    console.log(data);
                    var j = 0;
                    console.log(j++);
                    var albums = data['albums'];
                    console.log(j++);
                    var photos = data['photos'];
                    console.log(j++);
                    var html = '';
                    console.log(j++);
                    if (albums && albums.length > 0) {
                    console.log(j++ + " >>>>>>>>>>");
                        html += '<h2>Sous-albums</h2>';
                        html += '<ul class="thumbnails">';
                        for (var i = 0 ; i < albums.length ; i++) {
                            html += '<li class="span3">';
                            html += '<i class="icon folder-open"></i>&nbsp;<a href="#/album/' + albums[i].name + '">' + albums[i].name + '</a>';
                            html += '<a class="thumbnail" href="#/album/' + albums[i].name + '"><img class="album" src="album/' + albums[i].id + '/cover" alt="' + albums[i].name + '" style="background-color:#ddd;width:210px;"/></a>';
                            html += '</li>';
                        }
                        html += '</ul>';
                    console.log(j++);
                    }
                    console.log(j++);
                    if (photos && photos.length > 0) {
                    console.log(j++ + "dwsfsf");
                        if (albums && albums.length > 0) {
                            html += '<h2>Photos</h2>';
                        }
                    console.log(j++);
                        html += '<ul class="thumbnails">';
                        for (var i = 0 ; i < photos.length ; i++) {
                            html += '<li class="span2">';
                            html += '<a class="thumbnail" href="#/photo/' + photos[i].id + '"><img class="album" src="thumbnail/' + photos[i].id + '" alt="' + photos[i].name + '" title="' + photos[i].name + '" style="background-color:#ddd;width:210px;"/></a>';
                            html += '</li>';
                        }
                    console.log(j++);
                        html += '</ul>';
                    }
                    console.log(j++);
                    
                    $('#photos').fadeOut(function() {
                        $('#photos').html(html);
                        $('#photos').fadeIn();
                    });
                }
            });
            return false;
        }); // End route
        
        /* ******************* */
        /* Display a photo     */
        /* ******************* */
        this.get('#/photo/:photo', function() {
            
            $('#albums').hide();
            $('#photos').hide();
            $('#administration').hide();
            
            $.ajax({
                url: "deploy/api/photo/" + this.params['photo'],
                success: function(data) {
                    var photo = data.photo;
                    var album = data.album;
                    $('h1').html(photo.name + "<small>" + album.name + "</small>");
                    var html = '<ul><li><a href="#/album/' + album.name + '">Retour à l\'album</a></li></ul>';
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
        }); // End route
        
        /* ******************* */
        /* Administration      */
        /* ******************* */
        this.get('#/administration', function() {
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
                            console.log(data);
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
                    
                    //                    html += '<section>';
                    //                    html += '<h2 class="page-header">Création d\'un nouvel album</h2>';
                    //                    html += '<form method="put" action="#/administration/album">';
                    //                    html += '<fieldset>';
                    //                    html += '<div class="clearfix">';
                    //                    html += '<label for="name">Nom de l\'album</label>';
                    //                    html += '<div class="input">';
                    //                    html += '<input class="span5" type="text" required name="name" placeholder="Nom de l\'album" />';
                    //                    html += '</div>';
                    //                    html += '</div>';
                    //                    html += '<div class="clearfix">';
                    //                    html += '<label for="source">Chemin complet vers l\'album</label>';
                    //                    html += '<div class="input">';
                    //                    html += '<input class="span5" type="text" required name="source" placeholder="Chemin complet vers l\'album" />';
                    //                    html += '</div>';
                    //                    html += '</div>';
                    //                    html += '</fieldset>';
                    //                    html += '<div class="form-actions">'
                    //                    html += '<input type="submit" class="btn primary" value="Créer un album" />&nbsp;';
                    //                    html += '<button type="reset" class="btn">Annuler</button>';
                    //                    html += '</div>';
                    //                    html += '</form>';
                    //                    html += '</section>';
                    $("#sourceDirectory").val(data.configuration["source_path"]);
                    $("#targetDirectory").val(data.configuration["target_path"]);
                    
                    $('#admin_albums').html(html);
                    $('#administration').show();
                }
            });
            
        }); // End route
        
        /* ***************************************** */
        /* Modification de la configuration générale */
        /* ***************************************** */
        this.post('#/administration/configuration', function() {
            $("#configuration-form input").attr("disabled", "disabled");
            var data = {
                "sourceDirectory" : this.params["sourceDirectory"], 
                "targetDirectory" : this.params["targetDirectory"]
            };
            var context = this;
            $.ajax({
                url: "administration/configuration",
                type : "post",
                data : data,
                success: function(data) {
                    $("#configuration-form input").removeAttr("disabled");
                    context.redirect("#/administration");
                }
            });
        }); // End route
        
        /* **************** */
        /* Ajout d'un album */
        /* **************** */
        this.put('#/administration/album', function() {
            var data = {
                "name" : this.params["name"], 
                "source" : this.params["source"]
            };
            var context = this;
            $.ajax({
                url: "administration/album?" + $.param(data, true),
                type : "put",
                success: function(data) {
                    context.redirect("#/administration");
                }
            });
        }); // End route
        
        /* ******************* */
        /* Home page           */
        /* ******************* */
        this.get('#/', function() {
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
        }); // End route
                    
    }).run("#/");
});

