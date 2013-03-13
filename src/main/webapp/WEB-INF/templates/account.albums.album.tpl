<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head"></head>
    <body>
        <div th:fragment="body">
            <a th:id="${album.id}" th:href="@{'/albums/' + ${album.id} + '/edition'}" class="album admin thumbnail" th:style="${'background-image:url('} + @{'/' + ${album.coverUrl}} + ${')'}">
                <ul class="unstyled" th:inline="text">
                    <li title="${album.name}"><strong th:text="${album.name}"></strong></li>
                    <li th:text="${album.strBeginDate}"></li>
                    <li th:text="#{account.albums.photo_number} + ' ' + ${album.photosCount}"></li>
                    <li class="downloadable" th:if="${album.downloadable}">
                        <i class="icon-download-alt"></i>&nbsp;[[#{account.albums.downloadable}]]
                    </li>
                    <li class="undownloadable" th:unless="${album.downloadable}">
                        <i class="icon-download-alt"></i>&nbsp;[[#{account.albums.not_downloadable}]]
                    </li>
                    <li class="public" th:if="${album.publicAlbum}">
                        <i class="icon-ok"></i>&nbsp;[[#{common.public}]]
                    </li>
                    <li class="private" th:unless="${album.publicAlbum}">
                        <i class="icon-ban-circle"></i>&nbsp;[[#{common.private}]]
                    </li>
                </ul>
                <div class="btn-group-vertical" th:inline="text">
                    <button  th:if="${album.downloadable}" class="btn btn-small undownloadable" data:loading-text="#{common.modification_in_progress}">
                        <i class="icon-download-alt"></i>&nbsp;[[#{account.albums.make_undownloadable}]]
                    </button>
                    <button th:unless="${album.downloadable}" class="btn btn-small downloadable" data:loading-text="#{common.modification_in_progress}">
                        <i class="icon-download-alt"></i>&nbsp;[[#{account.albums.make_downloadable}]]
                    </button>
                    <button th:if="${album.publicAlbum}" class="btn btn-small private" data:loading-text="#{common.modification_in_progress}">
                        <i class="icon-ban-circle"></i>&nbsp;[[#{account.albums.make_private}]]
                    </button>
                    <button th:unless="${album.publicAlbum}" class="btn btn-small public" data:loading-text="#{common.modification_in_progress}">
                        <i class="icon-ok"></i>&nbsp;[[#{account.albums.make_public}]]
                    </button>
                </div>
            </a>
        </div>
    </body>
</html>