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
/* ***************************** */
/* Administration tab navigation */
/* ***************************** */
app.get('#/accounts/:accountId/delete', function() {
    if ($("#delete-account-confirm").length) {
        $("#delete-account-confirm").modal();
    } else {
        ajax({
            url: "account",
            success: function(data) {
                loadTab("personaldata", data, "account", function() {
                    $("#delete-account-confirm").modal();
                });
            }
        });
    }
});

/* ***************************** */
/* Administration tab navigation */
/* ***************************** */
app.get('#/account(/:tab)?', function() {
    var tab = this.params["tab"];
    if (tab) {
        tab = tab.substr(1);
        var route = "accounts/" + _config.userId + "/" + tab;
        if (tab == "upload") {
            route = "albums";
        } else if (tab == "albums") {
            route = "albums?criteria=all";
        }
    } else {
        route = "account", tab = "personaldata";
    }
    ajax({
        url: route,
        success: function(data) {
            loadTab(tab, data, "account");
        }
    });
});
