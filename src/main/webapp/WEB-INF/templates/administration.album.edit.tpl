<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head">
        <link rel="stylesheet" th:href="@{/static/js/lib/datetimepicker/bootstrap-datetimepicker.min.css}" />
    </head>
    <body>
        <div th:fragment="body">
            <h2 class="subtitle" th:text="#{album.admin.edit.modify}"></h2>
            <div class="block">
                <div th:if="${'success'.equals(flashMessages.infos.edition)}" class="alert alert-success" th:text="#{album.admin.edit.success}"></div>
                <div th:if="${flashMessages.errors.edition != null}" class="alert alert-danger" th:text="#{${'album.admin.edit.errors.' + flashMessages.errors.edition}}"></div>
                <form id="edit-album-form" class="form-horizontal" th:action="@{'/albums/'+${album.id}}" method="post">
                    <p></p>
                    <div class="control-group">
                        <label class="control-label" for="name" th:text="#{album.admin.edit.album_name}"></label>
                        <div class="controls">
                            <input type="text" required="required" class="span5" id="name" name="name" th:value="${album.name}" />
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label" for="beginDate" th:text="#{album.admin.edit.beginDate}"></label>
                        <div class="controls">
                            <div class="input-append date">
                                <input type="text"  data-format="dd/MM/yyyy hh:mm:ss" required="required" class="span3" id="beginDate" name="beginDate" th:value="${#dates.format(album.beginDate, 'dd/MM/yyyy HH:mm:ss')}" />
                                <span class="add-on">
                                    <i data-time-icon="icon-time" data-date-icon="icon-calendar"></i>
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label" for="endDate" th:text="#{album.admin.edit.endDate}"></label>
                        <div class="controls">
                            <div class="input-append date">
                                <input type="text" data-format="dd/MM/yyyy hh:mm:ss" required="required" class="span3" id="endDate" name="endDate" th:value="${#dates.format(album.endDate, 'dd/MM/yyyy HH:mm:ss')}" />
                                <span class="add-on">
                                    <i data-time-icon="icon-time" data-date-icon="icon-calendar"></i>
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label" for="name" th:text="#{album.admin.edit.album_description}"></label>
                        <div class="controls">
                            <textarea class="span5" id="description" name="description" th:text="${album.description}" rows="5"></textarea>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label" for="downloadable" th:text="#{album.admin.edit.download}"></label>
                        <div class="controls">
                            <label class="checkbox" th:inline="text">
                                <input id="downloadable" type="checkbox" name="downloadable" value="true" th:checked="${album.downloadable}"/> [[#{album.admin.edit.download_description}]]
                            </label>
                        </div>
                    </div>
                    <div class="form-actions">
                        <input type="submit" class="btn btn-primary" data:loading-text="#{common.modification_in_progress}" th:value="#{common.modify}" />
                    </div>
                </form>
            </div>
        </div>
        <div th:fragment="script">
            <script th:src="@{/static/js/lib/jquery-1.9.1.min.js}"></script>
            <script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/js/bootstrap.min.js"></script>
            <script th:src="@{/static/js/lib/datetimepicker/bootstrap-datetimepicker.min.js}"></script>
            <script th:src="@{/static/js/lib/datetimepicker/bootstrap-datetimepicker.fr.js}"></script>
            <script type="text/javascript">
                $(function() {
                    $(".date").datetimepicker({
                        language: 'fr-FR'
                    });
                });
            </script>
        </div>
    </body>
</html>