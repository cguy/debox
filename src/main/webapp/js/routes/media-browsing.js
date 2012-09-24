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
        $("html, body").animate({
            scrollTop: 0
        }, 0);
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

app.post('#/album/:album/comments', function() {
    var albumId = this.params['album'];
    ajax({
        url: computeUrl("album/" + albumId + "/comments"),
        data: $("#new-album-comment").serializeArray(),
        type: "post",
        success: function(data) {
            if ($(".no-comments").length == 1) {
                $(".no-comments").remove();
            }
            
            data = loadComment(data);
            
            var html = templates["comment"].render(data);
            $(html).insertBefore("#album-comments form");
            $("#album-comments form textarea").val("");
        }
    });
});

// Album comments
app.get('#/album/([a-zA-Z0-9-_]*)/comments', function() {
    var id = this.params['splat'][0];
    if ($("h1").attr("id") == id) {
        albumLoaded("comments");
    } else {
        ajax({
            url: computeUrl("album/" + id),
            success: function(data) {
                loadAlbum(data, albumLoaded, "comments");
            }
        });
    }
});

// Album edition
app.get('#/album/([a-zA-Z0-9-_]*)/edition', function() {
    var id = this.params['splat'][0];
    if ($("h1").attr("id") == id) {
        albumLoaded("edition");
    } else {
        ajax({
            url: computeUrl("album/" + id),
            success: function(data) {
                loadAlbum(data, albumLoaded, "edition");
            }
        });
    }
});

// Album alideshow
app.get('#/album/([a-zA-Z0-9-_]*)(/.*)+', function() {
    var albumId = this.params['splat'][0];
    var photoId = this.params['splat'][1].substr(1);
    
    var index = $(".photos a.thumbnail").index($("*[data-id=" + photoId + "]"));
    if (index == -1) {
        ajax({
            url: computeUrl("album/" + albumId),
            success: function(data) {
                loadAlbum(data, function() {
                    albumLoaded();
                    var index = $(".photos a.thumbnail").index($("*[data-id=" + photoId + "]"));
                    var slideshowData = [];
                    for (var i = 0 ; i < data.photos.length ; i++) {
                        slideshowData.push({
                            "id" : "/album/" + data.album.id + "/" + data.photos[i].id,
                            "url" : data.photos[i].url,
                            "caption" : data.photos[i].name
                        });
                    }
                    fullscreen(index, slideshowData);
                });
            }
        });
    } else if (document.getElementById("fullscreenContainer") == null) {
        var slideshowData = [];
        var photos = $(".photos a.thumbnail");
        for (var i = 0 ; i < photos.length ; i++) {
            var photo = $(photos[i]);
            slideshowData.push({
                "id" : "/album/" + albumId + "/" + photo.attr("data-id"),
                "url" : photo.attr("fullScreenUrl"),
                "caption" : photo.attr("data-title")
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

// Album loading (grid)
app.get('#/album/([a-zA-Z0-9-_]*)', function() {
    var id = this.params['splat'][0];
    if ($("h1").attr("id") == id) {
        albumLoaded();
    } else {
        ajax({
            url: computeUrl("album/" + id),
            success: function(data) {
                loadAlbum(data, albumLoaded);
            }
        });
    }
});

/* ******************* */
/* Facebook callback   */
/* ******************* */
app.get('#_=_', function() {
    this.redirect("#/");
});

/* ******************* */
/* About page           */
/* ******************* */
app.get('#/about', function() {
    editTitle($("a.brand").text() + " - " + fr.about.tooltip);
    loadTemplate("about");
});

/* ******************* */
/* Home page           */
/* ******************* */
app.get('#/', function() {
    editTitle($("a.brand").text() + " - " + fr.home.title);
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
});
