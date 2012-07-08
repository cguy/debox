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
app.post('#/administration/configuration', function() {
    var data = $("#configuration form").serializeArray();
    var force = false;
    for (var i = 0 ; i < data.length ; i++) {
        if (data[i].name == "force" && data[i].value == "true") {
            force = true;
            delete data[i];
        }
    }

    var targetBtn;
    if (force) {
        targetBtn = $("#configuration button");
    } else {
        targetBtn = $("#configuration input[type=submit]");
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
                "title" : data.title
            }, ".navbar .container-fluid", headerTemplateLoaded);

            if (force) {
                $("#synchronization form input[type=checkbox]").attr("checked", "checked");
                $("#synchronization form").submit();
            }
            $("#configuration p").text("Configuration enregistrée avec succès !");
            $("#configuration p").addClass("alert-success");
            $("#configuration p").removeClass("hide");
        },
        error: function() {
            $("#configuration p").text("Erreur durant l'enregistrement de la configuration.");
            $("#configuration p").addClass("alert-danger");
            $("#configuration p").removeClass("hide");
            targetBtn.button("reset");
        }
    });
    return false;
});
