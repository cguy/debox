<h2 class="subtitle">{{i18n.account.personaldata.title}}</h2>

<form id="personaldata" class="form-horizontal row block" action="#/accounts/{{id}}" method="post">
    <h3>{{i18n.account.personaldata.edit}}</h3>
    <p class="hide"><a class="close">&times;</a></p>

    <div class="row">
        <div class="span5">
            <div class="control-group">
                <label class="control-label" for="username">{{i18n.account.personaldata.username}}</label>
                <div class="controls">
                    <input type="text" required class="span3" id="username" name="username" value="{{username}}" />
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="span5">
            <div class="control-group">
                <label class="control-label" for="firstname">{{i18n.account.personaldata.firstname}}</label>
                <div class="controls">
                    <input type="text" class="span3" id="firstname" name="firstname" value="{{firstName}}" />
                </div>
            </div>
        </div>
        <div class="span5">
            <div class="control-group">
                <label class="control-label" for="lastname">{{i18n.account.personaldata.lastname}}</label>
                <div class="controls">
                    <input type="text" class="span3" id="lastname" name="lastname" value="{{lastName}}" />
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="form-actions">
            <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.common.modification_in_progress}}" value="{{i18n.common.validate}}" />
        </div>
    </div>
</form>


<form id="credentials" class="form-horizontal row block" action="#/accounts/{{id}}/credentials" method="post">
    <h3>{{i18n.account.personaldata.passwordChange}}</h3>
    <p class="hide"><a class="close">&times;</a></p>

    <div class="row">
        <div class="span5">
            <div class="control-group">
                <label class="control-label" for="oldPassword">{{i18n.account.personaldata.old_password}}</label>
                <div class="controls">
                    <input type="password" class="span3" id="oldPassword" name="oldPassword" />
                </div>
            </div>
        </div>
        <div class="span5">
            <div class="control-group">
                <label class="control-label" for="password">{{i18n.account.personaldata.new_password}}</label>
                <div class="controls">
                    <input type="password" class="span3" id="password" name="password" />
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="form-actions">
            <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.common.modification_in_progress}}" value="{{i18n.common.validate}}" />
        </div>
    </div>
</form>


<form id="delete-account" class="form-horizontal row block" action="#/accounts/{{id}}/delete" method="get">
    <h3>{{i18n.account.personaldata.accountDeletion.title}}</h3>
    <p class="hide"><a class="close">&times;</a></p>
    <p>{{i18n.account.personaldata.accountDeletion.message}}</p>
    <div class="row">
        <div class="form-actions">
            <input type="submit" class="btn btn-danger" data-loading-text="{{i18n.common.modification_in_progress}}" value="{{i18n.common.delete}}" />
        </div>
    </div>
</form>

{{! =================================== }}
{{! POPUP MODAL - DELETE A USER ACCOUNT }}
{{! =================================== }}
<form id="delete-account-confirm" class="form-horizontal modal fade hide" action="accounts/{{id}}/delete" method="post">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>{{i18n.account.personaldata.accountDeletion.title}}</h3>
    </div>
    <div class="modal-body">
        <p></p>
        <p>{{i18n.account.personaldata.accountDeletion.message}}</p>
        <p class="alert warning"><span class="label label-warning">{{i18n.common.warning}}</span> {{i18n.account.personaldata.accountDeletion.irreversible}}</p>
    </div>
    <div class="modal-footer">
        <input type="submit" class="btn btn-danger" value="{{i18n.common.delete}}" />
        <button type="reset" data-dismiss="modal" class="btn">{{i18n.common.cancel}}</button>
    </div>
</form>
