/*
 * jQuery File Upload Plugin JS Example 6.7
 * https://github.com/blueimp/jQuery-File-Upload
 *
 * Copyright 2010, Sebastian Tschan
 * https://blueimp.net
 *
 * Licensed under the MIT license:
 * http://www.opensource.org/licenses/MIT
 */

/*jslint nomen: true, unparam: true, regexp: true */
/*global $, window, document */

$(function () {
    'use strict';

    // Initialize the jQuery File Upload widget:
    $('#fileupload').fileupload();

    // Demo settings:
    $('#fileupload').fileupload('option', {
        maxFileSize: 5000000,
        acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
        process: [
        {
            action: 'load',
            fileTypes: /^image\/(gif|jpeg|png)$/,
            maxFileSize: 20000000 // 20MB
        },
        {
            action: 'save'
        }
        ]
    });
    // Upload server status check for browsers with CORS support:
//    if ($.support.cors) {
//        $.ajax({
//            type: 'HEAD'
//        }).fail(function () {
//            $('<span class="alert alert-error"/>')
//            .text('Upload server currently unavailable - ' +
//                new Date())
//            .appendTo('#fileupload');
//        });
//    }

});
