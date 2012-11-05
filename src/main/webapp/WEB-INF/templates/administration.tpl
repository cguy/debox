<div class="page-header"><h1>{{i18n.administration.title}}</h1></div>

<div class="account navbar navbar-static-top">
    <div class="navbar-inner">
        <ul class="nav">
            <li class="configuration"><a href="#/administration/configuration"><i class="icon-cogs"></i>{{i18n.administration.config.tab}}</a></li>
            <li class="synchronization"><a href="#/administration/synchronization"><i class="icon-refresh"></i>{{i18n.administration.sync.tab}}</a></li>
        </ul>
    </div>
</div>

<div id="sync-progress" class="alert alert-info hide">
    <h3 class="alert-heading" style="line-height:50px;">
        <span id="progress-label">{{i18n.administration.sync.in_progress}}&hellip;</span>&nbsp;<span id="progress-percentage"></span>
        <button id="cancel-sync" class="btn btn-warning pull-right" style="margin: 5px -20px 0 20px;">{{i18n.administration.sync.cancel}}</button>
    </h3>
    <div class="progress progress-info progress-striped active" style="margin:0 -20px 10px 0">
        <div class="bar"></div>
    </div>
</div>

<div id="administration"></div>
