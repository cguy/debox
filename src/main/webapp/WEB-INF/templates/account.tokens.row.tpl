<tr th:each="token : ${tokens}" th:id="${token.id}" th:fragment="line">
    <td class="access_label" th:text="${token.label}"></td>
    <td class="albums">
        <form action="#/token/{{id}" method="post" class="album-access-form">
            <div>
                <button type="button" class="btn show-tree" th:inline="text"><i class="icon icon-list"></i>&nbsp;[[#{account.tokens.visible_albums}]]</button>
                <span class="hide" th:inline="text">
                    <button type="button" class="btn cancel"><i class="icon icon-remove"></i>&nbsp;[[#{common.cancel}]]</button>
                    <button type="submit" class="btn btn-primary validate"><i class="icon icon-ok"></i>&nbsp;[[#{common.validate}]]</button>
                </span>
                <div class="alert alert-success hide" th:text="#{account.tokens.edit.success}"></div>
                <div class="alert alert-error hide" th:text="#{account.tokens.edit.error}"></div>
            </div>
            <div class="albums-access hide" name="albums"></div>
        </form>
    </td>
    <td class="access_link actions" th:inline="text">
        <input type="text" class="accessShare" data:original="${token.url}" th:value="${token.url}" />
        <div class="alert alert-success hide" th:text="#{account.tokens.reinit.success}"></div>
        <span class="btn-group">
            <button class="btn btn-small edit"><i class="icon-pencil icon-white"></i>&nbsp;[[#{common.modify}]]</button>
            <button href="#modal-token-reinit" data-toggle="modal" class="btn btn-small reinit"><i class="icon-refresh icon-white"></i>&nbsp;[[#{account.tokens.reinit.label}]]</button>
        </span>
        <button href="#modal-token-delete" data-toggle="modal" class="btn btn-small btn-danger delete"><i class="icon-remove icon-white"></i>&nbsp;[[#{common.delete}]]</button>
    </td>
</tr>
