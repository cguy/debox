<h1 class="page-header">{{i18n.administration.title}}</h1>

<div id="administration">
    <div id="sync-progress" class="alert alert-info hide">
        <h3 class="alert-heading" style="line-height:50px;">
            <span id="progress-label">{{i18n.administration.sync.in_progress}}&hellip;</span>&nbsp;<span id="progress-percentage"></span>
            <button id="cancel-sync" class="btn btn-warning pull-right" style="margin: 5px -20px 0 20px;">{{i18n.administration.sync.cancel}}</button>
        </h3>
        <div class="progress progress-info progress-striped active" style="margin:0 -20px 10px 0">
            <div class="bar"></div>
        </div>
    </div>

    <ul class="nav nav-tabs">
        <li class="active">
            <a href="#/administration/configuration" data-target="#configuration"><i class="icon-cogs"></i>&nbsp;{{i18n.administration.config.tab}}</a>
        </li>
        <li>
            <a href="#/administration/sync" data-target="#sync"><i class="icon-refresh"></i>&nbsp;{{i18n.administration.sync.tab}}</a>
        </li>
        <li>
            <a href="#/administration/albums" data-target="#albums"><i class="icon-camera-retro"></i>&nbsp;{{i18n.administration.albums.tab}}</a>
        </li>
        <li>
            <a href="#/administration/tokens" data-target="#tokens"><i class="icon-user"></i>&nbsp;{{i18n.administration.tokens.tab}}</a>
        </li>
        <li>
            <a href="#/administration/account" data-target="#account"><i class="icon-pencil"></i>&nbsp;{{i18n.administration.account.tab}}</a>
        </li>   
    </ul>

    <div class="tab-content">

        {{! Configuration form }}
        <div id="configuration" class="tab-pane active">
            <form  class="form-vertical" action="#/administration/configuration" method="post">
                <h2 class="page-header">{{i18n.administration.config.title}}</h2>
                <p class="alert hide"></p>
                <div class="control-group">
                    <label for="title">{{i18n.administration.config.galery_title}}</label>
                    <div class="controls">
                        <input class="span5" type="text" required id="title" name="title" placeholder="{{i18n.administration.config.galery_title_placeholder}}" value="{{data.configuration.title}}" />
                    </div>
                </div>
                <div class="control-group">
                    <label for="sourceDirectory">{{i18n.administration.config.source_directory}}:</label>
                    <div class="controls">
                        <input class="span5" type="text" required id="sourceDirectory" name="sourceDirectory" placeholder="{{i18n.administration.config.source_directory_placeholder}}" value="{{data.configuration.source_path}}" />
                    </div>
                </div>
                <div class="control-group">
                    <label for="targetDirectory">{{i18n.administration.config.target_directory}}:</label>
                    <div class="controls">
                        <input class="span5" type="text" required id="targetDirectory" name="targetDirectory" placeholder="{{i18n.administration.config.target_directory_placeholder}}" value="{{data.configuration.target_path}}" />
                    </div>
                </div>
                <div class="form-actions">
                    <input type="hidden" name="force" />
                    <button type="button" class="btn btn-danger" data-loading-text="{{i18n.administration.processing}}">{{i18n.administration.config.save_and_sync}}</button>
                    <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.administration.processing}}" value="{{i18n.administration.config.save}}" />
                </div>
            </form>
        </div>
        {{! End of configuration form }}

        {{! Synchronization form }}
        <div id="sync" class="tab-pane">
            <form class="form-vertical" action="#/administration/sync" method="post">
                <h2 class="page-header">{{i18n.administration.sync.title}}</h2>
                <div class="control-group">
                    <p class="error"></p>
                    <div class="control-group">
                        <label class="control-label">{{i18n.administration.sync.choice_mode}}:</label>
                        <div class="controls">
                            <label class="radio"><input type="radio" name="mode" value="fast" /><strong>{{i18n.administration.sync.fastest}}:</strong> {{i18n.administration.sync.fastest_description}}</label>
                            <label class="radio"><input type="radio" name="mode" value="normal" checked /><strong>{{i18n.administration.sync.normal}}:</strong> {{i18n.administration.sync.normal_description}}</label>
                            <label class="radio">
                                <input type="radio" name="mode" value="slow" /><strong>{{i18n.administration.sync.longest}}:</strong> {{i18n.administration.sync.longest_description}}
                            </label>
                            <div class="alert alert-warning"><span class="label label-warning">{{i18n.common.warning}}</span>&nbsp;{{i18n.administration.sync.warning}}</div>
                        </div>
                    </div>
                </div>
                <div class="form-actions">
                    <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.administration.processing}}" value="{{i18n.administration.sync.launch}}" />
                </div>
            </form>
        </div>
        {{! End of synchronization form }}

        {{! Albums }}
        <div id="albums" class="tab-pane">
            {{> admin.albums}}
        </div>
        {{! End of albums }}

        {{! Tokens }}
        <div id="tokens" class="tab-pane">
            <h2 class="page-header">{{i18n.administration.tokens.title}}</h2>
            <h3>{{i18n.administration.tokens.token_list}}</h3>
            <table id="administration_tokens" class="table table-striped table-bordered table-condensed{{^data.tokens}} hide{{/data.tokens}}">
                <thead>
                    <tr>
                        <th>{{i18n.administration.tokens.label}}</th>
                        <th>{{i18n.administration.tokens.albums}}</th>
                        <th style="width:120px;">{{i18n.administration.tokens.link2share}}</th>
                        <th style="width:185px;">{{i18n.administration.tokens.actions}}</th>
                    </tr>
                </thead>
                <tbody>
                {{#data.tokens}}
                    <tr id="{{id}}">
                        <td class="access_label">{{label}}</td>
                        <td class="albums">
                            <form action="#/token/{{id}}" method="post" class="album-access-form">
                                <div>
                                    <button type="button" class="btn show-tree"><i class="icon icon-list"></i>&nbsp;{{i18n.administration.tokens.visible_albums}}</button>
                                    <span class="hide">
                                        <button type="button" class="btn cancel"><i class="icon icon-remove"></i>&nbsp;{{i18n.common.cancel}}</button>
                                        <button type="submit" class="btn btn-primary validate"><i class="icon icon-ok"></i>&nbsp;{{i18n.common.validate}}</button>
                                    </span>
                                    <div class="alert alert-success hide">{{i18n.administration.tokens.edit.success}}</div>
                                    <div class="alert alert-error hide">{{i18n.administration.tokens.edit.error}}</div>
                                </div>
                                <div class="albums-access hide" name="albums"></div>
                            </form>
                        </td>
                        <td><a href="{{id}}#/"><i class="icon icon-share"></i>&nbsp;{{i18n.administration.tokens.link}}</a></td>
                        <td>
                            <span>
                                <button class="btn btn-info edit"><i class="icon-pencil icon-white"></i>&nbsp;{{i18n.common.modify}}</button>
                                <button href="#modal-token-delete" data-toggle="modal" class="btn btn-danger delete"><i class="icon-remove icon-white"></i>&nbsp;{{i18n.common.delete}}</button>
                            </span>
                        </td>
                    </tr>
                {{/data.tokens}}
                </tbody>
            </table>
            <p class="alert alert-warning{{#data.tokens.length}} hide{{/data.tokens.length}}">{{i18n.administration.tokens.no_token}}</p>

            <h3>{{i18n.administration.tokens.new_token}}</h3>
            <form id="form-token-create" class="well form-search" action="#/token" method="put">
                <input type="text" required name="label" class="input" placeholder="{{i18n.administration.tokens.new_token_label}}"/>
                <button type="submit" class="btn btn-primary">{{i18n.administration.tokens.create_token}}</button>
            </form>
        </div>
        {{! End of tokens }}

        {{! Account information form }}
        <div id="account" class="tab-pane">
            <form class="form-horizontal" action="#/administration/credentials" method="post">
                <h2 class="page-header">{{i18n.administration.account.title}}</h2>
                <p class="hide"><a class="close">&times;</a></p>
                <div class="control-group">
                    <label class="control-label" for="username">{{i18n.administration.account.username}}:</label>
                    <div class="controls">
                        <input type="text" required class="input-large" id="username" name="username" value="{{data.username}}" />
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
        </div>
        {{! End of account information form}}
    </div>
    {{! End of tabs}}

</div>
{{! End of administration block }}

{{! ========================================================== }}
{{! POPUP MODAL - EDIT A TOKEN (VISITORS ACCESS) }}
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
{{! POPUP MODAL - DELETE A TOKEN (VISITORS ACCESS) }}
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