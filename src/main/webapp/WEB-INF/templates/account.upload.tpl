<div class="page-header"><h1>{{i18n.account.upload.title}}</h1></div>

<div class="block">
    <h3>{{i18n.account.upload.album}}</h3>
    <div class="alert alert-danger mandatory hide"><span class="label label-important">{{i18n.common.mandatory}}</span> {{i18n.account.upload.mandatory}}</div>

    {{^albums.length}}
    <div class="alert noAlbums" style="margin-bottom: 0;">{{i18n.common.no_album}}</div>
    {{/albums.length}}
    <div class="dynatree albumId {{^albums.length}}hide{{/albums.length}}"></div>

    <a class="btn btn-primary" href="#modal-createNewAlbum" data-toggle="modal">{{i18n.account.upload.createAlbum}}</a>

    <form id="fileupload" action="administration/upload" method="POST" enctype="multipart/form-data" class="form-horizontal">
        <h3>{{i18n.account.upload.photos}}</h3>
        <input type="hidden" name="albumId" id="albumId" />

        <p id="targetAlbum" class="hide alert alert-info">{{i18n.account.upload.targetDirectory}}<a href="" data-href="#/albums/"></a></p>
        <!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->
        <div class="row fileupload-buttonbar">
            <div class="span8">
                <!-- The fileinput-button span is used to style the file input field as button -->
                <span class="btn btn-success fileinput-button">
                    <i class="icon-plus icon-white"></i>
                    <span>{{i18n.account.upload.add}}</span>
                    <input type="file" name="photo" multiple>
                </span>
                <button type="submit" class="btn btn-primary start">
                    <i class="icon-upload icon-white"></i>
                    <span>{{i18n.account.upload.action}}</span>
                </button>
                <button type="reset" class="btn btn-warning cancel">
                    <i class="icon-ban-circle icon-white"></i>
                    <span>{{i18n.common.cancel}}</span>
                </button>
            </div>
            <!-- The global progress information -->
            <div class="span5 fileupload-progress fade">
                <!-- The global progress bar -->
                <div class="progress progress-success progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100">
                    <div class="bar" style="width:0%;"></div>
                </div>
                <!-- The extended global progress information -->
                <div class="progress-extended">&nbsp;</div>
            </div>
        </div>
        <!-- The loading indicator is shown during file processing -->
        <div class="fileupload-loading"></div>
        <br>
        <!-- The table listing the files available for upload/download -->
        <table role="presentation" class="table table-striped"><tbody class="files" data-toggle="modal-gallery" data-target="#modal-gallery"></tbody></table>
    </form>
</div>

{{! ========================================================== }}
{{! POPUP MODAL - ALBUM CREATION                               }}
{{! ========================================================== }}
<form id="modal-createNewAlbum" class="modal hide fade form-horizontal" action="#/albums" method="put">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>{{i18n.account.upload.form.title}}</h3>
    </div>
    <div class="modal-body">
        <div id="creationError" class="alert alert-danger hide">
            <span class="label label-important">{{i18n.common.error}}</span> {{i18n.account.upload.errors.albumCreation}}
        </div>
        <div class="control-group">
            <label class="control-label" for="albumName">{{i18n.account.upload.form.albumName}}</label>
            <div class="controls">
                <input type="text" class="input-xlarge" id="albumName" name="albumName" required placeholder="{{i18n.account.upload.form.namePlaceHolder}}">
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">{{i18n.account.upload.form.subAlbum}}</label>
            <div class="controls">
                <div class="dynatree parentId"></div>
                <input type="hidden" name="parentId" id="parentId" />
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.common.creation_in_progress}}" value="{{i18n.common.validate}}" />
        <button class="btn" data-dismiss="modal">{{i18n.common.cancel}}</button>
    </div>
</form>