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
function loadAlbum(mode) {
    $("#top").click(function() {
        $("html, body").animate({
            scrollTop: 0
        });
        return false;
    })

    $("#top").hover(
            function() {
                $(this).animate({
                    opacity: 0.6
                });
            },
            function() {
                $(this).animate({
                    opacity: 0.3
                });
            }
    );

    $("#album-comments").mCustomScrollbar("destroy");
    $("#album-comments").mCustomScrollbar({
        scrollInertia: 0,
        mouseWheel: true,
        advanced: {
            updateOnContentResize: true
        }
    });

    var oldHref = $(".page-header .comments").attr("href");
    $('.page-header .comments').tooltip('destroy');
    if (mode == "comments") {
        $("#album-content").addClass("comments");
        $(".page-header .comments").addClass("active");
        $(".page-header .comments").attr("href", $(".page-header .comments").attr("data-href") + "#");

    } else {
        if (!/\/comments$/.test(oldHref)) {
            $(".page-header .comments").attr("href", $(".page-header .comments").attr("data-href") + "#comments");
        }
        $("#album-content").removeClass("comments");
        $(".page-header .comments").removeClass("active");
    }

    bindAlbumCommentDeletion();
}

function bindAlbumCommentDeletion() {
    $("#album-comments .comment .remove").unbind("click");
    $("#album-comments .comment .remove").click(function() {
        var commentId = $(this).parents(".comment").attr("id");
        var oldUrl = $("#remove-comment").attr("data-action");
        $("#remove-comment").attr("action", oldUrl.substring(0, oldUrl.lastIndexOf("/") + 1) + commentId);
        $("#remove-comment").modal();
        return false;
    });
}
