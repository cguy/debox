<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> 
    <span class="icon-bar"></span> 
    <span class="icon-bar"></span> 
    <span class="icon-bar"></span> 
</a>

{{#title}}<a class="brand" href="#/">{{title}}</a>{{/title}}

<div class="nav-collapse">
    <ul class="nav">
        <li><a href="#/"><i class="icon-home"></i>{{i18n.header.album_list}}</a></li>
        {{#administrator}}
        <li><a href="#/administration"><i class="icon-cog"></i>{{i18n.header.administration}}</a></li>
        {{/administrator}}
    </ul>
    <ul class="nav pull-right about">
        <li>
            <a href="#/about" rel="tooltip" data-placement="left" title="{{i18n.about.tooltip}}">
                <i class="icon-question-sign"></i>
            </a>
        </li>
    </ul>
    {{#username}}
    <ul class="nav pull-right">
        <li><a href="logout"><i class="icon-signout"></i>{{i18n.header.disconnection}}</a></li>
    </ul>
        <p class="navbar-text pull-right"><i class="icon-user"></i><strong>{{username}}</strong></p>
    {{/username}}
    {{^username}}
    <ul class="nav pull-right">
        <li><a href="#/sign-in"><i class="icon-signin"></i>{{i18n.header.connection}}</a></li>
        <li><a href="#/register"><i class="icon-user"></i>&nbsp;{{i18n.header.register}}</a></li>
    </ul>
    {{/username}}
</div>