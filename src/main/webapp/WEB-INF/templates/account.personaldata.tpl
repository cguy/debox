<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head">
    </head>
    <body>
        <div th:fragment="body">
            <h2 class="subtitle" th:text="#{account.personaldata.title}"></h2>

            <form id="personaldata" class="form-horizontal row block" th:action="@{'/accounts/' + ${user.id}}" method="post">
                <h3 th:text="#{account.personaldata.edit}"></h3>
                <p class="hide"><a class="close">&times;</a></p>

                <div class="row">
                    <div class="span5">
                        <div class="control-group">
                            <label class="control-label" for="username" th:text="#{account.personaldata.username}"></label>
                            <div class="controls">
                                <input type="text" required="required" class="span3" id="username" name="username" th:value="${user.username}" />
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="span5">
                        <div class="control-group">
                            <label class="control-label" for="firstname" th:text="#{account.personaldata.firstname}"></label>
                            <div class="controls">
                                <input type="text" class="span3" id="firstname" name="firstname" th:value="${user.firstName}" />
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="span5">
                        <div class="control-group">
                            <label class="control-label" for="lastname" th:text="#{account.personaldata.lastname}"></label>
                            <div class="controls">
                                <input type="text" class="span3" id="lastname" name="lastname" th:value="${user.lastName}" />
                            </div>
                        </div>
                    </div>
                </div>
                <div class="form-actions">
                    <input type="submit" class="btn btn-primary" data:loading-text="#{common.modification_in_progress}" th:value="#{common.validate}" />
                </div>
            </form>


            <form id="credentials" class="form-horizontal block" th:action="@{'/accounts/' + ${user.id} + '/credentials'}" method="post">
                <h3 th:text="#{account.personaldata.passwordChange}"></h3>
                <p class="hide"><a class="close">&times;</a></p>

                <div class="row">
                    <div class="span5">
                        <div class="control-group">
                            <label class="control-label" for="oldPassword" th:text="#{account.personaldata.old_password}"></label>
                            <div class="controls">
                                <input type="password" class="span3" id="oldPassword" name="oldPassword" />
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="span5">
                        <div class="control-group">
                            <label class="control-label" for="password" th:text="#{account.personaldata.new_password}"></label>
                            <div class="controls">
                                <input type="password" class="span3" id="password" name="password" />
                            </div>
                        </div>
                    </div>
                </div>
                <div class="form-actions">
                    <input type="submit" class="btn btn-primary" data:loading-text="#{common.modification_in_progress}" th:value="#{common.validate}" />
                </div>
            </form>


            <form id="delete-account" class="form-horizontal row block" th:action="@{'/accounts/' + ${user.id} + '/delete'}" method="get">
                <h3 th:text="#{account.personaldata.accountDeletion.title}"></h3>
                <p class="hide"><a class="close">&times;</a></p>
                <p th:text="#{account.personaldata.accountDeletion.message}"></p>
                <div class="form-actions">
                    <input type="submit" class="btn btn-danger" data:loading-text="#{common.modification_in_progress}" th:value="#{common.delete}" />
                </div>
            </form>

            <form id="delete-account-confirm" class="form-horizontal modal fade hide" th:action="@{'/accounts/' + ${user.id} + '/delete'}" method="post">
                <div class="modal-header">
                    <a class="close" data-dismiss="modal">&times;</a>
                    <h3 th:text="#{account.personaldata.accountDeletion.title}"></h3>
                </div>
                <div class="modal-body">
                    <p></p>
                    <p th:text="#{account.personaldata.accountDeletion.message}"></p>
                    <p class="alert warning" th:inline="text"><span class="label label-warning" th:text="#{common.warning}"></span> [[#{account.personaldata.accountDeletion.irreversible}]]</p>
                </div>
                <div class="modal-footer">
                    <input type="submit" class="btn btn-danger" th:value="#{common.delete}" />
                    <button type="reset" data-dismiss="modal" class="btn" th:text="#{common.cancel}"></button>
                </div>
            </form>
        </div>
    </body>
</html>