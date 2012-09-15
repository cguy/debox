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

app.get('#/album/:album(/.*)?', function() {
    var photoId = this.params['splat'][0];
    if (photoId) {
        photoId = photoId.substr(1);
    }
    var index = $(".photos a.thumbnail").index($("*[data-id=" + photoId + "]"));
    if (index == -1) {
        
        function onAlbumLoading(data) {
            if (!photoId || photoId == "edition" || photoId == "comments") {
                $("#alerts .alert").hide();
                
                hideAlbumChoose();
                if (photoId == "edition") {
                    $("#edit_album").addClass("visible");
                    $(".edit-album").addClass("hide");
                    $(".edit-album-cancel").removeClass("hide");
                    
                    $("#photos-edition").removeClass("hide");
                    $("#photos").addClass("hide");
                    
                } else {
                    $("#edit_album").removeClass("visible");
                    $(".edit-album").removeClass("hide");
                    $(".edit-album-cancel").addClass("hide");
                    
                    $("#photos-edition").addClass("hide");
                    $("#photos").removeClass("hide");
                }
                
                var oldHref = $(".page-header .comments").attr("href");
                $('.page-header .comments').tooltip('destroy');
                if (photoId == "comments") {
                    $("#album-content").addClass("comments");
                    $(".page-header .comments").addClass("active");
                    $(".page-header .comments").attr("href", oldHref.replace("/comments", ""));
                    
                    $(".page-header .comments").attr("title", fr.album.comments.hide);
                    
                } else {
                    $(".page-header .comments").attr("href", oldHref + "/comments");
                    $("#album-content").removeClass("comments");
                    $(".page-header .comments").removeClass("active");
                    $(".page-header .comments").attr("title", fr.album.comments.show);
                }
                $('.page-header .comments').tooltip();
                
            } else if (photoId.length > 1) {
                var index = $(".photos a.thumbnail").index($("*[data-id=" + photoId + "]"));
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
            
            // Album deletion binding
            $(".delete").unbind("click");
            $(".delete").click(function() {
                $("#delete-album-modal").modal();
            });
            
            $(".delete-photo").unbind("click");
            $(".delete-photo").click(function() {
                var photoId = $(this).parents("div").attr("data-id");
                $("#delete-photo").attr("action", "#/photo/"+photoId);
                $("#delete-photo").modal();
                return false;
            });
            
            $(".edit-photo").unbind("click");
            $(".edit-photo").click(function() {
                var photoId = $(this).parents("div").attr("data-id");
                $("#edit-photo").attr("action", "#/photo/"+photoId);
                
                var refTitleNode = $(this).parents("div").children(".title");
                $("#edit-photo #photoTitle").val(refTitleNode.text());
                $("#edit-photo").modal();
                return false;
            });
        }
        
        if ($("h1").attr("id") == this.params['album']) {
            onAlbumLoading();
        } else {
            ajax({
                url: computeUrl("album/" + this.params['album']),
                success: function(data) {
                    loadAlbum(data, onAlbumLoading);
                }
            });
        }
        
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
