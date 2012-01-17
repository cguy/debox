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
                    $('h1').html(data.albumName);
                    $('#albums').hide();
                    $('#photo').hide();
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
                    $('h1').html(data.photo + "<small>" + data.album + "</small>");
                    $('#albums').hide();
                    $('#photos').hide();
                    $('#photo').html(html);
                    $('#photo').fadeIn();
                }
            });
        }); // End route
        
        /* ******************* */
        /* Home page           */
        /* ******************* */
        this.get('/', function() {
            $('h1').html("Accueil");
            $('#photos').hide();
            $('#photo').hide();
            $('#albums').fadeIn();
        }); // End route
                    
    }).run();
});

