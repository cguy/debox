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
$.getDocHeight = function() {
    return Math.max(
            $(document).height(),
            $(window).height(),
            /* For opera: */
            document.documentElement.clientHeight
            );
};
$.getDocWidth = function() {
    return Math.max(
            $(document).width(),
            $(window).width(),
            /* For opera: */
            document.documentElement.clientWidth
            );
};

(function() {
    var fullScreenApi = {
        supportsFullScreen: false,
        isFullScreen: function() {
            return false;
        },
        requestFullScreen: function() {
        },
        cancelFullScreen: function() {
        },
        fullScreenEventName: '',
        prefix: ''
    },
            browserPrefixes = 'webkit moz o ms khtml'.split(' ');

    // check for native support
    if (typeof document.cancelFullScreen != 'undefined') {
        fullScreenApi.supportsFullScreen = true;
    } else {
        // check for fullscreen support by vendor prefix
        for (var i = 0, il = browserPrefixes.length; i < il; i++) {
            fullScreenApi.prefix = browserPrefixes[i];

            if (typeof document[fullScreenApi.prefix + 'CancelFullScreen' ] != 'undefined') {
                fullScreenApi.supportsFullScreen = true;

                break;
            }
        }
    }

    // update methods to do something useful
    if (fullScreenApi.supportsFullScreen) {
        fullScreenApi.fullScreenEventName = fullScreenApi.prefix + 'fullscreenchange';

        fullScreenApi.isFullScreen = function() {
            switch (this.prefix) {
                case '':
                    return document.fullScreen;
                case 'webkit':
                    return document.webkitIsFullScreen;
                default:
                    return document[this.prefix + 'FullScreen'];
            }
        }
        fullScreenApi.requestFullScreen = function(el) {
            if (!fullScreenApi.supportsFullScreen) {
                return false;
            }
            return (this.prefix === '') ? el.requestFullScreen() : el[this.prefix + 'RequestFullScreen']();
        }
        fullScreenApi.cancelFullScreen = function() {
            if (!fullScreenApi.supportsFullScreen) {
                return false;
            }
            return (this.prefix === '') ? document.cancelFullScreen() : document[this.prefix + 'CancelFullScreen']();
        }
    }

    // jQuery plugin
    if (typeof jQuery != 'undefined') {
        jQuery.fn.requestFullScreen = function() {
            return this.each(function() {
                if (fullScreenApi.supportsFullScreen) {
                    fullScreenApi.requestFullScreen(this);
                }
            });
        };
    }

    // export api
    window.fullScreenApi = fullScreenApi;
})();

function exitFullscreen() {
    delete s;
    $(document.body).removeClass("fixed");
    document.body.removeChild(document.getElementById("fullscreenContainer"));
}

function fullscreen(index, data, mode, canDownloadMedia) {
    s = new Slideshow();
    s.canDownloadMedia = !!canDownloadMedia;
    s.setItems(data, index);
    s._loadComments();
    s.show();
    s.setMode(mode);
}

jwerty.key('←', function() {
    if (document.getElementById("fullscreenContainer") != null) {
        window.location.hash = window.location.hash.replace(s.getCurrentId(), s.getId(s.getPreviousIndex()));
    }
});

jwerty.key('→', function() {
    if (document.getElementById("fullscreenContainer") != null) {
        window.location.hash = window.location.hash.replace(s.getCurrentId(), s.getId(s.getNextIndex()));
    }
});

jwerty.key('esc', function() {
    if (document.getElementById("fullscreenContainer") != null) {
        exitFullscreen();
    }
});

