<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head">
        <link rel="stylesheet" th:href="@{/static/css/thumbnails.css}" />
    </head>
    <body>
        <div th:fragment="scripts"></div>
        <div th:fragment="body">
            <h2 class="subtitle" th:text="#{account.albums.title}"></h2>

            <ul class="thumbnails settings" th:if="${not #lists.isEmpty(albums)}">
                <li th:each="album : ${albums}" th:include="account.albums.album::body"></li>
            </ul>
            <p class="alert" th:text="#{common.no_album}" th:if="${#lists.isEmpty(albums)}"></p>
        </div>
    </body>
</html>
