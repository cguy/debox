{{#title}}<a class="brand" href="#/">{{title}}</a>{{/title}}

<ul class="account">
{{#username}}
    <li class="username"><i class="icon-user"></i><strong>{{username}}</strong></li>
    <li><a href="logout"><i class="icon-signout"></i>{{i18n.header.disconnection}}</a></li>
{{/username}}
{{^username}}{{^isAnonymousUser}}
    <li><a href="#/sign-in"><i class="icon-signin"></i>{{i18n.header.connection}}</a></li>
    <li><a href="#/register"><i class="icon-user"></i>&nbsp;{{i18n.header.register}}</a></li>
{{/isAnonymousUser}}{{/username}}
</ul>

<ul class="items">
    <li><a href="#/"><i class="icon-home"></i>{{i18n.header.album_list}}</a></li>
</ul>

<ul>
    {{#authenticated}}
    <li class="upload"><a href="#/account/upload"><i class="icon-upload-alt"></i>{{i18n.account.upload.tab}}</a></li>
    <li class="albums"><a href="#/account/albums"><i class="icon-camera-retro"></i>{{i18n.account.albums.tab}}</a></li>
    <li class="comments"><a href="#/account/comments"><i class="icon-comment"></i>{{i18n.account.comments.tab}}</a></li>
    <li class="tokens"><a href="#/account/tokens"><i class="icon-user"></i>{{i18n.account.tokens.tab}}</a></li>
    <li class="synchronization"><a href="#/account/synchronization"><i class="icon-refresh"></i>{{i18n.account.sync.tab}}</a></li>
    <li class="personaldata"><a href="#/account/personaldata"><i class="icon-info-sign"></i>{{i18n.account.personaldata.tab}}</a></li>
    <li class="settings"><a href="#/account/settings"><i class="icon-cog"></i>{{i18n.account.settings.tab}}</a></li>
    {{/authenticated}}
    {{#administrator}}
    <li><a href="#/administration"><i class="icon-cog"></i>{{i18n.header.administration}}</a></li>
    {{/administrator}}
</ul>

<!-- About link -->
<a class="about" href="#/about">
    <i class="icon-question-sign"></i>&nbsp;{{i18n.about.tooltip}}
</a>