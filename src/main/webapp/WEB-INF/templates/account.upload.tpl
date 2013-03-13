<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head">
        <link rel="stylesheet" th:href="@{/static/css/jquery.fileupload-ui.css}" />
        <link rel="stylesheet" th:href="@{/static/js/lib/skin-vista/ui.dynatree.css}" />
    </head>
    <body>
        <div th:fragment="scripts">
        </div>
        <div th:fragment="body">
            <h2 class="subtitle" th:text="#{account.upload.title}"></h2>
            <div class="block">
                <h3 th:text="#{account.upload.album}"></h3>
                <div class="alert alert-danger mandatory hide"><span class="label label-important" th:text="#{common.mandatory}" th:inline="text"></span> [[#{account.upload.mandatory}]]</div>

                <div th:if="${#lists.isEmpty(albums)}" class="alert noAlbums" style="margin-bottom: 0;" th:text="#{common.no_album}"></div>
                <div th:class="'dynatree albumId ' + ${#lists.isEmpty(albums)} ? 'hide'"></div>

                <a class="btn btn-primary" href="#modal-createNewAlbum" data-toggle="modal" th:text="#{account.upload.createAlbum}"></a>

                <form id="fileupload" action="administration/upload" method="POST" enctype="multipart/form-data" class="form-horizontal">
                    <h3 th:text="#{account.upload.photos}"></h3>
                    <input type="hidden" name="albumId" id="albumId" />

                    <p id="targetAlbum" class="hide alert alert-info" th:inline="text">[[#{account.upload.targetDirectory}]]<a href="" data:href="@{'/albums/'}"></a></p>
                    <!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->
                    <div class="row fileupload-buttonbar">
                        <div class="span8">
                            <!-- The fileinput-button span is used to style the file input field as button -->
                            <span class="btn btn-success fileinput-button">
                                <i class="icon-plus icon-white"></i>
                                <span th:text="#{account.upload.add}"></span>
                                <input type="file" name="photo" multiple="multiple" />
                            </span>
                            <button type="submit" class="btn btn-primary start">
                                <i class="icon-upload icon-white"></i>
                                <span th:text="#{account.upload.action}"></span>
                            </button>
                            <button type="reset" class="btn btn-warning cancel">
                                <i class="icon-ban-circle icon-white"></i>
                                <span th:text="#{common.cancel}"></span>
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
                    <br />
                    <!-- The table listing the files available for upload/download -->
                    <table role="presentation" class="table table-striped"><tbody class="files" data-toggle="modal-gallery" data-target="#modal-gallery"></tbody></table>
                </form>
            </div>

            <form id="modal-createNewAlbum" class="modal hide fade form-horizontal" th:action="@{'/albums'}" method="put">
                <div class="modal-header">
                    <a class="close" data-dismiss="modal">&times;</a>
                    <h3 th:text="#{account.upload.form.title}"></h3>
                </div>
                <div class="modal-body">
                    <div id="creationError" class="alert alert-danger hide" th:inline="text">
                        <span class="label label-important" th:text="#{common.error}"></span> [[#{account.upload.errors.albumCreation}]]
                    </div>
                    <div class="control-group">
                        <label class="control-label" for="albumName" th:text="#{account.upload.form.albumName}"></label>
                        <div class="controls">
                            <input type="text" class="input-xlarge" id="albumName" name="albumName" required="required" th:placeholder="#{account.upload.form.namePlaceHolder}" />
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label" th:text="#{account.upload.form.subAlbum}"></label>
                        <div class="controls">
                            <div class="dynatree parentId"></div>
                            <input type="hidden" name="parentId" id="parentId" />
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <input type="submit" class="btn btn-primary" data:loading-text="#{common.creation_in_progress}" th:value="#{common.validate}" />
                    <button class="btn" data-dismiss="modal" th:text="#{common.cancel}"></button>
                </div>
            </form>
        </div>
    </body>
</html>
