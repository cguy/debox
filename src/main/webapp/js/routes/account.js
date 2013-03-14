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
    if (_("delete-account-confirm").length) {
        _("delete-account-confirm").modal();
    } else {
        ajax({
            url: "account",
            success: function(data) {
                loadTab("personaldata", data, "account", function() {
                    _("delete-account-confirm").modal();
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
        if (tab == "synchronization") {
            loadTab(tab, null, "account");
            return;
        } else if (tab == "upload") {
            route = "albums";
        } else if (tab == "albums") {
            route = "albums?criteria=all";
        } else if (tab == "personaldata") {
            route = "account";
        } else if (tab == "comments") {
            route = "comments?mediaOwnerId=" + _config.userId;
        } else if (tab == "dashboard") {
            return loadTab("dashboard", null, "account");
        }
    } else {
//        return loadTab("dashboard", null, "account");
        return this.redirect("#/account/albums");
    }
    ajax({
        url: route,
        success: function(data) {
            loadTab(tab, data, "account");
        }
    });
});
