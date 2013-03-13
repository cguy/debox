<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head">
        <link rel="stylesheet" th:substituteby="__${page}__::head" />
    </head>
    <body th:fragment="body">
        <div>
            <ul class="nav nav-tabs nav-stacked menu account" th:inline="text">
                <!--<li class="dashboard"><a th:href="@{'/account/dashboard'}"><i class="icon-dashboard"></i>[[#{account.dashboard.tab}]]</a></li>-->
                <li th:class="'upload' + (${page eq 'account.upload'} ? ' active')"><a th:href="@{'/account/upload'}"><i class="icon-upload-alt"></i>[[#{account.upload.tab}]]</a></li>
                <li th:class="'albums' + (${page eq 'account.albums'} ? ' active')"><a th:href="@{'/account/albums'}"><i class="icon-camera-retro"></i>[[#{account.albums.tab}]]</a></li>
                <li th:class="'comments' + (${page eq 'account.comments'} ? ' active')"><a th:href="@{'/account/comments'}"><i class="icon-comment"></i>[[#{account.comments.tab}]]</a></li>
                <li th:class="'tokens' + (${page eq 'account.tokens'} ? ' active')"><a th:href="@{'/account/tokens'}"><i class="icon-user"></i>[[#{account.tokens.tab}]]</a></li>
                <li th:class="'synchronization' + (${page eq 'account.synchronization'} ? ' active')"><a th:href="@{'/account/synchronization'}"><i class="icon-refresh"></i>[[#{account.sync.tab}]]</a></li>
                <li th:class="'personaldata' + (${page eq 'account.personaldata'} ? ' active')"><a th:href="@{'/account/about-me'}"><i class="icon-info-sign"></i>[[#{account.personaldata.tab}]]</a></li>
                <li th:class="'settings' + (${page eq 'account.settings'} ? ' active')"><a th:href="@{'/account/settings'}"><i class="icon-cog"></i>[[#{account.settings.tab}]]</a></li>
            </ul>

            <div id="account" th:class="${#strings.substringAfter(page,'account.')}" th:include="__${page}__::body"></div>
        </div>
    </body>
</html>
