<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head"></head>
    <body>
        <div th:fragment="scripts"></div>
        <div th:fragment="body">
            <h2 class="subtitle" th:text="#{account.tokens.title}"></h2>

            <div class="block" th:unless="${#lists.isEmpty(providers)}">
                <h3 th:text="#{account.tokens.thirdparty.title}"></h3>
                <a th:each="provider : ${providers}" th:if="${provider.enabled}" th:href="@{provider.url}" th:class="'btn btn-' + ${provider.id}">
                    <img class="logo" th:src="@{'/static/img/' + ${provider.id} + '_logo.png'" th:inline="text"/>&nbsp;[[#{provider.name}]]
                </a>
                <hr />
                <table id="thirdparty_accounts" class="table table-bordered table-striped" th:unless="${#lists.isEmpty(accounts)}">
                    <tr>
                        <th th:text="#{account.tokens.thirdparty.provider.name}"></th>
                        <th th:text="#{account.tokens.thirdparty.provider.identifier}"></th>
                        <th th:text="#{common.deletion}" class="delete"></th>
                    </tr>
                    <tr th:id="${account.provider.id}+'-'+${account.providerAccountId}" th:each="account : ${accounts}">
                        <td th:inline="text">
                            <img th:src="@{'/static/img/' + ${account.provider.id} + '_logo.png'"/>&nbsp;[[${account.provider.name}]]
                        </td>
                        <td>
                            <img th:src="${account.avatarUrl}" />
                            <a th:href="${account.accountUrl}" th:text="${account.username}"></a>
                        </td>
                        <td class="delete">
                            <a href="#delete-third-party-account" class="btn btn-danger btn-small delete-third-party-account" data-toggle="modal" th:text="#{account.tokens.thirdparty.provider.deletion}"></a>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="block">
                <h3 th:text="#{account.tokens.token_list}"></h3>
                <table id="administration_tokens" th:class="'table table-striped table-bordered table-condensed' + (${#lists.isEmpty(tokens)} ? ' hide')">
                    <thead>
                        <tr>
                            <th th:text="#{account.tokens.label}"></th>
                            <th th:text="#{account.tokens.albums}"></th>
                            <th th:text="#{account.tokens.link2share}"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr th:substituteby="account.tokens.row::line"></tr>
                    </tbody>
                </table>
                <p th:if="${#lists.isEmpty(tokens)}" class="alert alert-warning" th:text="#{account.tokens.no_token}"></p>
            </div>

            <div class="block">
                <h3 th:text="#{account.tokens.new_token}"></h3>
                <form id="form-token-create" class="well form-search" action="#/token" method="put">
                    <input type="text" required="required" name="label" class="input" th:placeholder="#{account.tokens.new_token_label}"/>
                    <button type="submit" class="btn btn-primary" th:text="#{account.tokens.create_token}"></button>
                </form>
            </div>

            <form id="edit_token" class="modal hide fade form-horizontal" action="#/token" method="post">
                <div class="modal-header">
                    <a class="close" data-dismiss="modal">&times;</a>
                    <h3 th:text="#{account.tokens.edit.title}"></h3>
                </div>
                <div class="modal-body">
                    <p></p>
                    <div class="control-group">
                        <label class="control-label" for="label" th:text="#{account.tokens.edit.label}"></label>
                        <div class="controls">
                            <input type="text" required="required" class="span4" id="label" name="label" />
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <input type="hidden" name="id" />
                    <input type="submit" class="btn btn-primary" data:loading-text="#{common.modification_in_progress}" th:value="#{common.modify}" />
                    <button type="reset" class="btn" th:text="#{common.cancel}"></button>
                </div>
            </form>

            <form id="modal-token-delete" class="modal hide fade form-horizontal" action="#/token" method="delete">
                <div class="modal-header">
                    <a class="close" data-dismiss="modal">&times;</a>
                    <h3 th:text="#{account.tokens.delete.title}"></h3>
                </div>
                <div class="modal-body">
                    <p></p>
                    <p th:inline="text">[[#{account.tokens.delete.message}]] <strong></strong>?</p>
                </div>
                <div class="modal-footer">
                    <input type="hidden" name="id" />
                    <input type="submit" class="btn btn-danger" data:loading-text="#{common.deletion_in_progress}" th:value="#{common.delete}" />
                    <button type="reset" class="btn" th:text="#{common.cancel}"></button>
                </div>
            </form>

            <form id="modal-token-reinit" class="modal hide fade form-horizontal" action="#/token/reinit" method="post">
                <div class="modal-header">
                    <a class="close" data-dismiss="modal">&times;</a>
                    <h3 th:text="#{account.tokens.reinit.title}"></h3>
                </div>
                <div class="modal-body">
                    <p></p>
                    <p th:inline="text">[[#{account.tokens.reinit.message}]] <strong></strong>?</p>
                    <p th:text="#{account.tokens.reinit.description}"></p>
                </div>
                <div class="modal-footer">
                    <input type="hidden" name="id" />
                    <input type="submit" class="btn btn-warning" data:loading-text="#{account.tokens.reinit.label_in_progress}" th:value="#{account.tokens.reinit.label}" />
                    <button type="reset" class="btn" th:text="#{common.cancel}"></button>
                </div>
            </form>

            <form id="delete-third-party-account" class="modal hide fade form-horizontal" action="#/third-party-account" method="delete">
                <div class="modal-header">
                    <a class="close" data-dismiss="modal">&times;</a>
                    <h3 th:text="#{account.tokens.thirdparty.provider.remove.title}"></h3>
                </div>
                <div class="modal-body">
                    <p th:text="#{account.tokens.thirdparty.provider.remove.message}"></p>
                </div>
                <div class="modal-footer">
                    <input type="hidden" name="id" class="third-party-account-id" />
                    <input type="submit" class="btn btn-danger" data:loading-text="#{common.deletion_in_progress}" th:value="#{common.delete}" />
                    <button type="reset" class="btn" th:text="#{common.cancel}"></button>
                </div>
            </form>
        </div>
    </body>
</html>
