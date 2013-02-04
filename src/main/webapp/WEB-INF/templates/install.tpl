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
    <div class="block">
        <form id="datasource" class="form-horizontal" action="datasource" method="post">
            <div class="control-group">
                <label class="control-label large" for="host">{{i18n.install.steps.0.form.type.label}}</label>
                <div class="controls">
                    <select name="type" id="database-type">
                        <option value="mysql" data-jdbc="mysql://127.0.0.1:3306/debox" selected>MySQL</option>
                        <option value="h2" data-jdbc="h2:file:~/.debox/database">H2</option>
                        <option value="postgresql" data-jdbc="postgresql://127.0.0.1:5432/debox">PostgreSQL</option>
                    </select>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label large" for="jdbc">{{i18n.install.steps.0.form.jdbc.label}}</label>
                <div class="input-prepend">
                    <span class="add-on">jdbc:</span><input type="text" id="jdbc" name="url" class="span4" placeholder="{{i18n.install.steps.0.form.host.default}}" value="127.0.0.1" >
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
        <p class="alert">{{i18n.install.steps.1.note}}</p>
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
<div class="step hide">
    <h2 class="subtitle">{{i18n.install.steps.2.title}}</h2>
    <div class="block">
        <p>{{i18n.install.steps.2.introduction}}</p>
        <form id="account" class="form-horizontal" action="datasource" method="post">
            <div class="control-group">
                <label class="control-label large" for="firstname">{{i18n.install.steps.2.form.firstname.label}}</label>
                <div class="controls">
                    <input type="text" id="firstname" name="firstname" class="span4" placeholder="{{i18n.install.steps.2.form.firstname.placeholder}}">
                </div>
            </div>
            <div class="control-group">
                <label class="control-label large" for="lastname">{{i18n.install.steps.2.form.lastname.label}}</label>
                <div class="controls">
                    <input type="text" id="lastname" name="lastname" class="span4" placeholder="{{i18n.install.steps.2.form.lastname.placeholder}}">
                </div>
            </div>
            <div class="control-group">
                <label class="control-label large" for="account_username">{{i18n.install.steps.2.form.username.label}}</label>
                <div class="controls">
                    <input type="email" id="account_username" name="username" class="span6" placeholder="{{i18n.install.steps.2.form.username.placeholder}}">
                </div>
            </div>
            <div class="control-group">
                <label class="control-label large" for="account_password">{{i18n.install.steps.2.form.password.label}}</label>
                <div class="controls">
                    <input type="password" id="account_password" name="password" class="span6" placeholder="{{i18n.install.steps.2.form.password.placeholder}}">
                </div>
            </div>
            <div class="control-group">
                <label class="control-label large" for="account_confirm">{{i18n.install.steps.2.form.confirm.label}}</label>
                <div class="controls">
                    <input type="password" id="account_confirm" name="confirm" class="span6" placeholder="{{i18n.install.steps.2.form.confirm.placeholder}}">
                </div>
            </div>
            <div class="form-actions">
                <input type="submit" class="btn btn-primary databaseValidation" value="{{i18n.install.steps.2.form.next}}" />
            </div>
        </form>
    </div>
</div>
<div class="step hide">
    <h2 class="subtitle">{{i18n.install.steps.3.title}}</h2>
    <div class="block">
        <p>{{i18n.install.steps.3.introduction}}</p>
        <form id="authenticate" class="form-horizontal" action="authenticate" method="post">
            <p>
                <input type="hidden" name="username">
            </p>
            <p>
                <input type="hidden" name="password">
            </p>
            <div class="form-actions">
                <input type="submit" class="btn btn-primary" value="{{i18n.install.steps.3.go}}" />
            </div>
        </form>
    </div>
</div>
