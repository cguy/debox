<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> 
    <span class="icon-bar"></span> 
    <span class="icon-bar"></span> 
    <span class="icon-bar"></span> 
</a>

{{#title}}<a class="brand" href="#/">{{title}}</a>{{/title}}

<div class="nav-collapse">
    <ul class="nav">
        <li><a href="#/"><i class="icon-home icon-white"></i>&nbsp;&nbsp;{{i18n.header.album_list}}</a></li>
        {{#isAdmin}}
        <li><a href="#/administration"><i class="icon-cogs icon-white"></i>&nbsp;&nbsp;{{i18n.header.administration}}</a></li>
        {{/isAdmin}}
    </ul>
    <ul class="nav pull-right about">
        <li>
            <a href="#/about" rel="tooltip" data-placement="bottom" title="{{i18n.about.tooltip}}">
                <i class="icon-question-sign"></i>
            </a>
        </li>
    </ul>
    <p class="navbar-text pull-right">
    {{#username}}
        <i class="icon-user icon-white"></i>&nbsp;&nbsp;<strong>{{username}}</strong> (<a href="logout">{{i18n.header.disconnection}}</a>)
    {{/username}}
    {{^username}}
        <a href="#authentication-form" data-toggle="modal">{{i18n.header.connection}}</a>
    {{/username}}
    </p>
</div>