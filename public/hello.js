$(document).ready(function() {
    $.ajax({
        url: "http://localhost:8080/blogdata/centroids"
    }).then(function(html) {
        $('.blogdata-kmeans-centroids').append(html);
    });
});