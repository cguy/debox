<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="utf-8" />
        <meta name="robots" content="noindex,nofollow,noarchive" />
        <title></title>
        
        <link rel="shortcut icon" type="image/png" th:href="@{/static/img/logo.png}" />
        <link rel="stylesheet" th:href="@{/static/css/bootstrap.min.css}" />
        <link rel="stylesheet" th:href="@{/static/css/font-awesome.css}" />
        <link rel="stylesheet" th:href="@{/static/css/style.css}" />
        <link rel="stylesheet" th:substituteby="__${body}__::head" />
    </head>
    <body>
        <div class="navbar navbar-inverse navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container-fluid" th:include="header::body"></div>
            </div>
        </div>
        <div class="container-fluid" th:include="__${body}__::body"></div>
        <div th:substituteby="__${body}__::script"></div>
        <!--<div id="loading" th:text="#{common.loading}"></div>-->
    </body>
</html>
