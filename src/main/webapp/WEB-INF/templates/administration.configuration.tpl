<form id="overall-configuration" class="form-vertical block" action="#/administration/configuration" method="post">
    <h3>{{i18n.administration.config.title}}</h3>
    <p class="alert hide"></p>
    <div class="control-group">
        <label class="control-label" for="title">{{i18n.administration.config.galery_title}}</label>
        <div class="controls">
            <input class="span5" type="text" required id="title" name="title" placeholder="{{i18n.administration.config.galery_title_placeholder}}" value="{{title}}" />
        </div>
    </div>
    <div class="control-group">
        <label class="control-label" for="sourceDirectory">{{i18n.administration.config.directory}}</label>
        <div class="controls">
            <input class="span5" type="text" required id="sourceDirectory" name="workingDirectory" placeholder="{{i18n.administration.config.directory_placeholder}}" value="{{workingDirectory}}" />
        </div>
    </div>
    <div class="form-actions">
        <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.administration.processing}}" value="{{i18n.administration.config.save}}" />
    </div>
</form>

<form id="thirdparty-configuration" class="third-party form-horizontal block" action="#/administration/configuration/social" method="post">
    <h3>Configuration des accès tiers</h3>
    <p class="alert hide"></p>

    <label class="checkbox"><input type="checkbox" class="thirdparty-activation" name="activated" {{#thirdPartyActivation}}checked{{/thirdPartyActivation}} />Activer la configuration des comptes tiers</label>

    <div class="providers">
        
        <h3>Facebook</h3>
        <div class="control-group">
            <label class="control-label" for="facebookApiKey">ID de l’application/Clé de l’API :</label>
            <div class="controls">
                <input class="span5" type="text" id="facebookApiKey" name="facebook.apiKey" value="{{facebook.apiKey}}" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="facebookSecret">Clé secrète :</label>
            <div class="controls">
                <input class="span5" type="text" id="facebookSecret" name="facebook.secret" value="{{facebook.secret}}" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="facebookCallbackURL">Adresse de callback :</label>
            <div class="controls">
                <input class="span5" type="text" id="facebookCallbackURL" name="facebook.callbackURL" value="{{facebook.callbackURL}}" />
            </div>
        </div>
    </div>

    <div class="form-actions">
        <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.administration.processing}}" value="{{i18n.administration.config.save}}" />
    </div>
</form>
