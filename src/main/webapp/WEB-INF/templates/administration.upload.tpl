<form class="form-vertical" action="#/administration/upload" method="post">
    <h2 class="page-header">{{i18n.administration.upload.title}}</h2>
    <p class="alert hide"></p>
    <div class="control-group">
        <label for="title">{{i18n.administration.config.galery_title}}</label>
        <div class="controls">
            <input class="span5" type="text" required id="title" name="title" placeholder="{{i18n.administration.config.galery_title_placeholder}}" value="{{title}}" />
        </div>
    </div>
    <div class="control-group">
        <label for="sourceDirectory">{{i18n.administration.config.source_directory}}:</label>
        <div class="controls">
            <input class="span5" type="text" required id="sourceDirectory" name="sourceDirectory" placeholder="{{i18n.administration.config.source_directory_placeholder}}" value="{{source_path}}" />
        </div>
    </div>
    <div class="control-group">
        <label for="targetDirectory">{{i18n.administration.config.target_directory}}:</label>
        <div class="controls">
            <input class="span5" type="text" required id="targetDirectory" name="targetDirectory" placeholder="{{i18n.administration.config.target_directory_placeholder}}" value="{{target_path}}" />
        </div>
    </div>
    <div class="form-actions">
        <input type="hidden" name="force" />
        <button type="button" class="btn btn-danger" data-loading-text="{{i18n.administration.processing}}">{{i18n.administration.config.save_and_sync}}</button>
        <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.administration.processing}}" value="{{i18n.administration.config.save}}" />
    </div>
</form>
<form id="fileupload" action="administration/upload" method="POST" enctype="multipart/form-data">
    <!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->
    <div class="row fileupload-buttonbar">
        <div class="span7">
            <!-- The fileinput-button span is used to style the file input field as button -->
            <span class="btn btn-success fileinput-button">
                <i class="icon-plus icon-white"></i>
                <span>Add files...</span>
                <input type="file" name="test" multiple>
            </span>
            <button type="submit" class="btn btn-primary start">
                <i class="icon-upload icon-white"></i>
                <span>Start upload</span>
            </button>
            <button type="reset" class="btn btn-warning cancel">
                <i class="icon-ban-circle icon-white"></i>
                <span>Cancel upload</span>
            </button>
            <button type="button" class="btn btn-danger delete">
                <i class="icon-trash icon-white"></i>
                <span>Delete</span>
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
