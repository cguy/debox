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
            $("#thirdparty-configuration p").text("Configuration enregistrée avec succès !").addClass("alert-success").removeClass("hide");
            $("#thirdparty-configuration input[type=submit]").button("reset");
        },
        error: function() {
            $("#thirdparty-configuration p").text("Erreur durant l'enregistrement de la configuration.").addClass("alert-danger").removeClass("hide");
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
                "username" : $("#menu .username strong").html(), 
                "title" : data.title,
                "authenticated" : _config.authenticated,
                "administrator" : _config.administrator
            }, "#menu");

            if (force) {
                $("#synchronization input[type=checkbox]").attr("checked", "checked");
                _("synchronization").submit();
            }
            $("#overall-configuration p").text("Configuration enregistrée avec succès !").addClass("alert-success").removeClass("hide");
        },
        error: function() {
            $("#overall-configuration p").text("Erreur durant l'enregistrement de la configuration.").addClass("alert-danger").removeClass("hide");
            targetBtn.button("reset");
        }
    });
    return false;
});
