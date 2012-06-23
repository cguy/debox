<form id="fileupload" action="administration/upload" method="POST" enctype="multipart/form-data">
    <h2 class="page-header">{{i18n.administration.upload.title}}</h2>

    <h3>{{i18n.administration.upload.album}}</h3>
    <div class="alert alert-danger mandatory hide"><span class="label label-important">{{i18n.common.mandatory}}</span> {{i18n.administration.upload.mandatory}}</div>
    <div class="dynatree"></div>
    <input type="hidden" name="albumId" id="albumId" />
    
    <h3>{{i18n.administration.upload.photos}}</h3>
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
