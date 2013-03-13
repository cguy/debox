<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head">
        <link rel="stylesheet" th:href="@{/static/js/lib/select2/select2.css}" />
    </head>
    <body>
        <div th:fragment="body">
            <h2 class="subtitle" th:text="#{album.admin.edit.album_authorized_tokens}"></h2>
            <div class="block">
                <form id="edit-album-form" class="form-horizontal" th:action="@{'/albums/'+${album.id}}" method="post">
                    <p></p>
                    <div class="control-group">
                        <label class="control-label" for="visibility" th:text="#{album.admin.edit.album_visibility}"></label>
                        <div class="controls">
                            <select name="visibility" id="visibility" class="span4">
                                <option value="true" th:selected="${album.publicAlbum}" th:text="#{common.public}"></option>
                                <option value="false" th:selected="${!album.publicAlbum}" th:text="#{common.private}"></option>
                            </select>
                        </div>
                    </div>
                    <div th:class="'control-group' + (${album.publicAlbum} ? ' hide' : '')" id="authorizedTokensGroup">
                        <label class="control-label" th:text="#{album.admin.edit.album_authorized_tokens}"></label>
                        <div class="controls">
                            <select name="authorizedTokens"
                                    data:placeholder="#{album.admin.edit.album_authorized_tokens_placeholder}"
                                    class="chzn-select span4" multiple="multiple">
                                <optgroup label="Contacts sociaux" th:each="contact : ${contacts}">
                                    <option th:value="${provider.id}+'-'+${contact.id}" th:selected="${contact.authorized}" th:text="${contact.name} + ' - ' + ${contact.provider.name}"></option>
                                </optgroup>
                                <optgroup label="Accès utilisateur" th:each="token : ${tokens}">
                                    <option th:value="${token.id}" th:selected="${not #lists.isEmpty(token.albums)}" th:text="${token.label}"></option>
                                </optgroup>
                            </select>
                        </div>
                    </div>
                    <div class="form-actions">
                        <input type="submit" class="btn btn-primary" data:loading-text="#{common.modification_in_progress}" th:value="#{common.modify}" />
                    </div>
                </form>
            </div>
        </div>
        <div th:fragment="script">
            <script th:src="@{/static/js/lib/head.load-0.96.min.js}"></script>
            <script type="text/javascript" th:inline="javascript">
                /*<![CDATA[*/
                head.js(
                    [[@{/static/js/lib/jquery-1.9.1.min.js}]],
                    [[@{/static/js/lib/select2/select2.min.js}]],
                    function() {
                        $(".chzn-select").select2();
                    }
                );
                /*]]>*/
            </script>
        </div>
    </body>
</html>