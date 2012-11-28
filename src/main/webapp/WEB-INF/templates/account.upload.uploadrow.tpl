{{#files}}
<tr class="template-upload fade">
    <td class="preview"><span class="fade"></span></td>
    <td class="name"><span>{{name}}</span></td>
    <td class="size"><span>{{strSize}}</span></td>
    {{#file.error}}
    <td class="error" colspan="2"><span class="label label-important">{{i18n.account.upload.error}}</span> {{i18n.account.upload.errors[file.error] || file.error}}</td>
    {{/file.error}}
    {{^file.error}}
    <td>
        <div class="progress progress-success progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0"><div class="bar" style="width:0%;"></div></div>
    </td>
    <td class="start">
        {{^options.autoUpload}}
        <button class="btn btn-primary">
            <i class="icon-upload icon-white"></i>
            <span>{{i18n.account.upload.start}}</span>
        </button>
        {{/options.autoUpload}}
    </td>
    {{#options.autoUpload}}
    <td colspan="2"></td>
    {{/options.autoUpload}}
    <td class="cancel">
        <button class="btn btn-warning">
            <i class="icon-ban-circle icon-white"></i>
            <span>{{i18n.common.cancel}}</span>
        </button>
    </td>
    {{/file.error}}
</tr>
{{/files}}