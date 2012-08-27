<form class="form-vertical" action="#/administration/sync" method="post">
    <h2>{{i18n.administration.sync.title}}</h2>
    <div class="control-group">
        <p class="error"></p>
        <div class="control-group">
            <label class="control-label">{{i18n.administration.sync.choice_mode}}:</label>
            <div class="controls">
                <label class="radio">
                    <input type="radio" name="mode" value="fast" /><strong>{{i18n.administration.sync.fastest}}:</strong> {{i18n.administration.sync.fastest_description}}
                </label>
                <label class="radio">
                    <input type="radio" name="mode" value="normal" checked /><strong>{{i18n.administration.sync.normal}}:</strong> {{i18n.administration.sync.normal_description}}
                </label>
                <label class="radio">
                    <input type="radio" name="mode" value="slow" /><strong>{{i18n.administration.sync.longest}}:</strong> {{i18n.administration.sync.longest_description}}
                </label>
            </div>
            <div class="controls">
                <label class="checkbox">
                    <input type="checkbox" name="forceCheckDates"/><em>{{i18n.administration.sync.force_check_dates}}</em>
                </label>
            </div>
            <div class="alert alert-warning"><span class="label label-warning">{{i18n.common.warning}}</span>&nbsp;{{i18n.administration.sync.warning}}</div>
        </div>
    </div>
    <div class="form-actions">
        <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.administration.processing}}" value="{{i18n.administration.sync.launch}}" />
    </div>
</form>