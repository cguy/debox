<div class="page-header">
    <h1>{{i18n.install.title}}</h1>
</div>
<div class="step">
    <h2 class="subtitle">{{i18n.install.introduction.title}}</h2>
    <div class="block">
        <p>{{i18n.install.introduction.presentation}}</p>
        <ol>
            {{#i18n.install.introduction.steps}}
            <li>{{.}}</li>
            {{/i18n.install.introduction.steps}}
        </ol>
        <div class="form-actions">
            <button class="btn btn-primary next">{{i18n.common.next}}</button>
        </div>
    </div>
</div>
<div class="step hide">
    <h2 class="subtitle">{{i18n.install.steps.0.title}}</h2>
    <h3>Création de la structure de la base de données</h3>
    <h3>Renseignement des informations relative à la base de données pour votre application debox</h3>
    <div class="block">
        <form id="datasource" class="form-horizontal" action="datasource" method="post">
            <div class="control-group">
                <label class="control-label large" for="host">{{i18n.install.steps.0.form.host.label}}</label>
                <div class="controls">
                    <input type="text" id="host" name="host" class="span4" placeholder="{{i18n.install.steps.0.form.host.default}}" value="127.0.0.1" >
                </div>
            </div>
            <div class="control-group">
                <label class="control-label large" for="port">{{i18n.install.steps.0.form.port.label}}</label>
                <div class="controls">
                    <input type="text" id="port" name="port" class="span4" placeholder="{{i18n.install.steps.0.form.port.default}}" value="3306" >
                </div>
            </div>
            <div class="control-group">
                <label class="control-label large" for="name">{{i18n.install.steps.0.form.name.label}}</label>
                <div class="controls">
                    <input type="text" id="name" name="name" class="span4" placeholder="{{i18n.install.steps.0.form.name.default}}" value="debox" >
                </div>
            </div>
            <div class="control-group">
                <label class="control-label large" for="username">{{i18n.install.steps.0.form.username.label}}</label>
                <div class="controls">
                    <input type="text" id="username" name="username" class="span4" placeholder="{{i18n.install.steps.0.form.username.default}}" value="root">
                </div>
            </div>
            <div class="control-group">
                <label class="control-label large" for="password">{{i18n.install.steps.0.form.password.label}}</label>
                <div class="controls">
                    <input type="password" id="password" name="password" class="span4" placeholder="{{i18n.install.steps.0.form.password.default}}">
                </div>
            </div>
            <div class="form-actions">
                <input type="submit" class="btn btn-primary databaseValidation" value="{{i18n.install.steps.0.form.next}}" />
            </div>
        </form>
    </div>
</div>
<div class="step hide">
    <h2 class="subtitle">{{i18n.install.steps.1.title}}</h2>
    <div class="block">
        <p>{{i18n.install.steps.1.introduction}}</p>
        <form id="workingDirectory" class="form-horizontal" action="datasource" method="post">
            <div class="control-group">
                <label class="control-label large" for="path">{{i18n.install.steps.1.path.label}}</label>
                <div class="controls">
                    <input type="text" id="path" name="path" class="span6" >
                </div>
            </div>
            <div class="form-actions">
                <input type="submit" class="btn btn-primary workingDirectoryValidation" value="{{i18n.install.steps.1.next}}" />
            </div>
        </form>
    </div>
</div>
