<a id="{{id}}" href="#/album/{{id}}/edition" class="album admin thumbnail" style="background-image:url('{{coverUrl}}')">
    <ul class="unstyled">
        <li title="{{name}}"><strong>{{name}}</strong></li>
        <li>{{beginDate}}</li>
        <li>{{i18n.account.albums.photo_number}}: {{photosCount}}</li>
        {{#downloadable}}
        <li class="downloadable">
            <i class="icon-download-alt"></i>&nbsp;{{i18n.account.albums.downloadable}}
        </li>
        {{/downloadable}}
        {{^downloadable}}
        <li class="undownloadable">
            <i class="icon-download-alt"></i>&nbsp;{{i18n.account.albums.not_downloadable}}
        </li>
        {{/downloadable}}
        {{#public}}
        <li class="public">
            <i class="icon-ok"></i>&nbsp;{{i18n.common.public}}
        </li>
        {{/public}}
        {{^public}}
        <li class="private">
            <i class="icon-ban-circle"></i>&nbsp;{{i18n.common.private}}
        </li>
        {{/public}}
    </ul>
    <div class="btn-group-vertical">
        {{#downloadable}}
        <button class="btn btn-small undownloadable" data-loading-text="{{i18n.common.modification_in_progress}}">
            <i class="icon-download-alt"></i>&nbsp;{{i18n.account.albums.make_undownloadable}}
        </button>
        {{/downloadable}}
        {{^downloadable}}
        <button class="btn btn-small downloadable" data-loading-text="{{i18n.common.modification_in_progress}}">
            <i class="icon-download-alt"></i>&nbsp;{{i18n.account.albums.make_downloadable}}
        </button>
        {{/downloadable}}
        {{#public}}
        <button class="btn btn-small private" data-loading-text="{{i18n.common.modification_in_progress}}">
            <i class="icon-ban-circle"></i>&nbsp;{{i18n.account.albums.make_private}}
        </button>
        {{/public}}
        {{^public}}
        <button class="btn btn-small public" data-loading-text="{{i18n.common.modification_in_progress}}">
            <i class="icon-ok"></i>&nbsp;{{i18n.account.albums.make_public}}
        </button>
        {{/public}}
    </div>
</a>
