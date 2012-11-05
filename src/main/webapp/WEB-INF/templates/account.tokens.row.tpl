<tr id="{{id}}">
    <td class="access_label">{{label}}</td>
    <td class="albums">
        <form action="#/token/{{id}}" method="post" class="album-access-form">
            <div>
                <button type="button" class="btn show-tree"><i class="icon icon-list"></i>&nbsp;{{i18n.account.tokens.visible_albums}}</button>
                <span class="hide">
                    <button type="button" class="btn cancel"><i class="icon icon-remove"></i>&nbsp;{{i18n.common.cancel}}</button>
                    <button type="submit" class="btn btn-primary validate"><i class="icon icon-ok"></i>&nbsp;{{i18n.common.validate}}</button>
                </span>
                <div class="alert alert-success hide">{{i18n.account.tokens.edit.success}}</div>
                <div class="alert alert-error hide">{{i18n.account.tokens.edit.error}}</div>
            </div>
            <div class="albums-access hide" name="albums"></div>
        </form>
    </td>
    <td class="access_link actions">
        <input type="text" class="accessShare" data-original="{{url}}" value="{{url}}" />
        <div class="alert alert-success hide">{{i18n.account.tokens.reinit.success}}</div>
        <span class="btn-group">
            <button class="btn btn-small edit"><i class="icon-pencil icon-white"></i>&nbsp;{{i18n.common.modify}}</button>
            <button href="#modal-token-reinit" data-toggle="modal" class="btn btn-small reinit"><i class="icon-refresh icon-white"></i>&nbsp;{{i18n.account.tokens.reinit.label}}</button>
        </span>
        <button href="#modal-token-delete" data-toggle="modal" class="btn btn-small btn-danger delete"><i class="icon-remove icon-white"></i>&nbsp;{{i18n.common.delete}}</button>
    </td>
</tr>
