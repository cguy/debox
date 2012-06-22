pouet
{{#files}}
    <tr class="template-download fade">
        {{#error}}
            <td></td>
            <td class="name"><span>{{name}}</span></td>
            <td class="size"><span>{{strSize}}</span></td>
            <td class="error" colspan="2"><span class="label label-important">{{i18n.administration.upload.error}}</span> {{i18n.administration.upload.error}}</td>
        {{/error}}
        {{^error}}
            <td class="preview">
            {{#thumbnail_url}}
                <a href="{{url}}" title="{{name}}" rel="gallery" download="{{name}}"><img src="{{thumbnail_url}}"></a>
            {{/thumbnail_url}}
            </td>
            <td class="name">
                <a href="{{url}}" title="{{name}}" rel="{{thumbnail_url}}gallery" download="{{name}}">{{name}}</a>
            </td>
            <td class="size"><span>{{strSize}}</span></td>
            <td colspan="2"></td>
        {{/error}}
        <td class="delete">
            <button class="btn btn-danger" data-type="{{delete_type}}" data-url="{{delete_url}}">
                <i class="icon-trash icon-white"></i>
                <span>{{i18n.administration.upload.remove}}</span>
            </button>
        </td>
    </tr>
{{/files}}