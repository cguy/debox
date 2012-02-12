var templates = {};
            
function loadTemplate(tplId, data, selector, callback) {
    function render(strTemplate, data, selector, callback) {
        if (data) {
            data.baseUrl = baseUrl;
        }
        var html = Mustache.render(strTemplate, {
            data : data
        });
        $(selector).html(html);
        if (callback) {
            callback();
        }
    }
                
    if (!selector) {
        selector = ".container .content";
    }
                
    if (!templates[tplId]) {
        $.ajax({
            url: "/templates/" + tplId + ".tpl?t="+new Date().getTime(),
            success: function(tpl) {
                templates[tplId] = tpl;
                render(templates[tplId], data, selector, callback);
            }
        });
    } else {
        render(templates[tplId], data, selector, callback);
    }
}
    
function ajax(object) {
    if (!object.error) {
        object.error = function(xhr) {
            var status = xhr.status;
            if (status == 404 || status == 403) {
                loadTemplate(status);
            }
        }
    }
    $.ajax(object);
}
    
function computeUrl(url) {
    if (location.pathname != "/") {
        var token = location.pathname.substring(1, location.pathname.length - 1);
        var separator = url.indexOf("?") == -1 ? "?" : "&";
        return url + separator + "token=" + token;
    }
    return url;
}

function editTitle(title) {
    document.title = title;
}

function initHeader(title, username) {
    editTitle(title + " - Accueil");
    loadTemplate("header", {"title": title, "username" : username}, ".navbar .container");
}
