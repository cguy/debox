<div>
    <ul class="nav nav-tabs nav-stacked menu account">
        <!--<li class="dashboard"><a href="#/account/dashboard"><i class="icon-dashboard"></i>{{i18n.account.dashboard.tab}}</a></li>-->
        <li class="upload"><a href="#/account/upload"><i class="icon-upload-alt"></i>{{i18n.account.upload.tab}}</a></li>
        <li class="albums"><a href="#/account/albums"><i class="icon-camera-retro"></i>{{i18n.account.albums.tab}}</a></li>
        <li class="comments"><a href="#/account/comments"><i class="icon-comment"></i>{{i18n.account.comments.tab}}</a></li>
        <li class="tokens"><a href="#/account/tokens"><i class="icon-user"></i>{{i18n.account.tokens.tab}}</a></li>
        <li class="synchronization"><a href="#/account/synchronization"><i class="icon-refresh"></i>{{i18n.account.sync.tab}}</a></li>
        <li class="personaldata"><a href="#/account/personaldata"><i class="icon-info-sign"></i>{{i18n.account.personaldata.tab}}</a></li>
        <li class="settings"><a href="#/account/settings"><i class="icon-cog"></i>{{i18n.account.settings.tab}}</a></li>
    </ul>

    <div id="sync-progress" class="alert alert-info hide">
        <h3 class="alert-heading" style="line-height:50px;">
            <span id="progress-label">{{i18n.account.sync.in_progress}}&hellip;</span>&nbsp;<span id="progress-percentage"></span>
            <button id="cancel-sync" class="btn btn-warning pull-right" style="margin: 5px -20px 0 20px;">{{i18n.account.sync.cancel}}</button>
        </h3>
        <div class="progress progress-info progress-striped active" style="margin:0 -20px 10px 0">
            <div class="bar"></div>
        </div>
    </div>

    <div id="account"></div>
</div>
<!--<div class="account navbar navbar-static-top">
    <div class="navbar-inner">
        <ul class="nav">
            <li class="personaldata"><a href="#/account"><i class="icon-info-sign"></i>{{i18n.account.personaldata.tab}}</a></li>
            <li class="settings"><a href="#/account/settings"><i class="icon-cog"></i>{{i18n.account.settings.tab}}</a></li>
            <li class="upload"><a href="#/account/upload"><i class="icon-upload-alt"></i>{{i18n.account.upload.tab}}</a></li>
            <li class="synchronization"><a href="#/account/synchronization"><i class="icon-refresh"></i>{{i18n.account.sync.tab}}</a></li>
            <li class="albums"><a href="#/account/albums"><i class="icon-camera-retro"></i>{{i18n.account.albums.tab}}</a></li>
            <li class="tokens"><a href="#/account/tokens"><i class="icon-user"></i>{{i18n.account.tokens.tab}}</a></li>
        </ul>
    </div>
</div>-->

