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
            <a href="#/administration/synchronization" data-target="#synchronization"><i class="icon-refresh"></i>&nbsp;{{i18n.administration.sync.tab}}</a>
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
        <div id="configuration" class="tab-pane active"></div>
        <div id="synchronization" class="tab-pane"></div>
        <div id="albums" class="tab-pane"></div>
        <div id="tokens" class="tab-pane"></div>
        <div id="account" class="tab-pane"></div>
    </div>

</div>
