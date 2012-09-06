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
/* ******************* */
/* Register            */
/* ******************* */
app.get("#/register", function() {
    loadTemplate("register");
});

/* ******************* */
/* Authentication      */
/* ******************* */
app.post('#/authenticate', function() {
    $("#login input[type=submit]").button('loading');
    $("#login p").html("").removeClass("alert alert-error");
    var context = this;

    var data = {
        "username" : this.params["username"], 
        "password" : this.params["password"]
    };
    ajax({
        url: "authenticate",
        type : "post",
        data : data,
        success: function(username) {
            $("#connect").button('reset');
            loadTemplates(function() {
                if (location.hash && location.hash.length > 1) {
                    context.redirect(location.hash);
                } else {
                    context.redirect("#/");
                }
                loadTemplate("header", {
                    "username" : username,
                    "title" : $("a.brand").html()
                }, ".navbar .container-fluid", headerTemplateLoaded);
            });
        },
        error: function(xhr) {
            $("#connect").button('reset');
            $("#login p").addClass("alert alert-error");
            if (xhr.status == 401) {
                $("#login p").html("Erreur de connexion: veuillez vérifier vos identifiants de connexion.");
            } else {
                $("#login p").html("Erreur pendant la connexion, veuillez réessayer ultérieurement.");
            }
        }
    });
    return false;
});

/* ***************************** */
/* Administration tab navigation */
/* ***************************** */
app.get('#/administration(/:tab)?', function() {
    var tab = this.params["tab"];
    if (tab) {
        tab = tab.substr(1);
        if (tab == "synchronization") {
            loadAdministrationTab(tab);
        } else {
            var route = tab;
            if (tab == "upload") {
                route = "albums";
            } else if (tab == "albums") {
                route = "albums?criteria=all";
            }
            ajax({
                url: route,
                success: function(data) {
                    loadAdministrationTab(tab, data);
                }
            });
        }
    } else {
        this.redirect("#/administration/configuration");
    }
});

app.get('#/administration(/:tab)?', function() {
    return false;
    editTitle($("a.brand").text() + " - Administration");
    var tabId = this.params['tab'];
    if ($("#administration").length > 0) {
        $(".nav-tabs a[data-target|=\"#" + tabId.substr(1) + "\"]").tab("show");
        $("form .alert-error, form .alert-success").addClass("hide");
        return false;
    }

    ajax({
        url: "administration",
        success: function(data) {
            allAlbums = data.albums;

            loadTemplate("administration", data, null, function() {
                handleAdmin();
                manageSync(data);

                if (tabId) {
                    $(".nav-tabs a[data-target|=\"#" + tabId.substr(1) + "\"]").tab("show");
                } else {
                    $(".nav-tabs a[data-target|=\"#configuration\"]").tab("show");
                }

                // Generates trees for tokens management
                var tokens = data.tokens;
                for (var tokenIndex = 0 ; tokenIndex < tokens.length ; tokenIndex++) {
                    var treeChildren = [];
                    prepareDynatree(data.albums, tokens[tokenIndex].albums, treeChildren, null);
                    initDynatree(tokens[tokenIndex].id, treeChildren);
                }

            }); // End loading template
        }
    }); // End ajax call
});
