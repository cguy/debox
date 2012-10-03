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
$.getDocHeight = function(){
    return Math.max(
        $(document).height(),
        $(window).height(),
        /* For opera: */
        document.documentElement.clientHeight
        );
};

(function() {
    var fullScreenApi = {
        supportsFullScreen: false,
        isFullScreen: function() {
            return false;
        },
        requestFullScreen: function() {},
        cancelFullScreen: function() {},
        fullScreenEventName: '',
        prefix: ''
    },
    browserPrefixes = 'webkit moz o ms khtml'.split(' ');

    // check for native support
    if (typeof document.cancelFullScreen != 'undefined') {
        fullScreenApi.supportsFullScreen = true;
    } else {
        // check for fullscreen support by vendor prefix
        for (var i = 0, il = browserPrefixes.length; i < il; i++ ) {
            fullScreenApi.prefix = browserPrefixes[i];

            if (typeof document[fullScreenApi.prefix + 'CancelFullScreen' ] != 'undefined' ) {
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
    var elt = document.getElementById("fullscreenContainer");
    document.body.removeChild(elt);
    var controls = document.getElementById("rs-controls-slideshow-div");
    if (controls) {
        document.body.removeChild(controls);
    }
    location.hash = location.hash.substring(0, location.hash.lastIndexOf("/"));
}
            
function fullscreen(index, data) {
    s = new Slideshow();
    s.setItems(data);
    s.setIndex(index);
    s.show();
    return;
    
    var elt = createBg();
    document.body.appendChild(elt);
    $(document.body).addClass("fixed");
    fullScreenApi.requestFullScreen(elt);

    var hammer = new Hammer($("#slideshow-div").get(0));
    hammer.ondragend = function(ev) {
        if(Math.abs(ev.distance) > 10) {
            if(ev.direction == 'right') {
                window.location.hash = "#" + $('#slideshow-div').rsfSlideshow("getPreviousSlideId");
            } else if(ev.direction == 'left') {
                window.location.hash = "#" + $('#slideshow-div').rsfSlideshow("getNextSlideId");
            }
        }
    };
}

jwerty.key('←', function () {
    if (document.getElementById("fullscreenContainer") != null) {
        window.location.hash = window.location.hash.replace(s.getCurrentId(), s.getId(s.getPreviousIndex()));
    }
});

jwerty.key('→', function () {
    if (document.getElementById("fullscreenContainer") != null) {
        window.location.hash = window.location.hash.replace(s.getCurrentId(), s.getId(s.getNextIndex()));
    }
});

jwerty.key('esc', function () {
    if (document.getElementById("fullscreenContainer") != null) {
        exitFullscreen();
    }
});

function SlideshowItem() {
    this.thumbnailUrl = null;
    this.url = null;
    this.comments = [];
    this.date = null;
    this.name = null;
}

function Slideshow() {
    this.containerNode = null;
    this.photosNode = null;
    
    this.items = [];
    this.index = 0;
    this.configuration = {
        "id" : "id",
        "date" : "date",
        "name" : "title",
        "thumbnail" : "thumbnailUrl",
        "url" : "url"
    };
    
    this.init = function() {
        var container = document.createElement("div");
        container.id = "fullscreenContainer";
                
        var elt = document.createElement("div");
        elt.id = "fullscreenContainer_photos";
    
        var close = document.createElement("div");
        close.id = "slideshow-close";
        close.onclick = exitFullscreen;
        $(close).animate({
            opacity: 0.3
        }, 0); /* Initial setup for IE < 9 */
        $(close).hover(
            function () {
                $(this).css("cursor", "pointer");
                $(this).animate({
                    opacity: 1
                });
            },
            function () {
                $(this).css("cursor", "inherit");
                $(this).animate({
                    opacity: 0.3
                });
            }
            );
                
        this.previousNode = document.createElement("a");
        this.previousNode.id = "slideshow-previous";
        $(this.previousNode).append('<i class="icon-chevron-left"></i>');
        
        this.nextNode = document.createElement("a");
        this.nextNode.id = "slideshow-next";
        $(this.nextNode ).append('<i class="icon-chevron-right"></i>');
        
        this.labelNode = document.createElement("div");
        this.labelNode.id = "slideshow-label";
        
        var self = this;
        this.labelNode.addEventListener("webkitTransitionEnd", function() {
            $(this).text(self.items[self.index].name);
            this.className = "";
        });

        container.appendChild(elt);
        container.appendChild(close);
        container.appendChild(this.previousNode);
        container.appendChild(this.nextNode);
        container.appendChild(this.labelNode);
        
        this.containerNode = container;
        this.photosNode = elt;
    }
    this.init()
    
    this.convert = function(old) {
        var c = this.configuration;
        var result = [old.length];
        for (var i = 0 ; i < old.length ; i++) {
            var oldItem = old[i];
            var item = {};
            for (var key in c) {
                item[key] = oldItem[c[key]];
            }
            result[i] = item;
        }
        return result;
    }
    
    this.setIndex = function(index) {
        this.index = index;
        
        var prevIndex = this.getPreviousIndex(); 
        var nextIndex = this.getNextIndex();
        
        this.photosNode.childNodes[prevIndex].className = "previous";
        this.photosNode.childNodes[this.index].className = "";
        this.photosNode.childNodes[nextIndex].className = "next";
        
        for (var i = 0 ; i < prevIndex && prevIndex != this.photosNode.childNodes.length - 1 ; i++) {
            this.photosNode.childNodes[i].className = "undisplayed previous";
        }
        for (i = nextIndex + 1 ; i < this.photosNode.childNodes.length - 1 ; i++) {
            this.photosNode.childNodes[i].className = "undisplayed next";
        }
        $(this.labelNode).text(this.items[this.index].name);
        this.refreshLinks();
    }
    
    this.refreshLinks = function() {
        var hash = window.location.hash;
        $(this.previousNode).attr("href", hash.replace(this.getCurrentId(), this.getId(this.getPreviousIndex())));
        $(this.nextNode).attr("href", hash.replace(this.getCurrentId(), this.getId(this.getNextIndex())));
    }
    
    this.getCurrentId = function() {
        return this.items[this.index].id;
    }
    
    this.getId = function(index) {
        return this.items[index].id;
    }
    
    this.setItems = function(items) {
        this.items = this.convert(items);
        for (var i = 0 ; i < this.items.length ; i++) {
            var item = this.items[i];
            var img = document.createElement("img");
            img.id = item.id;
            img.className = "undisplayed";
            img.src = item.url;
            this.photosNode.appendChild(img);
        }
    }
    
    this.setSize = function(id, w, h) {
        var index = this.getItemIndex(id);
        this.items[index].width = w;
        this.items[index].height = h;
    }
    
    this.show = function() {
        document.body.appendChild(this.containerNode);
        $(document.body).addClass("fixed");
//        fullScreenApi.requestFullScreen(this.containerNode);
    }
    
    this.hide = function() {
        document.body.removeChild(this.containerNode);
    }
    
    this.previous = function() {
        var prevIndex = this.getPreviousIndex(); 
        var nextIndex = this.getNextIndex();
        var newPreviousIndex = this.getPreviousIndex(prevIndex);
        
        var currentPhoto = this.photosNode.childNodes[this.index];
        var previousPhoto = this.photosNode.childNodes[prevIndex];
        var nextPhoto = this.photosNode.childNodes[nextIndex];
        var newPreviousPhoto = this.photosNode.childNodes[newPreviousIndex];
        
        nextPhoto.className = "next undisplayed";
        currentPhoto.className = "next";
        previousPhoto.className = ""
        newPreviousPhoto.className = "previous";
        this.labelNode.className = "hide";
        
        this.index = prevIndex;
        this.refreshLinks();
    }
    
    this.next = function() {
        var prevIndex = this.getPreviousIndex(); 
        var nextIndex = this.getNextIndex();
        var newNextIndex = this.getNextIndex(nextIndex);
        
        var currentPhoto = this.photosNode.childNodes[this.index];
        var previousPhoto = this.photosNode.childNodes[prevIndex];
        var nextPhoto = this.photosNode.childNodes[nextIndex];
        var newNextPhoto = this.photosNode.childNodes[newNextIndex];
        
        previousPhoto.className = "previous undisplayed";
        currentPhoto.className = "previous";
        nextPhoto.className = ""
        newNextPhoto.className = "next";
        this.labelNode.className = "hide";
        
        this.index = nextIndex;
        this.refreshLinks();
    }
    
    this.gotoItem = function(itemId) {
        var index = this.getItemIndex(itemId);
        if (this.isNextIndex(index)) {
            this.next();
        } else if (this.isPreviousIndex(index)) {
            this.previous();
        } else {
            throw "Cannot switch to photo other than strict previous or following photo.";
        }
    }
    
    this.getNextIndex = function(index) {
        if (typeof index == "undefined") {
            index = this.index;
        }
        return index == this.photosNode.childNodes.length - 1 ? 0 : index + 1
    }
    
    this.getPreviousIndex = function(index) {
        if (typeof index == "undefined") {
            index = this.index;
        }
        return index != 0 ? index-1 : this.photosNode.childNodes.length - 1
    }
    
    this.isNextIndex = function(index) {
        return (this.index == this.photosNode.childNodes.length - 1 && index == 0)  || (this.index != this.photosNode.childNodes.length - 1 && index == this.index + 1);
    }
    
    this.isPreviousIndex = function(index) {
        return (this.index != 0 && index == this.index-1) || (this.index == 0 && index == this.photosNode.childNodes.length - 1);
    }
    
    this.getItemIndex = function(itemId) {
        for (var i = 0 ; i < this.items.length ; i++) {
            if (this.items[i].id == itemId) {
                return i;
            }
        }
        throw "Unable to find index for item " + itemId;
    }
    
}
