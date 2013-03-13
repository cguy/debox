<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head">
        <link rel="stylesheet" th:href="@{/static/css/thumbnails.css}" />
    </head>
    <body>
        <div th:fragment="body">
            <div class="page-header album">
                <a th:href="@{${albumParent != null} ? ${albumParent.id} : '/'}" class="back"><i class="icon-circle-arrow-left"></i></a>

                <h1 th:id="${album.id}" th:text="${album.name}"></h1>
                <div class="information" th:text="${album.information}"></div>

                <a th:inline="text" th:if="${config.administrator}" th:href="@{'/albums/' + ${album.id} + '/edition'}" th:title="#{album.admin.edit.modify_this}" th:class="'pull-right edit-album ' + (${inEdition != null} ? ' hide')">
                    <i class="icon-cog"></i><br />[[#{common.modify}]]
                </a>
                
                <a th:if="${config.authenticated}" th:href="@{'/albums/' + ${album.id}}" data:href="@{'/albums/' + ${album.id}}" class="pull-right comments" th:inline="text">
                    <span th:class="'badge badge-info' + ${#lists.isEmpty(comments)} ? ' hide'" th:text="${comments.size}"></span>
                    <i class="icon-comment"></i><br />[[#{comments.title}]]
                </a>

                <div th:if="${#bools.isTrue(album.downloadable) and not #lists.isEmpty(medias)}" class="download dropdown pull-right">
                    <a href="#" data-placement="bottom" data-toggle="dropdown" th:title="#{album.download}" class="dropdown-toggle" th:inline="text">
                        <i class="icon-download-alt"></i><br />[[#{common.download}]]
                    </a>
                    <ul class="dropdown-menu">
                        <li><a th:href="@{${album.smallSizeDownloadUrl}}" th:text="#{album.reduced_size}"></a></li>
                        <li><a th:href="@{${album.downloadUrl}}" th:text="#{album.original_size}"></a></li>
                    </ul>
                </div>
            </div>

            <div id="album-content">
                <div id="album-comments" th:if="${#bools.isTrue(config.authenticated)}">
                    <div th:class="'alert alert-heading no-comments' + (${not #lists.isEmpty(comments)} ? ' hide')" th:text="#{comments.empty.album}"></div>

                    <div th:each="comment : ${comments}">
                        <div th:substituteby="comment::body"></div>
                    </div>

                    <form id="new-album-comment" method="post" action="@{'/albums/' + ${album.id} + '/comments'}">
                        <textarea name="content" required="required" th:placeholder="#{comments.placeholder}"></textarea>
                        <div class="form-actions">
                            <input type="submit" class="btn btn-primary btn-small" th:value="#{common.validate}" />
                        </div>
                    </form>
                </div>

                <div class="album_description" th:if="${not #strings.isEmpty(album.description)}" th:text="${album.description}"></div>
                <!--<div substituteby="administration.album.edit::body"></div>-->

                <div id="photos">
                    <h2 th:if="${not #lists.isEmpty(subAlbums) and not #lists.isEmpty(medias)}" th:text="#{album.subalbums}"></h2>
                    <ul class="thumbnails albums" th:if="${not #lists.isEmpty(subAlbums)}">
                        <li th:each="subAlbum : ${subAlbums}">
                            <a class="thumbnail" th:href="@{'/albums/' + ${subAlbum.id}}">
                                <span class="picture" th:style="${'background-image:url('} + @{'/' + ${subAlbum.coverUrl}} + ${')'}"></span>
                                <span class="title" th:title="${subAlbum.name}"><span th:text="${subAlbum.name}"></span></span>
                                <span class="filter">
                                    <i class="icon-plus-sign"></i>
                                    <span class="date" th:if="${subAlbum.beginDate != null}" th:inline="text">
                                        <i class="icon-calendar"></i>
                                        [[${subAlbum.strBeginDate}]]
                                    </span>
                                    <span class="videos count" th:if="${subAlbum.videosCount > 0}" th:inline="text">
                                        [[${subAlbum.totalVideosCount}]]
                                        [[${subAlbum.totalVideosCount > 1 ? #messages.msg('common.videos') : #messages.msg('common.video')}]]
                                        <i class="icon-film"></i>
                                    </span>
                                    <span class="photos count" th:if="${subAlbum.totalPhotosCount > 0}" th:inline="text">
                                        [[${subAlbum.totalPhotosCount}]]
                                        [[${subAlbum.totalPhotosCount > 1 ? #messages.msg('common.photos') : #messages.msg('common.photo')}]]
                                        <i class="icon-picture"></i>
                                    </span>
                                </span>
                            </a>
                        </li>
                    </ul>
                    <h2  th:if="${not #lists.isEmpty(medias) and not #lists.isEmpty(subAlbums)}" th:text="${album.photosCount} + ' ' + (${album.photosCount > 1} ? #{common.photos} : #{common.photo})"></h2>
                    <div th:if="${not #lists.isEmpty(medias)}" th:substituteby="album.medias::body"></div>

                    <p   th:if="${#lists.isEmpty(medias) and #lists.isEmpty(subAlbums)}" class="alert alert-danger" th:text="#{album.no_photos}" ></p>
                </div>

                <!--<div substituteby="administration.album.cover::body"></div>
                <div substituteby="administration.album.medias::body"></div>-->
            </div>
            <a id="top"></a>

            <form id="remove-comment" class="modal hide fade" th:action="@{'/albums/' + ${album.id} + '/comments/'}" data:action="@{'/albums/' + ${album.id} + '/comments/'}" method="delete">
                <div class="modal-header">
                    <h3 th:text="#{common.deletion}"></h3>
                </div>
                <div class="modal-body" tx:text="#{comments.confirm}"></div>
                <div class="modal-footer">
                    <input type="submit" class="btn btn-danger" data:loading-text="#{common.deletion_in_progress}" th:value="#{common.delete}" />
                    <button type="reset" class="btn" data-dismiss="modal" th:text="#{common.cancel}"></button>
                </div>
            </form>
        </div>
        
        
        <div th:fragment="script">
            <script th:src="@{/static/js/lib/modernizr.custom.77358.js}"></script>
            <script th:src="@{/static/js/lib/jquery-1.9.1.min.js}"></script>
            <script th:src="@{/static/js/lib/TweenLite.min.js}"></script>
            <script th:src="@{/static/js/lib/jquery.mousewheel.min.js}"></script>
            <script th:src="@{/static/js/lib/jquery.mCustomScrollbar.min.js}"></script>
            <script th:src="@{/static/js/lib/jwerty.js}"></script>
            <script th:src="@{/static/js/lib/bootstrap.min.js}"></script>
            <script th:src="@{/static/js/slideshow.js}"></script>
            <script th:src="@{/static/js/utils.js}"></script>
            <script th:src="@{/static/js/album.js}"></script>
            <script type="text/javascript">
                function reload() {
                    var hash = location.hash;
                    if (hash) {
                        hash = hash.substr(1);
                    }
                    loadAlbum(hash);
                }
                window.onhashchange = reload;
                reload();
            </script>
        </div>
    </body>
</html>
