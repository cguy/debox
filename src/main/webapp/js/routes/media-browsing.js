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
function loadSlideshow(context, mode) {
    var albumId = context.params['splat'][0];
    var photoId = context.params['splat'][1];
    var index = $(".photos a.thumbnail").index($("*[data-id=" + photoId + "]"));
    if (index == -1) {
        ajax({
            url: computeUrl("album/" + albumId),
            success: function(data) {
                loadAlbum(data, function() {
                    albumLoaded();
                    var index = $(".photos a.thumbnail").index($("*[data-id=" + photoId + "]"));
                    fullscreen(index, data.photos, mode);
                });
            }
        });
    } else if (document.getElementById("fullscreenContainer") == null) {
        var slideshowData = [];
        var photos = $(".photos a.thumbnail");
        for (var i = 0 ; i < photos.length ; i++) {
            var photo = $(photos[i]);
            slideshowData.push({
                "date": photo.attr("data-date"),
                "id" : photo.attr("data-id"),
                "title" : photo.attr("data-title"),
                "thumbnail": photo.attr("data-thumbnail"),
                "url" : photo.attr("data-url")
            });
        }
        fullscreen(index, slideshowData, mode);
                
    } else {
        s.gotoItem(photoId);
        s.setMode(mode);
    }
}

app.get('#/album/([a-zA-Z0-9-_]*)/([a-zA-Z0-9-_]*)', function() {
    loadSlideshow(this);
});

app.get('#/album/([a-zA-Z0-9-_]*)/([a-zA-Z0-9-_]*)/comments', function() {
    loadSlideshow(this, "/comments");
});

app.post('#/album/([a-zA-Z0-9-_]*)/([a-zA-Z0-9-_]*)/comments', function() {
    var photoId = this.params['splat'][1];
    ajax({
        url: computeUrl("photo/" + photoId + "/comments"),
        data: $("#new-photo-comment").serializeArray(),
        type: "post",
        success: function(data) {
            $("#slideshow-comments .no-comments").addClass("hide");
            data = loadComment(data);
            var html = templates["comment"].render(data);
            $(html).insertBefore("#slideshow-comments form");
            $("#slideshow-comments form textarea").val("");
            $("#slideshow-comments").mCustomScrollbar("update");
        }
    });
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
