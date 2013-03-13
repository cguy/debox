<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head></head>
    <body th:fragment="body">
        <ul class="thumbnails photos">
            <li class="span2" th:each="media : ${medias}">
                <a th:if="${media instanceof org.debox.photo.model.Photo}"
                   class="thumbnail"
                   data-photo="true"
                   th:href="${'#' + media.id}"
                   data:id="${media.id}"
                   data:date="${media.date}"
                   data:title="${media.title}"
                   data:thumbnailUrl="${media.thumbnailUrl}"
                   data:url="${media.url}">

                    <span class="picture" th:style="${'background-image:url('} + @{'/' + ${media.thumbnailUrl}} + ${')'}"></span>
                    <span class="filter"><i class="icon-plus-sign"></i></span>
                </a>
                
                <a th:if="${media instanceof org.debox.photo.model.Video}"
                   class="thumbnail"
                   data-video="true"
                   th:href="${'#' + media.id}"
                   data:id="${media.id}"
                   data:date="${media.date}"
                   data:title="${media.title}"
                   data:thumbnailUrl="${media.thumbnailUrl}"
                   data:oggUrl="${media.oggUrl}"
                   data:h264Url="${media.h264Url}"
                   data:webmUrl="${media.webmUrl}">

                    <span class="picture video" th:style="${'background-image:url('} + @{'/' + ${media.squareThumbnailUrl}} + ${')'}">
                        <i class="icon-film"></i>
                    </span>
                    <span class="filter"><i class="icon-play-circle"></i></span>
                </a>
            </li>
        </ul>
    </body>
</html>