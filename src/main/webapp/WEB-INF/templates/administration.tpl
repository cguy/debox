<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head"></head>
    <body th:fragment="body">
        <div>
            <ul class="nav nav-tabs nav-stacked menu account" th:inline="text">
                <li th:class="'settings' + (${page eq 'administration.configuration'} ? ' active')"><a th:href="@{'/administration/settings'}"><i class="icon-cog"></i>[[#{account.settings.tab}]]</a></li>
            </ul>

            <div id="administration" th:class="${#strings.substringAfter(page,'administration.')}" th:include="__${page}__::body"></div>
        </div>
    </body>
</html>
