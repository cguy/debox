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
app.before({except: null}, function() {
    if (this.verb == "get") {
        $("html, body").animate({scrollTop: 0}, 0);
        if (this.path.indexOf("#/administration") == -1) {
            delete allAlbums;
            delete allTokens;
        }
        var regex = new RegExp("#\/album\/([a-zA-Z0-9_-]+)\/([a-zA-Z0-9_-]+)");
        if (!regex.test(this.path) && document.getElementById("fullscreenContainer") != null) {
            exitFullscreen();
        }
    }
});

app.get('#/album/:album', function() {
    if ($("h1").attr("id") == this.params['album'] && document.getElementById("fullscreenContainer") != null) {
        return;
    }
            
    ajax({
        url: computeUrl("album/" + this.params['album']),
        success: loadAlbum
    });
});
        
app.get('#/album/:album(/.*)?', function() {
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
                data.album.photos = data.photos;
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
            slideshowData.push({
                "id" : "/album/" + this.params['album'] + "/" + photos[i].id,
                "url" : $(photos[i]).attr("fullScreenUrl"),
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
});

/* ******************* */
/* Facebook callback   */
/* ******************* */
app.get('#_=_', function() {
    this.redirect("#/");
});

/* ******************* */
/* Home page           */
/* ******************* */
app.get('#/', function() {
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
