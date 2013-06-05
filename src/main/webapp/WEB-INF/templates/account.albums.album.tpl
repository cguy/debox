<a id="{{id}}" href="#/albums/{{id}}/edition" class="album admin thumbnail" style="background-image:url('{{coverUrl}}')">
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
        {{#publicAlbum}}
        <li class="public">
            <i class="icon-ok"></i>&nbsp;{{i18n.common.public}}
        </li>
        {{/publicAlbum}}
        {{^publicAlbum}}
        <li class="private">
            <i class="icon-ban-circle"></i>&nbsp;{{i18n.common.private}}
        </li>
        {{/publicAlbum}}
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
        {{#publicAlbum}}
        <button class="btn btn-small private" data-loading-text="{{i18n.common.modification_in_progress}}">
            <i class="icon-ban-circle"></i>&nbsp;{{i18n.account.albums.make_private}}
        </button>
        {{/publicAlbum}}
        {{^publicAlbum}}
        <button class="btn btn-small public" data-loading-text="{{i18n.common.modification_in_progress}}">
            <i class="icon-ok"></i>&nbsp;{{i18n.account.albums.make_public}}
        </button>
        {{/publicAlbum}}
    </div>
</a>
