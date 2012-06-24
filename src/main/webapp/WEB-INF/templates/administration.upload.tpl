<h2 class="page-header">{{i18n.administration.upload.title}}</h2>
<h3>{{i18n.administration.upload.album}}</h3>
<div class="alert alert-danger mandatory hide"><span class="label label-important">{{i18n.common.mandatory}}</span> {{i18n.administration.upload.mandatory}}</div>

<div class="accordion" id="accordion">
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle existingAlbum" data-toggle="collapse" data-parent="#accordion" href="#existingAlbum">
                {{i18n.administration.upload.existingAlbum}}
            </a>
        </div>
        <div id="existingAlbum" class="accordion-body collapse in">
            <div class="accordion-inner">
                {{#albums.length}}
                <div class="dynatree albumId"></div>
                {{/albums.length}}
                {{^albums.length}}
                <div class="alert" style="margin-bottom: 0;">Aucun album n'a été créé pour le moment.</div>
                {{/albums.length}}
            </div>
        </div>
    </div>
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle createNewAlbum" data-toggle="collapse" data-parent="#accordion" href="#createNewAlbum">
                {{i18n.administration.upload.newAlbum}}
            </a>
        </div>
        <form id="createNewAlbum" class="accordion-body collapse form-horizontal" action="" method="post">
            <div class="accordion-inner">
                <div id="creationError" class="alert alert-danger hide"><span class="label label-important">{{i18n.common.error}}</span> Une erreur est survenue pendant la création de l'album.</div>
                <div class="control-group">
                    <label class="control-label" for="albumName">Nom de l'album :</label>
                    <div class="controls">
                        <input type="text" class="input-xlarge" id="albumName" name="albumName" required placeholder="Veuillez entrer le nom du nouvel album">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="noParent">Sous-album :</label>
                    <div class="controls">
                        <label class="radio">
                            <input id="noParent" type="radio" name="parent" value="false" checked>
                            L'album ne sera pas un sous-album, il sera accessible directement dans la liste des albums.
                        </label>
                        <label class="radio">
                            <input type="radio" name="parent" value="true">
                            L'album sera un sous-album, choisissez l'album qui contiendra ce sous-album.
                        </label>
                        <div id="parentMandatory" class="alert alert-danger hide"><span class="label label-important">{{i18n.common.mandatory}}</span> Pour que l'album créé soit un sous-album, vous devez spécifier l'album qui contiendra ce sous-album.</div>
                        <div class="dynatree parentId hide"></div>
                        <input type="hidden" name="parentId" id="parentId" />
                    </div>
                </div>
            </div>
            <div class="form-actions">
                <button class="btn btn-primary">Créer l'album</button>
            </div>
        </form>
    </div>
</div>

<form id="fileupload" action="administration/upload" method="POST" enctype="multipart/form-data" class="form-horizontal">
    <h3>{{i18n.administration.upload.photos}}</h3>
    <input type="hidden" name="albumId" id="albumId" />

    <p id="targetAlbum" class="hide alert alert-info">{{i18n.administration.upload.targetDirectory}}<strong></strong></p>
    <!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->
    <div class="row fileupload-buttonbar">
        <div class="span7">
            <!-- The fileinput-button span is used to style the file input field as button -->
            <span class="btn btn-success fileinput-button">
                <i class="icon-plus icon-white"></i>
                <span>{{i18n.administration.upload.add}}</span>
                <input type="file" name="photo" multiple>
            </span>
            <button type="submit" class="btn btn-primary start">
                <i class="icon-upload icon-white"></i>
                <span>{{i18n.administration.upload.action}}</span>
            </button>
            <button type="reset" class="btn btn-warning cancel">
                <i class="icon-ban-circle icon-white"></i>
                <span>{{i18n.administration.upload.cancel}}</span>
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
