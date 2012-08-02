{{#hasProviders}}
<div class="thirdparty">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>
        <h3>{{i18n.header.connection_others}}</h3>
    </div>
    <div class="modal-body">
        {{#providers}}
            {{#enabled}}<a href="{{url}}" class="btn btn-large {{id}}"><img src="static/img/{{id}}_logo.png"/>&nbsp;{{name}}</a>{{/enabled}}
            {{^enabled}}<div class="btn btn-large {{id}} disabled"><img src="static/img/{{id}}_logo.png"/>&nbsp;{{name}}</div>{{/enabled}}
        {{/providers}}
    </div>
</div>
{{/hasProviders}}
<div class="classic">
    <div class="modal-header">
        <h3>{{i18n.header.connection}}</h3>
    </div>
    <div class="modal-body">
        <div class="control-group">
            <label class="control-label" for="username">{{i18n.header.username}}</label>
            <div class="controls">
                <input type="text" required id="username" name="username" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="password">{{i18n.header.password}}</label>
            <div class="controls">
                <input type="password" required id="password" name="password" />
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <input id="connect" type="submit" class="btn btn-primary" data-loading-text="{{i18n.header.connection_in_progress}}" value="{{i18n.header.connection}}" />
    </div>
</div>
    