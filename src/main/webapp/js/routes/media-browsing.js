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
        if (this.path.indexOf("#/administration") == -1 && this.path.indexOf("#/account") == -1) {
            delete allAlbums;
            delete allTokens;
        }
        var regex = new RegExp("#\/albums\/([a-zA-Z0-9_-]+)\/([a-zA-Z0-9_-]+)");
        if (!regex.test(this.path) && document.getElementById("fullscreenContainer") != null) {
            exitFullscreen();
        }
    }
});

app.after(function() {
    if (this.verb == "get") {
        if (this.path.indexOf("#/administration") == -1 && this.path.indexOf("#/account") == -1) {
            $("body").removeClass("admin");
        } else {
            $("body").addClass("admin");
        }
    }
});

app.post('#/albums/:album/comments', function() {
    var albumId = this.params['album'];
    ajax({
        url: computeUrl("albums/" + albumId + "/comments"),
        data: $("#new-album-comment").serializeArray(),
        type: "post",
        success: function(data) {
            if ($(".no-comments").length > 0) {
                $(".no-comments").addClass("hide");
            }
            $(".page-header .comments .badge").removeClass("hide");
            $(".page-header .comments .badge").text(parseInt($(".page-header .comments .badge").text(), 10) + 1);
            data = loadComment(data);
            
            var html = templates["comment"].render(data);
            $(html).insertBefore("#album-comments form");
            $("#album-comments form textarea").val("");
            bindAlbumCommentDeletion();
        }
    });
});

// Album comments
app.get('#/albums/([a-zA-Z0-9-_]*)/comments', function() {
    var id = this.params['splat'][0];
    if ($("h1").attr("id") == id) {
        albumLoaded("comments");
    } else {
        ajax({
            url: computeUrl("albums/" + id),
            success: function(data) {
                loadAlbum(data, albumLoaded, "comments");
            }
        });
    }
});

// Album edition
app.get('#/albums/([a-zA-Z0-9-_]*)/edition', function() {
    var id = this.params['splat'][0];
    if ($("h1").attr("id") == id) {
        albumLoaded("edition");
    } else {
        ajax({
            url: computeUrl("albums/" + id),
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
            url: computeUrl("albums/" + albumId),
            success: function(data) {
                loadAlbum(data, function() {
                    albumLoaded();
                    var index = $(".photos a.thumbnail").index($("*[data-id=" + photoId + "]"));
                    fullscreen(index, data.medias, mode);
                });
            }
        });
    } else if (document.getElementById("fullscreenContainer") == null) {
        var slideshowData = [];
        var photos = $(".photos a.thumbnail");
        for (var i = 0 ; i < photos.length ; i++) {
            var photo = $(photos[i]);
            slideshowData.push(photo.data());
        }
        fullscreen(index, slideshowData, mode);
                
    } else {
        s.gotoItem(photoId);
        s.setMode(mode);
    }
}

app.get('#/albums/([a-zA-Z0-9-_]*)/([a-zA-Z0-9-_]*)', function() {
    loadSlideshow(this);
});

app.get('#/albums/([a-zA-Z0-9-_]*)/([a-zA-Z0-9-_]*)/comments', function() {
    loadSlideshow(this, "/comments");
});

// Photo / video comment
app.post(/#\/(photos|videos)\/([a-zA-Z0-9-_]*)\/comments/, function() {
    var type = this.params['splat'][0].slice(0, -1);
    var mediaId = this.params['splat'][1];
    ajax({
        url: computeUrl(type + "/" + mediaId + "/comments"),
        data: $("#new-media-comment").serializeArray(),
        type: "post",
        success: function(data) {
            $("#slideshow-comments .no-comments").addClass("hide");
            $("#slideshow-options .comments .badge[data-id=" + mediaId + "]").removeClass("hide");
            $("#slideshow-options .comments .badge[data-id=" + mediaId + "]").text(parseInt($("#slideshow-options .comments .badge").text(), 10) + 1);
            data = loadComment(data);
            var html = templates["comment"].render(data);
            $(html).insertBefore("#slideshow-comments form");
            $("#slideshow-comments form textarea").val("");
            $("#slideshow-comments").mCustomScrollbar("update");
            bindPhotoCommentDeletion();
        },
        error : function() {
            alert("Erreur");
        }
    });
});

app.del(/#\/(photos|videos)\/([a-zA-Z0-9-_]*)\/comments\/([a-zA-Z0-9-_]*)/, function() {
    deleteComment(this.params['splat'][2], $("#slideshow-options .comments .badge"), $("#slideshow-comments .no-comments"), $("#remove-comment"));
});

app.del("#/comments/:commentId", function(context) {
    var commentId = this.params["commentId"];
    $.ajax({
        url: "comments/" + commentId,
        type: "delete",
        success: function() {
            _("modal-comment-delete").modal("hide");
            context.redirect("#/account/comments");
        },
        error : function() {
            alert(lang.common.error);
        }
    });
    return false;
});

function deleteComment(id, badgeNode, emptyNode, modalNode) {
    ajax({
        url: "comments/" + id,
        type: "delete",
        success: function() {
            $("#"+id).remove();
            modalNode.modal("hide");
            var count = parseInt(badgeNode.text(), 10) - 1;
            badgeNode.text(count);
            if (count == 0) {
                badgeNode.addClass("hide");
                emptyNode.removeClass("hide");
            }
        },
        error : function() {
            modalNode.modal("hide");
        }
    });
}

app.del('#/albums/([a-zA-Z0-9-_]*)/comments/([a-zA-Z0-9-_]*)', function() {
    deleteComment(this.params['splat'][1], $(".page-header .comments .badge"), $("#album-comments .no-comments"), $("#remove-comment"));
});

// Album loading (grid)
app.get('#/albums/([a-zA-Z0-9-_]*)', function() {
    var id = this.params['splat'][0];
    if ($("h1").attr("id") == id) {
        _("loading").addClass("hide");
        albumLoaded();
    } else {
        ajax({
            url: computeUrl("albums/" + id),
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
    editTitle($("a.brand").text() + " - " + lang.about.tooltip);
    loadTemplate("about");
});

/* ******************* */
/* Home page           */
/* ******************* */
app.get('#/', function() {
    editTitle($("a.brand").text() + " - " + lang.home.title);
    ajax({
        url: computeUrl("albums"),
        success: function(data) {
            for (var i = 0 ; i < data.albums.length ; i++) {
                var album = data.albums[i];
                createAlbum(album);
            }
            data.albums.size = function() {
                return data.albums.length;
            }
            loadTemplate("home", data);
        }
    });
});
