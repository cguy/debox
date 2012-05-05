<form class="form-horizontal" action="#/account/{{id}}" method="post">
    <h2 class="page-header">{{i18n.administration.account.title}}</h2>
    <p class="hide"><a class="close">&times;</a></p>
    <div class="control-group">
        <label class="control-label" for="username">{{i18n.administration.account.username}}:</label>
        <div class="controls">
            <input type="text" required class="input-large" id="username" name="username" value="{{username}}" />
        </div>
    </div>
    <div class="control-group">
        <label class="control-label" for="oldPassword">{{i18n.administration.account.old_password}}:</label>
        <div class="controls">
            <input type="password" required class="input-large" id="oldPassword" name="oldPassword" />
        </div>
    </div>
    <div class="control-group">
        <label class="control-label" for="password">{{i18n.administration.account.new_password}}:</label>
        <div class="controls">
            <input type="password" required class="input-large" id="password" name="password" />
        </div>
    </div>
    <div class="control-group">
        <label class="control-label" for="confirm">{{i18n.administration.account.password_repeat}}:</label>
        <div class="controls">
            <input type="password" required class="input-large" id="confirm" name="confirm" />
        </div>
    </div>
    <div class="form-actions">
        <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.common.modification_in_progress}}" value="{{i18n.common.validate}}" />
    </div>
</form>