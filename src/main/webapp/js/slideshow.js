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
            
function exitFullscreen() {
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
    container.style.height = $.getDocHeight() + "px";
                
    var elt = document.createElement("div");
    elt.id = "slideshow-div";
    elt.className = "rs-slideshow";
    elt.style.position = "fixed";
    elt.style.top = "0px";
    elt.style.left = "0px";
    elt.style.height = Math.round(window.innerHeight) + "px";
    elt.onclick = exitFullscreen;

    container.appendChild(elt);
    return container;
}
            
function fullscreen(index, data) {
    var elt = createBg();
    document.body.appendChild(elt);
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
    $('#slideshow-div').rsfSlideshow(
        'goToSlide', index
    );
    
    jwerty.key('←', function () {
        if (document.getElementById("slideshow-div") != null) {
            $('#slideshow-div').rsfSlideshow('previousSlide');
        }
    });
    
    jwerty.key('→', function () {
        if (document.getElementById("slideshow-div") != null) {
            $('#slideshow-div').rsfSlideshow('nextSlide');
        }
    });
    
    jwerty.key('esc', function () {
        if (document.getElementById("slideshow-div") != null) {
            exitFullscreen();
        }
    });
        
}
