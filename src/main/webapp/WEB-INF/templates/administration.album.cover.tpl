<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head">
        <link rel="stylesheet" th:href="@{/static/css/thumbnails.css}" />
    </head>
    <body th:fragment="body">
        <h2 class="subtitle" th:text="#{album.admin.edit.choose_cover.title}"></h2>
        <div id="cover-photos">
            <div class="block current-cover">
                <h3 th:text="#{album.admin.edit.cover.current}"></h3>
                <ul class="thumbnails albums center">
                    <li>
                        <div class="thumbnail" th:href="@{'/albums/'+ ${album.id}}">
                            <span class="picture" th:style="'background-image:url(' + @{'/' + ${album.coverUrl}} + '?_=' + ${#dates.createNow().getTime()} + ')'"></span>
                            <span class="title" th:title="${album.name}"><span th:text="${album.name}"></span></span>
                        </div>
                    </li>
                </ul>
            </div>
            <div class="block list">
                <p class="alert alert-info" th:text="#{album.admin.edit.cover.instructions}"></p>
                <h3 th:text="#{album.subalbums}" th:if="${not #lists.isEmpty(subAlbums)}"></h3>
                <ul class="thumbnails albums" th:if="${not #lists.isEmpty(subAlbums)}">
                    <li th:each="subAlbum : ${subAlbums}">
                        <div data:id="'a.' + ${subAlbum.id}" class="thumbnail cover" th:style="${'background-image:url('} + @{'/' + ${subAlbum.coverUrl}} + ${')'}"></div>
                    </li>
                </ul>

                <h3 th:if="${not #lists.isEmpty(medias) and not #lists.isEmpty(subAlbums)}" th:text="#{album.admin.edit.choose_cover.medias}"></h3>
                <ul th:if="${not #lists.isEmpty(medias)}" class="thumbnails photos">
                    <li th:each="media : ${medias}">
                        <div data:id="${media.id}"
                             class="thumbnail"
                             th:style="'background-image:url(' + (${media instanceof org.debox.photo.model.Photo} ? @{'/'+${media.thumbnailUrl}} :  @{'/'+${media.squareThumbnailUrl}})  + ')'">
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </body>
</html>
