<tr id="{{id}}">
    <td class="access_label">{{label}}</td>
    <td class="albums">
        <form action="#/token/{{id}}" method="post" class="album-access-form">
            <div>
                <button type="button" class="btn show-tree"><i class="icon icon-list"></i>&nbsp;{{i18n.administration.tokens.visible_albums}}</button>
                <span class="hide">
                    <button type="button" class="btn cancel"><i class="icon icon-remove"></i>&nbsp;{{i18n.common.cancel}}</button>
                    <button type="submit" class="btn btn-primary validate"><i class="icon icon-ok"></i>&nbsp;{{i18n.common.validate}}</button>
                </span>
                <div class="alert alert-success hide">{{i18n.administration.tokens.edit.success}}</div>
                <div class="alert alert-error hide">{{i18n.administration.tokens.edit.error}}</div>
            </div>
            <div class="albums-access hide" name="albums"></div>
        </form>
    </td>
    <td><a href="{{id}}#/"><i class="icon icon-share"></i>&nbsp;{{i18n.administration.tokens.link}}</a></td>
    <td>
        <span>
            <button class="btn btn-info edit"><i class="icon-pencil icon-white"></i>&nbsp;{{i18n.common.modify}}</button>
            <button href="#modal-token-delete" data-toggle="modal" class="btn btn-danger delete"><i class="icon-remove icon-white"></i>&nbsp;{{i18n.common.delete}}</button>
        </span>
    </td>
</tr>
