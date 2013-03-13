<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head">
        <link rel="stylesheet" th:href="@{/static/css/thumbnails.css}" />
    </head>
    <body th:fragment="body">
        <div class="page-header"><h1 th:text="#{home.title}"></h1></div>

        <ul class="thumbnails albums" th:if="${not #lists.isEmpty(albums)}">
            <li th:each="album : ${albums}">
                <a class="thumbnail" th:href="@{'/albums/'+ ${album.id}}">
                    <span class="picture" th:style="${'background-image:url('} + @{'/' + ${album.coverUrl}} + ${')'}"></span>
                    <span class="title" th:title="${album.name}"><span th:text="${album.name}"></span></span>
                    <span class="filter">
                        <i class="icon-plus-sign"></i>
                        <span class="date" th:if="${album.beginDate != null}" th:inline="text">
                            <i class="icon-calendar"></i>
                            [[${album.strBeginDate}]]
                        </span>
                        <span class="videos count" th:if="${album.videosCount > 0}" th:inline="text">
                            [[${album.totalVideosCount}]]
                            [[${album.totalVideosCount > 1 ? #messages.msg('common.videos') : #messages.msg('common.video')}]]
                            <i class="icon-film"></i>
                        </span>
                        <span class="photos count" th:if="${album.totalPhotosCount > 0}" th:inline="text">
                            [[${album.totalPhotosCount}]]
                            [[${album.totalPhotosCount > 1 ? #messages.msg('common.photos') : #messages.msg('common.photo')}]]
                            <i class="icon-picture"></i>
                        </span>
                    </span>
                </a>
            </li>
        </ul>
        <p class="alert" th:if="${#lists.isEmpty(albums)}" th:inline="text"><strong th:text="#{common.warning} + ': '"></strong>[[#{common.no_album}]]</p>
    </body>
</html>

