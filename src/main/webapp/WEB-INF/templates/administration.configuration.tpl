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
        <label class="control-label" for="sourceDirectory">{{i18n.administration.config.source_directory}}:</label>
        <div class="controls">
            <input class="span5" type="text" required id="sourceDirectory" name="sourceDirectory" placeholder="{{i18n.administration.config.source_directory_placeholder}}" value="{{source_path}}" />
        </div>
    </div>
    <div class="control-group">
        <label class="control-label" for="targetDirectory">{{i18n.administration.config.target_directory}}:</label>
        <div class="controls">
            <input class="span5" type="text" required id="targetDirectory" name="targetDirectory" placeholder="{{i18n.administration.config.target_directory_placeholder}}" value="{{target_path}}" />
        </div>
    </div>
    <div class="form-actions">
        <input type="hidden" name="force" />
        <button type="button" class="btn btn-danger" data-loading-text="{{i18n.administration.processing}}">{{i18n.administration.config.save_and_sync}}</button>
        <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.administration.processing}}" value="{{i18n.administration.config.save}}" />
    </div>
</form>

<form id="thirdparty-configuration" class="third-party form-horizontal block" action="#/administration/configuration/social" method="post">
    <h3>Configuration des accès tiers</h3>
    <p class="alert hide"></p>

    <label class="checkbox"><input type="checkbox" class="thirdparty-activation" name="activated" {{#thirdPartyActivation}}checked{{/thirdPartyActivation}} />Activer la configuration des comptes tiers</label>

    <div class="providers {{^thirdPartyActivation}}hide{{/thirdPartyActivation}}">
        
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

        <!--
        <h3>Google</h3>
        <div class="control-group">
            <label class="control-label" for="googleApiKey">ID Client :</label>
            <div class="controls">
                <input class="span5" type="text" id="googleApiKey" name="google.apiKey" value="{{google.apiKey}}" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="googleSecret">Clé secrète :</label>
            <div class="controls">
                <input class="span5" type="text" id="googleSecret" name="google.secret" value="{{google.secret}}" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="googleCallbackURL">Adresse de callback :</label>
            <div class="controls">
                <input class="span5" type="text" id="googleCallbackURL" name="google.callbackURL" value="{{google.callbackURL}}" />
            </div>
        </div>

        <h3>Twitter</h3>
        <div class="control-group">
            <label class="control-label" for="twitterApiKey">Consumer key :</label>
            <div class="controls">
                <input class="span5" type="text" id="twitterApiKey" name="twitter.apiKey" value="{{twitter.apiKey}}" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="twitterSecret">Consumer secret :</label>
            <div class="controls">
                <input class="span5" type="text" id="twitterSecret" name="twitter.secret" value="{{twitter.secret}}" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="twitterCallbackURL">Adresse de callback :</label>
            <div class="controls">
                <input class="span5" type="text" id="twitterCallbackURL" name="twitter.callbackURL" value="{{twitter.callbackURL}}" />
            </div>
        </div>
        -->
    </div>

    <div class="form-actions">
        <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.administration.processing}}" value="{{i18n.administration.config.save}}" />
    </div>
</form>
