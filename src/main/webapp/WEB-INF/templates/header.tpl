<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head></head>
    <body th:fragment="body">
        <a class="brand" th:if="${not #strings.isEmpty(config.title)}" th:href="@{/}" th:text="${config.title}"></a>
        <div class="nav-collapse">
            <ul class="nav">
                <li><a th:href="@{/}"><i class="icon-home"></i><span th:text="#{header.album_list}"></span></a></li>
                <li th:if="${#bools.isTrue(config.authenticated)}"><a th:href="@{/account}"><i class="icon-picture"></i><span th:text="#{header.settings}"></span></a></li>
                <li th:if="${#bools.isTrue(config.administrator)}"><a th:href="@{/administration}"><i class="icon-cog"></i><span th:text="#{header.administration}"></span></a></li>
            </ul>
            <ul class="nav pull-right about">
                <li>
                    <a th:href="@{/about}" title="#{about.tooltip}">
                        <i class="icon-question-sign"></i>
                    </a>
                </li>
            </ul>
            <ul class="nav pull-right" th:if="${not #strings.isEmpty(config.username)}">
                <li><a th:href="@{/logout}"><i class="icon-signout"></i><span th:text="#{header.disconnection}"></span></a></li>
            </ul>
            <p class="navbar-text pull-right" th:if="${not #strings.isEmpty(config.username)}"><i class="icon-user"></i><strong th:text="${config.username}"></strong></p>
            <ul class="nav pull-right" th:if="${#strings.isEmpty(config.username)}">
                <li><a th:href="@{/login}"><i class="icon-signin"></i><span th:text="#{header.connection}"></span></a></li>
                <li><a th:href="@{/register}"><i class="icon-user"></i>&nbsp;<span th:text="#{header.register}"></span></a></li>
            </ul>
        </div>
    </body>
</html>
