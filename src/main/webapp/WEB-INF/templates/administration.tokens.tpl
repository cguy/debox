<h2>{{i18n.administration.tokens.title}}</h2>

<h3>{{i18n.administration.tokens.thirdparty.title}}</h3>
{{#providers}}
    {{#enabled}}<a href="{{url}}" class="btn btn-large {{id}}"><img src="static/img/{{id}}_logo.png"/>&nbsp;{{name}}</a>{{/enabled}}
    {{^enabled}}<div class="btn btn-large {{id}} disabled"><img src="static/img/{{id}}_logo.png"/>&nbsp;{{name}}</div>{{/enabled}}
{{/providers}}
{{#accounts.length}}
<h3></h3>
<table id="thirdparty_accounts" class="table table-bordered table-striped">
    <tr>
        <th>{{i18n.administration.tokens.thirdparty.provider.name}}</th>
        <th>{{i18n.administration.tokens.thirdparty.provider.identifier}}</th>
        <th class="delete">{{i18n.common.deletion}}</th>
    </tr>
    {{#accounts}}
    <tr id="{{provider.id}}-{{providerAccountId}}">
        <td>
            <img src="static/img/{{provider.id}}_logo.png"/>&nbsp;{{provider.name}}
        </td>
        <td>
            <img src="{{avatarUrl}}" />
            <a href="{{accountUrl}}">{{username}}</a>
        </td>
        <td class="delete">
            <a href="#delete-third-party-account" class="btn btn-danger btn-small delete-third-party-account" data-toggle="modal">{{i18n.administration.tokens.thirdparty.provider.deletion}}</a>
        </td>
    </tr>
    {{/accounts}}
</table>
{{/accounts.length}}

<h3>{{i18n.administration.tokens.token_list}}</h3>
<table id="administration_tokens" class="table table-striped table-bordered table-condensed{{^tokens}} hide{{/tokens}}">
    <thead>
        <tr>
            <th>{{i18n.administration.tokens.label}}</th>
            <th>{{i18n.administration.tokens.albums}}</th>
            <th style="width:120px;">{{i18n.administration.tokens.link2share}}</th>
            <th>{{i18n.administration.tokens.actions}}</th>
        </tr>
    </thead>
    <tbody>
    {{#tokens}}
        {{> administration.tokens.row}}
    {{/tokens}}
    </tbody>
</table>
<p class="alert alert-warning{{#tokens.length}} hide{{/tokens.length}}">{{i18n.administration.tokens.no_token}}</p>

<h3>{{i18n.administration.tokens.new_token}}</h3>
<form id="form-token-create" class="well form-search" action="#/token" method="put">
    <input type="text" required name="label" class="input" placeholder="{{i18n.administration.tokens.new_token_label}}"/>
    <button type="submit" class="btn btn-primary">{{i18n.administration.tokens.create_token}}</button>
</form>

{{! ========================================================== }}
{{! POPUP MODAL - EDIT A TOKEN (VISITORS ACCESS)               }}
{{! ========================================================== }}
<form id="edit_token" class="modal hide fade form-horizontal" action="#/token" method="post">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>{{i18n.administration.tokens.edit.title}}</h3>
    </div>
    <div class="modal-body">
        <p></p>
        <div class="control-group">
            <label class="control-label" for="label">{{i18n.administration.tokens.edit.label}}</label>
            <div class="controls">
                <input type="text" required class="span4" id="label" name="label" />
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <input type="hidden" name="id" />
        <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.common.modification_in_progress}}" value="{{i18n.common.modify}}" />
        <button type="reset" class="btn">{{i18n.common.cancel}}</button>
    </div>
</form>

{{! ========================================================== }}
{{! POPUP MODAL - DELETE A TOKEN (VISITORS ACCESS)             }}
{{! ========================================================== }}
<form id="modal-token-delete" class="modal hide fade form-horizontal" action="#/token" method="delete">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>{{i18n.administration.tokens.delete.title}}</h3>
    </div>
    <div class="modal-body">
        <p></p>
        <p>{{i18n.administration.tokens.delete.message}} <strong></strong>?</p>
    </div>
    <div class="modal-footer">
        <input type="hidden" name="id" />
        <input type="submit" class="btn btn-danger" data-loading-text="{{i18n.common.deletion_in_progress}}" value="{{i18n.common.delete}}" />
        <button type="reset" class="btn">{{i18n.common.cancel}}</button>
    </div>
</form>

{{! ========================================================== }}
{{! POPUP MODAL - REINIT A TOKEN (VISITORS ACCESS)             }}
{{! ========================================================== }}
<form id="modal-token-reinit" class="modal hide fade form-horizontal" action="#/token/reinit" method="post">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>{{i18n.administration.tokens.reinit.title}}</h3>
    </div>
    <div class="modal-body">
        <p></p>
        <p>{{i18n.administration.tokens.reinit.message}} <strong></strong>?</p>
        <p>{{i18n.administration.tokens.reinit.description}}</p>
    </div>
    <div class="modal-footer">
        <input type="hidden" name="id" />
        <input type="submit" class="btn btn-warning" data-loading-text="{{i18n.administration.tokens.reinit.label_in_progress}}" value="{{i18n.administration.tokens.reinit.label}}" />
        <button type="reset" class="btn">{{i18n.common.cancel}}</button>
    </div>
</form>

{{! ========================================================== }}
{{! POPUP MODAL - DELETE A THIRD PARTY ACCOUNT                 }}
{{! ========================================================== }}
<form id="delete-third-party-account" class="modal hide fade form-horizontal" action="#/third-party-account" method="delete">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>{{i18n.administration.tokens.thirdparty.provider.remove.title}}</h3>
    </div>
    <div class="modal-body">
        <p>{{i18n.administration.tokens.thirdparty.provider.remove.message}}</p>
    </div>
    <div class="modal-footer">
        <input type="hidden" name="id" class="third-party-account-id" />
        <input type="submit" class="btn btn-danger" data-loading-text="{{i18n.common.deletion_in_progress}}" value="{{i18n.common.delete}}" />
        <button type="reset" class="btn">{{i18n.common.cancel}}</button>
    </div>
</form>
