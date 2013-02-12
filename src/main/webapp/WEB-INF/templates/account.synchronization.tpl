<h2 class="subtitle">{{i18n.account.sync.title}}</h2>

<form id="synchronization" class="form-vertical block" action="#/administration/sync" method="post">
    <div id="sync-progress" class="alert alert-info hide">
        <h3 class="alert-heading" style="line-height:50px;">
            <span id="progress-label">{{i18n.account.sync.in_progress}}&hellip;</span>&nbsp;<span id="progress-percentage"></span>
            <button id="cancel-sync" class="btn btn-warning pull-right" style="margin: 5px -20px 0 20px;">{{i18n.account.sync.cancel}}</button>
        </h3>
        <div class="progress progress-info progress-striped active" style="margin:0 -20px 10px 0">
            <div class="bar"></div>
        </div>
    </div>
    
    <div class="control-group">
        <p class="error"></p>
        <div class="control-group">
            <label class="control-label">{{i18n.account.sync.choice_mode}}:</label>
            <div class="controls">
                <label class="radio">
                    <input type="radio" name="mode" value="fast" /><strong>{{i18n.account.sync.fastest}}:</strong> {{i18n.account.sync.fastest_description}}
                </label>
                <label class="radio">
                    <input type="radio" name="mode" value="normal" checked /><strong>{{i18n.account.sync.normal}}:</strong> {{i18n.account.sync.normal_description}}
                </label>
                <label class="radio">
                    <input type="radio" name="mode" value="slow" /><strong>{{i18n.account.sync.longest}}:</strong> {{i18n.account.sync.longest_description}}
                </label>
            </div>
            <div class="controls">
                <label class="checkbox">
                    <input type="checkbox" name="forceCheckDates"/><em>{{i18n.account.sync.force_check_dates}}</em>
                </label>
            </div>
            <div class="alert alert-warning"><span class="label label-warning">{{i18n.common.warning}}</span>&nbsp;{{i18n.account.sync.warning}}</div>
        </div>
    </div>
    <div class="form-actions">
        <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.administration.processing}}" value="{{i18n.account.sync.launch}}" />
    </div>
</form>
