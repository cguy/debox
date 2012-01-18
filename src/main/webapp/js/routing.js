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
                    var list = data.list;
                    var html = '<ul class="thumbnails">';
                    for (var i = 0 ; i < list.length ; i++) {
                        html += '<li class="span2">';
                        html += '\n\t<a class="thumbnail" href="#/photo/' + data.albumName + '/' + data.names[i] + '"><img  src="' + list[i] + '" style="width:160px;" /></a>';
                        html += '</li>';
                    }
                    html += '</div>';
                    
                    $('#photos').fadeOut(function() {
                        $('#photos').html(html);
                        $('#photos').fadeIn();
                    });
                }
            });
        }); // End route
        
        /* ******************* */
        /* Display a photo     */
        /* ******************* */
        this.get('#/photo/:album/:photo', function() {
            $('h1').html(this.params['photo'] + "<small>" + this.params['album'] + "</small>");
            $('#albums').hide();
            $('#photos').hide();
            $('#administration').hide();
            
            var path = this.path.substr(0, this.path.lastIndexOf('/'));
            path = path.replace("#/photo", "#/album");
            $.ajax({
                url: "deploy/api/photo/" + this.params['album'] + "/" + this.params['photo'],
                success: function(data) {
                    var html = '<ul><li><a href="' + path + '">Retour à l\'album</a></li></ul>';
                    html += '<div class="thumbnails" style="text-align: center;">';
                    html += '<a class="thumbnail" href="/deploy/photo/' + data.album + '/' + data.photo + '" id="' + data.photo + '" style="display:inline-block; float:none;">';
                    html += '<img src="/deploy/photo/' + data.album + '/' + data.photo + '" style="max-height:700px;max-width:100%;" />';
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
            
            $.ajax({
                url: "api/albums",
                success: function(data) {
                    var html = "";
                    if (data.length == 0) {
                        html += '<p class="alert">Aucun album n\'a été créé pour le moment !</p>'
                    } else {
                        console.log(data);
                        html += '<h2>Liste des albums</h2>';
                        html += '<table>';
                        html += '<thead>';
                        html += '<tr>';
                        html += '<th>Nom de l\'album</th>';
                        html += '<th>Répertoire source</th>';
                        html += '<th>Action</th>';
                        html += '<th></th>';
                        html += '</tr>';
                        html += '</thead>';
                        html += '<tbody>';
                        for (var i = 0 ; i < data.length ; i++) {
                            html += '<tr>';
                            html += '<td>' + data[i].name + '</td>';
                            html += '<td>' + data[i].source + '</td>';
                            html += '<td></td>';
                            html += '</tr>';
                        }
                        html += '</tbody>';
                        html += '</table>';
                    }
                    
                    html += '<h2>Création d\'un nouvel album</h2>';
                    html += '<form method="put" class="form-horizontal" action="#/administration/album">';
                    html += '<fieldset class="control-group">';
                    html += '<label class="control-label" for="name">Nom de l\'album</label>';
                    html += '<div class="controls controls-group">';
                    html += '<input type="text" required name="name" placeholder="Nom de l\'album" />';
                    html += '</div>';
                    html += '<label class="control-label" for="source">Chemin complet vers l\'album</label>';
                    html += '<div class="controls controls-group">';
                    html += '<input class="span5" type="text" required name="source" placeholder="Chemin complet vers l\'album" />';
                    html += '</div>';
                    html += '</fieldset>';
                    html += '';
                    html += '<input type="submit" class="btn primary pull-right" value="Créer un album" /></form>';
                    
                    $('#administration').html(html);
                    $('#administration').show();
                }
            });
            
        }); // End route
        
        /* ******************* */
        /* Ajour d'un album    */
        /* ******************* */
        this.put('#/administration/album', function() {
            var data = {"name" : this.params["name"], "source" : this.params["source"]};
            var context = this;
            $.ajax({
                url: "administration/album?" + $.param(data, true),
                type : "put",
                success: function(data) {
                    context.redirect("#/administration");
                }
            });
            
            return false;
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
                        html += '<p class="alert">Aucun album n\'a été créé pour le moment !</p>'
                    } else {
                        html += '<ul>';
                        for (var i = 0 ; i < data.length ; i++) {
                            html += '<li>';
                            html += '<a href="#/album/' + data[i].name + '">' + data[i].name + '</a>';
                            html += '</li>';
                        }
                        html += '</ul>';
                    }
                    
                    $('#albums').html(html);
                    $('#albums').fadeIn();
                }
            });
        }); // End route
                    
    }).run("#/");
});

