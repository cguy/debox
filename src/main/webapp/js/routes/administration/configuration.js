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
app.post('#/administration/configuration/social', function() {
    var data = $("#thirdparty-configuration").serializeArray();
    ajax({
        url: "configuration/thirdparty",
        type : "post",
        data : data,
        success: function() {
            $("#thirdparty-configuration p").text("Configuration enregistrée avec succès !");
            $("#thirdparty-configuration p").addClass("alert-success");
            $("#thirdparty-configuration p").removeClass("hide");
            $("#thirdparty-configuration input[type=submit]").button("reset");
        },
        error: function() {
            $("#thirdparty-configuration p").text("Erreur durant l'enregistrement de la configuration.");
            $("#thirdparty-configuration p").addClass("alert-danger");
            $("#thirdparty-configuration p").removeClass("hide");
            $("#thirdparty-configuration input[type=submit]").button("reset");
        }
    });
});

app.post('#/administration/configuration', function() {
    var data = $("#overall-configuration").serializeArray();
    var force = false;
    for (var i = 0 ; i < data.length ; i++) {
        if (data[i].name == "force" && data[i].value == "true") {
            force = true;
            delete data[i];
        }
    }

    var targetBtn;
    if (force) {
        targetBtn = $("#overall-configuration button");
    } else {
        targetBtn = $("#overall-configuration input[type=submit]");
    }
    targetBtn.button("loading");

    $.ajax({
        url: "configuration",
        type : "post",
        data : data,
        success: function(data) {
            targetBtn.button("reset");
            loadTemplate("header", {
                "username" : $(".navbar-text.pull-right strong").html(), 
                "title" : data.title,
                "authenticated" : _config.authenticated,
                "administrator" : _config.administrator
            }, ".navbar .container-fluid", headerTemplateLoaded);

            if (force) {
                $("#synchronization input[type=checkbox]").attr("checked", "checked");
                $("#synchronization").submit();
            }
            $("#overall-configuration p").text("Configuration enregistrée avec succès !");
            $("#overall-configuration p").addClass("alert-success");
            $("#overall-configuration p").removeClass("hide");
        },
        error: function() {
            $("#overall-configuration p").text("Erreur durant l'enregistrement de la configuration.");
            $("#overall-configuration p").addClass("alert-danger");
            $("#overall-configuration p").removeClass("hide");
            targetBtn.button("reset");
        }
    });
    return false;
});
