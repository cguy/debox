<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head">
        <link rel="stylesheet" th:href="@{/static/css/thumbnails.css}" />
    </head>
    <body>
        <div th:fragment="body">
            <h2 class="subtitle" th:text="#{album.edit.tab.medias}"></h2>
            <div id="photos-edition">
                <ul class="thumbnails photos">
                    <li class="span2" th:each="media : ${medias}" th:with="isVideo=${media instanceof org.debox.photo.model.Video}" >
                        <div data-id="{{id}}" data:video="${isVideo}" class="thumbnail">
                            <span th:unless="${isVideo}" class="picture" th:style="${'background-image:url('} + @{'/' + ${media.thumbnailUrl}} + ${')'}"></span>
                            <span th:if="${isVideo}" class="picture" th:style="${'background-image:url('} + @{'/' + ${media.squareThumbnailUrl}} + ${')'}"></span>
                            <span class="title" th:text="${media.title}"></span>
                            <span class="actions">
                                <a href="#edit-media" class="edit-media"><i class="icon-edit"></i></a>
                                <a href="#delete-media" class="delete-media"><i class="icon-remove"></i></a>
                            </span>
                        </div>
                    </li>
                </ul>
            </div>

            <form id="edit-media" class="modal fade hide form-horizontal" method="post">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h3 th:text="#{media.edit.title}"></h3>
                </div>
                <div class="modal-body">
                    <p class="alert alert-danger hide" th:text="#{media.edit.error}"></p>
                    <div class="control-group">
                        <label class="control-label" for="mediaTitle" th:text="#{media.title}"></label>
                        <div class="controls">
                            <input type="text" id="mediaTitle" name="title" th:placeholder="#{media.edit.placeholder}" />
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <input class="btn btn-primary" type="submit" th:value="#{common.validate}"/>
                    <button class="btn" data-dismiss="modal" type="button" th:text="#{common.cancel}"></button>
                </div>
            </form>

            <form id="delete-media" class="modal fade hide" method="delete">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h3 th:text="#{media.delete.title}"></h3>
                </div>
                <div class="modal-body">
                    <p class="alert alert-danger hide" th:text="#{media.delete.error}"></p>
                    <p th:text="#{media.delete.confirm}"></p>
                </div>
                <div class="modal-footer">
                    <input class="btn btn-danger" type="submit" th:value="#{common.delete}"/>
                    <button class="btn" data-dismiss="modal" type="button" th:text="#{common.cancel}"></button>
                </div>
            </form>
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