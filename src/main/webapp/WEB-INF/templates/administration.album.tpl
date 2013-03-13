<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head">
        <link rel="stylesheet" th:substituteby="__${page}__::head" />
    </head>
    <body>
        <div th:fragment="body">
            <ul class="nav nav-tabs nav-stacked menu account" th:inline="text">
                <li th:class="'edit' + (${page eq 'administration.album.edit'} ? ' active')"><a th:href="@{'/albums/'+${album.id}+'/edition'}"><i class="icon-pencil"></i>[[#{album.admin.edit.modify}]]</a></li>
                <li th:class="'permissions' + (${page eq 'administration.album.permissions'} ? ' active')"><a th:href="@{'/albums/'+${album.id}+'/edition/permissions'}"><i class="icon-lock"></i>[[#{album.admin.edit.album_authorized_tokens}]]</a></li>
                <li th:class="'cover' + (${page eq 'administration.album.cover'} ? ' active')"><a th:href="@{'/albums/'+${album.id}+'/edition/cover'}"><i class="icon-picture"></i>[[#{album.edit.tab.cover}]]</a></li>
                <li th:class="'medias' + (${page eq 'administration.album.medias'} ? ' active')"><a th:href="@{'/albums/'+${album.id}+'/edition/medias'}"><i class="icon-camera"></i>[[#{album.edit.tab.medias}]]</a></li>
            </ul>

            <div id="album-edition" th:class="${#strings.substringAfter(page,'administration.album.')}" th:include="__${page}__::body"></div>
        </div>
        <div th:fragment="script" th:substituteby="__${page}__::script"></div>
    </body>
</html>