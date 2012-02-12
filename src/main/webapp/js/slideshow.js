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
