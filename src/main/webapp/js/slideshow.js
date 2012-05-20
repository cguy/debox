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
            return (this.prefix === '') ? el.requestFullScreen() : el[this.prefix + 'RequestFullScreen']();
        }
        fullScreenApi.cancelFullScreen = function(el) {
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
    $(document.body).removeClass("fixed");
    var elt = document.getElementById("fullscreenContainer");
    document.body.removeChild(elt);
    var controls = document.getElementById("rs-controls-slideshow-div");
    if (controls) {
        document.body.removeChild(controls);
    }
    location.hash = location.hash.substring(0, location.hash.lastIndexOf("/"));
}
            
function createBg() {
    var container = document.createElement("div");
    container.id = "fullscreenContainer";
                
    var elt = document.createElement("div");
    elt.id = "slideshow-div";
    elt.className = "rs-slideshow";
    elt.style.position = "fixed";
    elt.style.top = "0px";
    elt.style.left = "0px";
    elt.style.bottom = "0px";
    
    var close = document.createElement("div");
    close.className = "slideshow-close";
    close.onclick = exitFullscreen;
    $(close).animate({opacity: 0.3}, 0); /* Initial setup for IE < 9 */
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

    container.appendChild(elt);
    container.appendChild(close);
    return container;
}
            
function fullscreen(index, data) {
    var elt = createBg();
    document.body.appendChild(elt);
    $(document.body).addClass("fixed");
    $('#slideshow-div').rsfSlideshow({
        autostart : false,
        transition: 350,
        slides: data,
        controls: {
            previousSlide: {
                auto: true
            },    //    auto-generate a "previous slide" control
            nextSlide: {
                auto: true
            }    //    auto-generate a "next slide" control
        },
        effect: 'fade'
    });

    if (fullScreenApi.supportsFullScreen) {
        fullScreenApi.requestFullScreen(elt);
    }

    $('#slideshow-div').rsfSlideshow(
        'goToSlide', index
    );
}

jwerty.key('←', function () {
    if (document.getElementById("slideshow-div") != null) {
        window.location.hash = "#" + $('#slideshow-div').rsfSlideshow("getPreviousSlideId");
    }
});

jwerty.key('→', function () {
    if (document.getElementById("slideshow-div") != null) {
        window.location.hash = "#" + $('#slideshow-div').rsfSlideshow("getNextSlideId");
    }
});

jwerty.key('esc', function () {
    if (document.getElementById("slideshow-div") != null) {
        exitFullscreen();
    }
});
