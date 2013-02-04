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
app.get('#/administration(/:tab)?', function() {
    var tab = this.params["tab"];
    if (tab) {
        tab = tab.substr(1);
        ajax({
            url: tab,
            success: function(data) {
                loadTab(tab, data);
            }
        });
    } else {
        this.redirect("#/administration/configuration");
    }
});

/* ******************* */
/* Register            */
/* ******************* */
app.get("#/register", function() {
    if (_config.authenticated) {
        this.redirect("#/account");
    } else {
        loadTemplate("register", {
            alreadyRegistered : typeof this.params.alreadyRegistered !== 'undefined',
            error : typeof this.params.error !== 'undefined',
            success : typeof this.params.success !== 'undefined',
            mandatoryFields : typeof this.params["mandatory.fields"] !== 'undefined',
            passwordMatch : typeof this.params["password.match"] !== 'undefined',
        });
    }
});

app.get("#/sign-in", function() {
    if (_config.authenticated) {
        this.reditect("#/");
    } else {
        loadTemplate("register", {
            isSignIn: true,
            signInError : typeof this.params.error !== 'undefined'
        });
    }
});
