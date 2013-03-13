<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head"></head>
    <body>
        <div th:fragment="body">
            <h2 class="subtitle" th:text="#{account.sync.title}"></h2>

            <form id="synchronization" class="form-vertical block" th:action="@{/administration/sync}" method="post">
                <div id="sync-progress" class="alert alert-info hide">
                    <h3 class="alert-heading" style="line-height:50px;">
                        <span id="progress-label" th:text="#{account.sync.in_progress} + '&hellip;'"></span>&nbsp;<span id="progress-percentage"></span>
                        <button id="cancel-sync" class="btn btn-warning pull-right" style="margin: 5px -20px 0 20px;" th:text="#{account.sync.cancel}"></button>
                    </h3>
                    <div class="progress progress-info progress-striped active" style="margin:0 -20px 10px 0">
                        <div class="bar"></div>
                    </div>
                </div>

                <div class="control-group">
                    <p class="error"></p>
                    <div class="control-group">
                        <label class="control-label" th:text="#{account.sync.choice_mode}+':'"></label>
                        <div class="controls" th:inline="text">
                            <label class="radio">
                                <input type="radio" name="mode" value="fast" /><strong th:text="#{account.sync.fastest}+':'"></strong> [[#{account.sync.fastest_description}]]
                            </label>
                            <label class="radio">
                                <input type="radio" name="mode" value="normal" checked="checked" /><strong th:text="#{account.sync.normal}+':'"></strong> [[#{account.sync.normal_description}]]
                            </label>
                            <label class="radio">
                                <input type="radio" name="mode" value="slow" /><strong th:text="#{account.sync.longest} + ':'"></strong> [[#{account.sync.longest_description}]]
                            </label>
                        </div>
                        <div class="controls">
                            <label class="checkbox">
                                <input type="checkbox" name="forceCheckDates"/><em th:text="#{account.sync.force_check_dates}"></em>
                            </label>
                        </div>
                        <div class="alert alert-warning" th:inline="text"><span class="label label-warning" th:text="#{common.warning}"></span>&nbsp;[[#{account.sync.warning}]]</div>
                    </div>
                </div>
                <div class="form-actions">
                    <input type="submit" class="btn btn-primary" data:loading-text="#{administration.processing}" th:value="#{account.sync.launch}" />
                </div>
            </form>
        </div>
    </body>
</html>
