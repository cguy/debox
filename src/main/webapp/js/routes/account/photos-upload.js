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
app.put('#/albums', function() {
    // Note : album name is mandatory, but handled by HTML5 required attribute (modern browser)
    // Never let default behavior, we handle form submit
    $("#creationError").slideUp(500);

    var parentId = null;
    var formData = $("#modal-createNewAlbum").serializeArray();
    var params = "";
    for (var k in formData) {
        var param = formData[k];
        params += param.name + "=" + param.value + "&";
        if (param.name == "parentId") {
            parentId = param.value;
        }
    }
    var parentNode = $(".dynatree.parentId").dynatree("getTree").getNodeByKey(parentId);
    var parentPath = "";
    parentNode.visitParents(function(node) {
        if (!node.data.key) {
            return false;
        }
        parentPath = "/" + node.data.key + parentPath;
        return true;

    }, true);

    $.ajax({
        url: "album?" + params,
        type: "put",
        success: function(data) {

            var item = {
                title: data.name, 
                key: data.id, 
                isFolder: true,
                activate: true,
                focus: true,
                isLazy : !!data['subAlbumsCount']
            };

            updateAlbumTreeAfterAlbumCreation(parentPath, item);
            updateParentTreeAfterAlbumCreation(parentId, item);

            $("#modal-createNewAlbum #albumName").val("");
            $("#modal-createNewAlbum").modal("hide");
        },
        error: function() {
            $("#creationError").slideDown(500);
        }
    });
    return false;
});
        
