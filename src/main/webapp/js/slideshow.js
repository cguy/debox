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

function fullscreen(index, data, mode) {
    s = new Slideshow();
    s.setItems(data);
    s.setIndex(index);
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

        var prevIndex = this.getPreviousIndex();
        var nextIndex = this.getNextIndex();

        this.getMedias()[prevIndex].className = "previous";
        this.getMedias()[this.index].className = "";
        this.getMedias()[nextIndex].className = "next";

        for (var i = 0; i < prevIndex && prevIndex != this.getMedias().length - 1; i++) {
            this.getMedias()[i].className = "undisplayed previous";
        }
        for (i = nextIndex + 1; i < this.getMedias().length - 1; i++) {
            this.getMedias()[i].className = "undisplayed next";
        }
        $("#slideshow-label").text(this.items[this.index].name);
        this.refreshLinks();
    };
    
    this.getAlbumId = function() {
        var hash = location.hash;
        var prefix = "#/album/";
        var prefixIndex = hash.indexOf(prefix) + prefix.length;
        var albumId = hash.substring(prefixIndex, hash.indexOf("/", prefixIndex));
        return albumId;
    };
    
    this.getBasePath = function() {
        return "#/album/" + this.getAlbumId() + "/" + this.items[this.index].id; 
    };

    this.refreshLinks = function() {
        var hash = location.hash;
        $("#slideshow-previous").attr("href", hash.replace(this.getCurrentId(), this.getId(this.getPreviousIndex())));
        $("#slideshow-next").attr("href", hash.replace(this.getCurrentId(), this.getId(this.getNextIndex())));
        
        var isCommentsMode = /\/comments$/.test(hash);
        var path = this.getBasePath();
        path += isCommentsMode ? "" : "/comments";
        $("#slideshow-options .comments").attr("href", path);
        $("#new-photo-comment").attr("action", location.hash);
        
        var commentsText = fr.comments.show;
        if (isCommentsMode) {
            commentsText = fr.comments.hide;
        }
        $("#slideshow-options > a.comments").attr("title", commentsText);
        $("#slideshow-help-label").text(commentsText);
        $("#slideshow-options > a.comments").unbind("mouseenter");
        $("#slideshow-options > a.comments").unbind("mouseout");
        $("#slideshow-options *").mouseenter(function() {
            var title = $(this).attr("title");
            if (!title) {
                title = $(this).parents("a").attr("title");
            }
            $("#slideshow-options").addClass("show");
            $("#slideshow-help-label").text(title);
        });
        $("#slideshow-options").mouseout(function() {
            $("#slideshow-options").removeClass("show");
        });
    };

    this.getCurrentId = function() {
        return this.items[this.index].id;
    };

    this.getId = function(index) {
        return this.items[index].id;
    };
    
    this.setLabel = function() {
        $("#slideshow-label").text(this.items[this.index].name);
        $("#slideshow-label").get(0).className = "";
    };

    this.setItems = function(items) {
        this.items = this.convert(items);
        
        var html = templates["slideshow"].render({data:this.items, i18n: fr, config: _config}, templates);
        $(document.body).append(html);
        
        var self = this;
        addTransitionListener($("#slideshow-label").get(0), function() {self.setLabel();});
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
        href = href.substring(0, href.indexOf("/", href.indexOf("#/album/") + "#/album/".length));
        $("#slideshow-options .exit").attr("href", href);
        $("#slideshow-options .exit").click(function() {
            exitFullscreen();
        });
        $("#slideshow-comments").mCustomScrollbar({
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
        $("#fullscreenContainer").addClass("comments");
        this.refreshLinks();
        this._loadComments();
    };
    
    this._loadComments = function() {
        if (!_config.authenticated) {
            return;
        }
        var id = this.items[this.index].id;
        ajax({
            url: computeUrl("photo/" + id + "/comments"),
            success: function(data) {
                if (data.photoId != id) {
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
                    $("#slideshow-options .comments .badge").addClass("hide");
                } else {
                    $("#slideshow-comments .no-comments").addClass("hide");
                    $("#slideshow-comments").mCustomScrollbar("update");
                    $("#slideshow-options .comments .badge").removeClass("hide");
                    $("#slideshow-options .comments .badge").text(data.comments.length);
                }
                bindPhotoCommentDeletion();
            },
            error: function() {
                console.log("Error during deletion");
            }
        });
    };
    
    this._displayDrawer = function() {
        $("#fullscreenContainer").addClass("drawer");
        $("#slideshow-drawer").removeClass("hide");
        this._resetMargin();
    };
    
    this._hideDrawer = function() {
        $("#slideshow-drawer").addClass("hide");
        $("#fullscreenContainer").removeClass("drawer");
        this.getMedias()[this.index].style.right = "34%";
        this.getMedias()[this.index].style.maxWidth = "90%";
        this.refreshLinks();
    };
    
    this._resetMargin = function() {
        var prevIndex = this.getPreviousIndex();
        var nextIndex = this.getNextIndex();
        
        this.getMedias()[this.index].style.right = (this.getMediasNode().clientWidth * .34 + this.DRAWER_MARGIN)+"px";
        this.getMedias()[this.index].style.maxWidth = (this.getMediasNode().clientWidth / 3 - this.DRAWER_MARGIN) * .8+"px";
        this.getMedias()[prevIndex].style.right = null;
        this.getMedias()[nextIndex].style.right = null;
    };

    this.hide = function() {
        document.body.removeChild($("#fullscreenContainer"));
    };
    
    this.getMediasNode = function() {
        return $("#fullscreenContainer_photos").get(0);
    };
    
    this.getMedias = function() {
        return $("#fullscreenContainer_photos > *");
    };

    this.previous = function() {
        var prevIndex = this.getPreviousIndex();
        var nextIndex = this.getNextIndex();
        var newPreviousIndex = this.getPreviousIndex(prevIndex);

        var currentMedia = this.getMedias()[this.index];
        var previousMedia = this.getMedias()[prevIndex];
        var nextMedia = this.getMedias()[nextIndex];
        var newPreviousMedia = this.getMedias()[newPreviousIndex];

        nextMedia.className = "next undisplayed";
        currentMedia.className = "next";
        previousMedia.className = "";
        newPreviousMedia.className = "previous";
        
        if (!Modernizr.csstransitions) {
            this.setLabel();
        } else {
            $("#slideshow-label").get(0).className = "hide";
        }
        
        this.index = prevIndex;
        this._resetMargin();
        this.refreshLinks();
    };

    this.next = function() {
        var prevIndex = this.getPreviousIndex();
        var nextIndex = this.getNextIndex();
        var newNextIndex = this.getNextIndex(nextIndex);

        var currentMedia = this.getMedias()[this.index];
        var previousMedia = this.getMedias()[prevIndex];
        var nextMedia = this.getMedias()[nextIndex];
        var newNextMedia = this.getMedias()[newNextIndex];
        
        previousMedia.className = "previous undisplayed";
        currentMedia.className = "previous";
        nextMedia.className = "";
        newNextMedia.className = "next";
        
        if (!Modernizr.csstransitions) {
            this.setLabel();
        } else {
            $("#slideshow-label").get(0).className = "hide";
        }

        this.index = nextIndex;
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
        return index == this.getMedias().length - 1 ? 0 : index + 1
    };

    this.getPreviousIndex = function(index) {
        if (typeof index == "undefined") {
            index = this.index;
        }
        return index != 0 ? index - 1 : this.getMedias().length - 1;
    };

    this.isNextIndex = function(index) {
        return (this.index == this.getMedias().length - 1 && index == 0) || (this.index != this.getMedias().length - 1 && index == this.index + 1);
    };

    this.isPreviousIndex = function(index) {
        return (this.index != 0 && index == this.index - 1) || (this.index == 0 && index == this.getMedias().length - 1);
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
