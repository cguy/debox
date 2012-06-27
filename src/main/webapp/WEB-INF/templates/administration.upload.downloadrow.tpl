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
                <img src="{{thumbnail_url}}">
            {{/thumbnail_url}}
            </td>
            <td class="name">
                {{name}}
            </td>
            <td class="size"><span>{{strSize}}</span></td>
            <td colspan="2"></td>
        {{/error}}
        <td class="delete">
            <button class="btn">
                <i class="icon-trash icon-white"></i>
                <span>{{i18n.common.close}}</span>
            </button>
        </td>
    </tr>
{{/files}}