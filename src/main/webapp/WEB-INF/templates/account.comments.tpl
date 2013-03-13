<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head">
    </head>
    <body>
        <div th:fragment="body">
            <h2 class="subtitle" th:text="#{account.comments.title}"></h2>

            <div class="block">
                <h3 th:text="#{common.summary}"></h3>
                <ul class="unstyled" th:inline="text">
                    <li>[[#{account.comments.albums}]] <span class="bold">[[${albums}]]</span></li>
                    <li>[[#{account.comments.photos}]] <span class="bold">[[${photos}]]</span></li>
                    <li>[[#{account.comments.videos}]] <span class="bold">[[${videos}]]</span></li>
                    <li>[[#{account.comments.total}]] <span class="bold">[[${total}]]</span></li>
                </ul>

                <h3 th:text="#{account.comments.list}"></h3>
                <table id="admin-comments" class="table table-striped table-bordered table-condensed" th:unless="${#lists.isEmpty(comments)}">
                    <colgroup>
                        <col />
                        <col />
                        <col />
                        <col />
                    </colgroup>
                    <thead>
                        <tr>
                            <th th:text="#{account.comments.date}"></th>
                            <th th:text="#{account.comments.author}"></th>
                            <th th:text="#{account.comments.content}"></th>
                            <th th:text="#{account.comments.actions}"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr th:id="${comment.id}" th:each="comment : ${comments}">
                            <td th:text="${comment.date}"></td>
                            <td><div th:inline="text">[[${comment.user.firstName} + ' ' + ${comment.user.lastName}]]</div></td>
                            <td>
                                <a th:unless="${#strings.isEmpty(comment.media.albumId)}" th:href="@{'/albums/' + ${comment.media.albumId} + '#' + ${comment.media.id} + '/comments'}" th:text="${comment.content}"></a>
                                <a th:if="${#strings.isEmpty(comment.media.albumId)}" th:href="@{'/albums/' + ${comment.media.id} + '#comments'}" th:text="${comment.content}"></a>
                            </td>
                            <td>
                                <div class="btn-group">
                                    <a href="#modal-comment-delete" role="button" class="btn btn-small btn-danger" data-toggle="modal" th:text="#{common.delete}"></a>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <p class="alert" th:if="${#lists.isEmpty(comments)}" th:text="#{account.comments.empty}"></p>
            </div>

            <form id="modal-comment-delete" class="modal hide fade form-horizontal" action="" data:action="@{/comments/}" method="delete">
                <div class="modal-header">
                    <a class="close" data-dismiss="modal">&times;</a>
                    <h3 th:text="#{account.comments.delete.title}"></h3>
                </div>
                <div class="modal-body">
                    <p></p>
                    <p th:text="#{account.comments.delete.message}"></p>
                </div>
                <div class="modal-footer">
                    <input type="submit" class="btn btn-danger" data:loading-text="#{common.deletion_in_progress}" th:value="#{common.delete}" />
                    <button type="reset" class="btn" th:text="#{common.cancel}"></button>
                </div>
            </form>
        </div>
    </body>
</html>