function Slideshow() {
    // Please adjust with width and padding value
    // from #slideshow-drawer entry in slideshow.css
    this.DRAWER_MARGIN = 340;

    this.items = [];
    this.index = 0;

    this.convert = function(old) {
        var result = [old.length];
        for (var i = 0; i < old.length; i++) {
            var oldItem = old[i];
            var item = {};
            for (var key in oldItem) {
                item[key.toLowerCase()] = oldItem[key] ? oldItem[key] : null;
            }
            result[i] = item;
        }
        return result;
    };

    this.setIndex = function(index) {
        this.index = index;
        
        $("#slideshow-options .comments .commentsCount").attr("data-id", this.items[this.index].id);

        this.setLabel();
//        this.refreshLinks();
    };
    
    this.getAlbumId = function() {
        var hash = location.hash;
        var prefix = "#/albums/";
        var prefixIndex = hash.indexOf(prefix) + prefix.length;
        var albumId = hash.substring(prefixIndex, hash.indexOf("/", prefixIndex));
        return albumId;
    };
    
    this.getBasePath = function() {
        return "#/albums/" + this.getAlbumId() + "/" + this.getCurrentId(); 
    };

    this.refreshLinks = function() {
        var hash = location.hash;
        _("slideshow-previous").attr("href", hash.replace(this.getCurrentId(), this.getId(this.getPreviousIndex())));
        _("slideshow-next").attr("href", hash.replace(this.getCurrentId(), this.getId(this.getNextIndex())));
        
        var isCommentsMode = /\/comments$/.test(hash);
        var path = this.getBasePath();
        path += isCommentsMode ? "" : "/comments";
        $("#slideshow-options .comments").attr("href", path);
        
        var currentItem = this.items[this.index];
        var isVideo = !!currentItem.video;
        var path = "photos";
        if (isVideo) {
            path = "videos";
        }
        _("new-media-comment").attr("action", "#/" + path + "/" + currentItem.id + "/comments");
        
        var commentsText = lang.comments.show;
        if (isCommentsMode) {
            commentsText = lang.comments.hide;
        }
        _("slideshow-help-label").text(commentsText);
        $("#slideshow-options > a.comments").attr("title", commentsText).unbind("mouseenter").unbind("mouseout").mouseenter(function() {
            var title = $(this).attr("title");
            if (!title) {
                title = $(this).parents("a").attr("title");
            }
            _("slideshow-options").addClass("show");
            _("slideshow-help-label").text(title);
            
        }).mouseout(function() {
            _("slideshow-options").removeClass("show");
        });
    };

    this.getCurrentId = function() {
        return this.items[this.index].id;
    };

    this.getId = function(index) {
        return this.items[index].id;
    };
    
    this.setLabel = function() {
        var dl = fr.slideshow.download.photo;
        var url = this.items[this.index].url;
        if (this.items[this.index].video) {
            dl = fr.slideshow.download.video;
            url = this.items[this.index].h264url;
        }
        var suffix = "";
        if (this.canDownloadMedia) {
            console.log(this.items[this.index]);
            suffix = ' - <a href="' + url + '" download="'+this.items[this.index].filename+'">' + dl + '</a>';
        }
        _("slideshow-label").html(this.items[this.index].title + " - " + (this.index+1) + " " + lang.common.on + " " + this.items.length + suffix);
    };

    this.setItems = function(items, index) {
        this.items = this.convert(items);
        this.setIndex(index);
        
        var html = templates["slideshow"].render({data:this.items, i18n: fr, config: _config}, templates);
        $(document.body).append(html);
        
        var prevIndex = this.getPreviousIndex();
        var nextIndex = this.getNextIndex();

        var currentMedia = this.items[this.index];
        var previousMedia = this.items[prevIndex];
        var nextMedia = this.items[nextIndex];
        
        var node = this.createNode(currentMedia);
        node.className = "current";
        
        if (previousMedia && previousMedia.id != currentMedia.id) {
            node = this.createNode(previousMedia);
            node.className = "previous";
        }
        if (nextMedia && nextMedia.id != currentMedia.id) {
            node = this.createNode(nextMedia);
            node.className = "next";
        }
        
        this.setLabel();
    };

    this.setSize = function(id, w, h) {
        var index = this.getItemIndex(id);
        this.items[index].width = w;
        this.items[index].height = h;
    };

    this.show = function() {
        $(document.body).addClass("fixed");
//        fullScreenApi.requestFullScreen($("#fullscreenContainer").get(0));
        
        var href = location.href;
        href = href.substring(0, href.indexOf("/", href.indexOf("#/albums/") + "#/albums/".length));
        $("#slideshow-options .exit").attr("href", href).click(function() {
            exitFullscreen();
        });
        _("slideshow-comments").mCustomScrollbar({
            scrollInertia: 500,
            mouseWheel: 50,
            advanced:{
                updateOnContentResize: true
            }
        });
    };
    
    this.setMode = function(mode) {
        if (!mode) {
            this._hideDrawer();
        } else if (mode == "/comments") {
            this._showComments();
        }
    };
    
    this._showComments = function() {
        this._displayDrawer();
        _("fullscreenContainer").addClass("comments");
        this.refreshLinks();
        this._loadComments();
    };
    
    this._loadComments = function() {
        if (!_config.authenticated) {
            return;
        }
        var id = this.getCurrentId();
        var isVideo = !!this.items[this.index].video;
        var path = "photos";
        if (isVideo) {
            path = "videos";
        }
        ajax({
            url: computeUrl(path + "/" + id + "/comments"),
            success: function(data) {
                if (data.mediaId != id) {
                    return;
                }
                $("#slideshow-comments .comment").remove();
                for (var i = 0 ; i < data.comments.length ; i++) {
                    var comment = loadComment(data.comments[i]);
                    var html = templates["comment"].render(comment);
                    $(html).insertBefore("#slideshow-comments form");
                    $("#slideshow-comments form textarea").val("");
                }
                if (data.comments.length == 0) {
                    $("#slideshow-comments .no-comments").removeClass("hide");
                    $("#slideshow-options .comments .commentsCount").addClass("hide");
                } else {
                    $("#slideshow-comments .no-comments").addClass("hide");
                    $("#slideshow-comments").mCustomScrollbar("update");
                    $("#slideshow-options .comments .commentsCount").removeClass("hide").text(data.comments.length);
                }
                bindPhotoCommentDeletion();
            },
            error: function() {
                console.log("Error during comments loading");
            }
        });
    };
    
    this._displayDrawer = function() {
        _("fullscreenContainer").addClass("drawer");
        _("slideshow-drawer").removeClass("hide");
        this._resetMargin();
    };
    
    this._hideDrawer = function() {
        _("slideshow-drawer").addClass("hide");
        _("fullscreenContainer").removeClass("drawer");
        this.getCurrentNode().style.right = "34%";
        this.getCurrentNode().style.maxWidth = "90%";
        this.refreshLinks();
    };
    
    this._resetMargin = function() {
        this.getCurrentNode().style.right = (this.getMediasNode().clientWidth * .34 + this.DRAWER_MARGIN)+"px";
        this.getCurrentNode().style.maxWidth = (this.getMediasNode().clientWidth / 3 - this.DRAWER_MARGIN) * .8+"px";
        this.getPreviousNode().style.right = null;
        this.getNextNode().style.right = null;
    };

    this.hide = function() {
        document.body.removeChild(document.getElementById("fullscreenContainer"));
    };
    
    this.getMediasNode = function() {
        return _("fullscreenContainer_photos").get(0);
    };
    
    this.getPreviousNode = function(item) {
        if (!item) {
            item = this.items[this.getPreviousIndex()];
        }
        var node = $("#fullscreenContainer_photos > .previous")[0];
        node = this._getCompatibleNode(node, item);
        node.className = "previous";
        return node;
    };
    
    this.getNextNode = function(item) {
        if (!item) {
            item = this.items[this.getNextIndex()];
        }
        var node = $("#fullscreenContainer_photos > .next")[0];
        node = this._getCompatibleNode(node, item);
        node.className = "next";
        return node;
    };
    
    this.getCurrentNode = function(item) {
        if (!item) {
            item = this.items[this.index];
        }
        var node = $("#fullscreenContainer_photos > .current")[0];
        node = this._getCompatibleNode(node, item);
        node.className = "current";
        return node;
    };
    
    this._getCompatibleNode = function(node, item) {
        if (!node) {
            return this.createNode(item);
            
        } else if (node.id == item.id) {
            return node;
            
        } else if (node instanceof HTMLImageElement && item.photo) {
            node.id = item.id;
            node.src = item.thumbnailurl;
            
        } else if (node instanceof HTMLVideoElement && item.video) {
            node.id = item.id;
            node.poster = item.thumbnailurl;
            
            node.innerHTML = "";
            if (item.oggurl) {
                node.append('<source src="' + item.oggurl + '" type="video/ogg"/>');
            }
            if (item.webmurl) {
                node.append('<source src="' + item.webmurl + '" type="video/webm"/>');
            }
            if (item.h264url) {
                node.append('<source src="' + item.h264url + '" type="video/mp4"/>');
            }
            
        } else {
            node.remove();
            node = this.createNode(item);
        }

        return node;
    };
    
    this.createNode = function(item) {
        var container = _("fullscreenContainer_photos");
        var template = templates["slideshow.photo"];
        if (item.video) {
            template = templates["slideshow.video"];
        }
        var html = template.render(item);
        container.append(html);
        return _(item.id)[0];
    };
    
    this.previous = function() {
        var prevIndex = this.getPreviousIndex();

        var previous = $("#fullscreenContainer_photos > .previous")[0];
        var current = $("#fullscreenContainer_photos > .current")[0];
        $("#fullscreenContainer_photos > .next").remove();
        
        if (current instanceof HTMLVideoElement) {
            current.pause();
        }
        
        previous.className = "current";
        current.className = "next";
        
        this.getPreviousNode(this.items[this.getPreviousIndex(prevIndex)]);
        
        this.index = prevIndex;
        
        this.setLabel();
        $("#slideshow-options .comments .commentsCount").attr("data-id", this.items[this.index].id);
        this._resetMargin();
        this.refreshLinks();
    };

    this.next = function() {
        var nextIndex = this.getNextIndex();

        var next = $("#fullscreenContainer_photos > .next")[0];
        var current = $("#fullscreenContainer_photos > .current")[0];
        $("#fullscreenContainer_photos > .previous").remove();
        
        if (current instanceof HTMLVideoElement) {
            current.pause();
        }
        
        next.className = "current";
        current.className = "previous";
        
        this.getNextNode(this.items[this.getNextIndex(nextIndex)]);
        
        this.index = nextIndex;
        
        this.setLabel();
        $("#slideshow-options .comments .commentsCount").attr("data-id", this.items[this.index].id);
        this._resetMargin();
        this.refreshLinks();
    };

    this.gotoItem = function(itemId) {
        var index = this.getItemIndex(itemId);
        if (this.isNextIndex(index)) {
            this.next();
        } else if (this.isPreviousIndex(index)) {
            this.previous();
        } else if (this.index != index) {
            throw "Cannot switch to photo other than strict previous or following photo.";
        }
        this._loadComments();
    };

    this.getNextIndex = function(index) {
        if (typeof index == "undefined") {
            index = this.index;
        }
        return index == this.items.length - 1 ? 0 : index + 1
    };

    this.getPreviousIndex = function(index) {
        if (typeof index == "undefined") {
            index = this.index;
        }
        return index > 0 ? index - 1 : this.items.length - 1;
    };

    this.isNextIndex = function(index) {
        return (this.index == this.items.length - 1 && index == 0) || (this.index != this.items.length - 1 && index == this.index + 1);
    };

    this.isPreviousIndex = function(index) {
        return (this.index != 0 && index == this.index - 1) || (this.index == 0 && index == this.items.length - 1);
    };

    this.getItemIndex = function(itemId) {
        for (var i = 0; i < this.items.length; i++) {
            if (this.items[i].id == itemId) {
                return i;
            }
        }
        throw "Unable to find index for item " + itemId;
    };

}
