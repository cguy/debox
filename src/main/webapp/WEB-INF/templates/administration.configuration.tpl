<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head th:fragment="head"></head>
    <body th:fragment="body">
        <form id="overall-configuration" class="form-vertical block" th:action="@{/administration/configuration}" method="post">
            <h3 th:text="#{administration.config.title}"></h3>
            <p class="alert hide"></p>
            <div class="control-group">
                <label class="control-label" for="title" th:text="#{administration.config.galery_title}"></label>
                <div class="controls">
                    <input class="span5" type="text" required="required" id="title" name="title" th:placeholder="#{administration.config.galery_title_placeholder}" th:value="${title}" />
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="sourceDirectory" th:text="#{administration.config.directory}"></label>
                <div class="controls">
                    <input class="span5" type="text" required="required" id="sourceDirectory" name="workingDirectory" th:placeholder="#{administration.config.directory_placeholder}" th:value="${workingDirectory}" />
                </div>
            </div>
            <div class="form-actions">
                <input type="submit" class="btn btn-primary" data:loading-text="#{administration.processing}" th:value="#{administration.config.save}" />
            </div>
        </form>

        <form id="thirdparty-configuration" class="third-party form-horizontal block" action="@{/administration/configuration/social}" method="post">
            <h3 th:text="'Configuration des accès tiers'"></h3>
            <p class="alert hide"></p>

            <label class="checkbox"><input type="checkbox" class="thirdparty-activation" name="activated" th:checked="${thirdPartyActivation}" />Activer la configuration des comptes tiers</label>

            <div class="providers">

                <h3>Facebook</h3>
                <div class="control-group">
                    <label class="control-label" for="facebookApiKey">ID de l’application/Clé de l’API :</label>
                    <div class="controls">
                        <input class="span5" type="text" id="facebookApiKey" name="facebook.apiKey" th:value="${facebook.apiKey}" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="facebookSecret">Clé secrète :</label>
                    <div class="controls">
                        <input class="span5" type="text" id="facebookSecret" name="facebook.secret" th:value="${facebook.secret}" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="facebookCallbackURL">Adresse de callback :</label>
                    <div class="controls">
                        <input class="span5" type="text" id="facebookCallbackURL" name="facebook.callbackURL" th:value="${facebook.callbackURL}" />
                    </div>
                </div>

                <!--
                <h3>Google</h3>
                <div class="control-group">
                    <label class="control-label" for="googleApiKey">ID Client :</label>
                    <div class="controls">
                        <input class="span5" type="text" id="googleApiKey" name="google.apiKey" value="{google.apiKey}" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="googleSecret">Clé secrète :</label>
                    <div class="controls">
                        <input class="span5" type="text" id="googleSecret" name="google.secret" value="{google.secret}" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="googleCallbackURL">Adresse de callback :</label>
                    <div class="controls">
                        <input class="span5" type="text" id="googleCallbackURL" name="google.callbackURL" value="{google.callbackURL}" />
                    </div>
                </div>
        
                <h3>Twitter</h3>
                <div class="control-group">
                    <label class="control-label" for="twitterApiKey">Consumer key :</label>
                    <div class="controls">
                        <input class="span5" type="text" id="twitterApiKey" name="twitter.apiKey" value="{twitter.apiKey}" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="twitterSecret">Consumer secret :</label>
                    <div class="controls">
                        <input class="span5" type="text" id="twitterSecret" name="twitter.secret" value="{twitter.secret}" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="twitterCallbackURL">Adresse de callback :</label>
                    <div class="controls">
                        <input class="span5" type="text" id="twitterCallbackURL" name="twitter.callbackURL" value="{twitter.callbackURL}" />
                    </div>
                </div>
                -->
            </div>

            <div class="form-actions">
                <input type="submit" class="btn btn-primary" data:loading-text="#{administration.processing}" th:value="#{administration.config.save}" />
            </div>
        </form>
    </body>
</html>
