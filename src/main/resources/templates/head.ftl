<!--下述语句配置css与js的信息-->
<head>
    <meta charset="UTF-8">
    <title>首页</title>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/css/header.css">
    <link rel="stylesheet" type="text/css" href="/css/login.css">
    <link rel="stylesheet" type="text/css" href="/css/message.css">
    <link rel="stylesheet" type="text/css" href="/css/button.css">
    <script src="/js/jquery.min.js"></script>
    <script src="/js/bootstrap.min.js"></script>
    <script src="/js/add.js"></script>
    <script src="/js/like.js"></script>
    <script src="/js/follow.js"></script>

    <script>
    $(function () {
        var count = 0;
        var pageStart = 0;
        var pageSize = 10;
        getData(pageStart, pageSize);
        $(document).on('click', '.js-load-more', function () {
            count++;
            pageStart = count * pageSize;
            getData(pageStart, pageSize);
        });
    });
    function getData(offset, size) {
        $.ajax(
            {
                type: 'POST',
                url: '/get?offset=' + offset + "&limit=" + size,
                dataType: 'json',
                success: function (response) {
                    var len = response.length;
                    var result = '';
                    for (var i = 0; i < len; i++) {
                        var userId = response[i].objs.user.id;
                        result += '<hr/>' +
                            '<div class="row">' +
                            '<div class="col-sm-8 col-lg-8 col-md-8 col-xl-8"><div class="row"><div class="col-sm-3" align="center">' +
                            '<img width="100px" height="100px" alt="image" src="' + response[i].objs.user.headUrl + '"/>';
                        //alert("before!");  有可能是空指针，若下面发生错误，user有可能是空指针，因此这里某处发生错误，后面的js语句就执行不了，因此渲染不出界面
                        if (response[i].objs.current)
                        {
                            result += '<input type="hidden" name="id" id="toid" value="' + response[i].objs.user.id + '">'
                            if (userId == response[i].objs.current.id) {
                                result += '<button class="btn btn-light" data-toggle="modal" data-target="#messageModal"' +
                                    ' style="margin-top: 20px" data-id="' + response[i].objs.user.id + '" disabled>发送私信</button>';
                                    // alert("middle!");
                            } else {
                                result += '<button class="btn btn-light" data-toggle="modal" data-target="#messageModal"' +
                                    ' style="margin-top: 20px" data-id="' + response[i].objs.user.headUrl + '">发送私信'
                            }
                        }
                        //alert("after!");
                        result += '</div><div class="col-sm-9"><p><a href=' + '"/question/' + response[i].objs.question.id + '"' +
                            ' style="font-family: 微软雅黑;font-size: 18px;">' + response[i].objs.question.title +
                            '</a></p>' + '<div class="row"><div class="col-sm-4"><a href="/user/' +response[i].objs.user.id +
                              '">' + response[i].objs.user.name + '</a></div><div class="col-sm-6"><p>' + response[i].objs.question.createdDate +
                            '</p></div></div>' + '<div class="row-fluid"><div class="col-sm-12"><p>' + response[i].objs.question.content +
                                '</p></div></div>' + '<div class="row"><div class="col-sm-4"><p>';
                        if (response[i].objs.current) {
                            if (response[i].objs.followed) {
                                result += '<button type="button" class="btn btn-light" onclick="unfollow()" value="' +
                                    response[i].objs.user.id + '">取消关注</button>';
                            } else {
                                result += '<button type="button" class="btn btn-light" onclick="follow()" value="' +
                                    response[i].objs.user.id + '">关注作者</button>';
                            }
                        }
                        result += '</p></div><div class="col-sm-4"><p>评论数：' + response[i].objs.question.commentCount +
                            '</p></div></div></div></div></div></div>';
                    }
                    $('.js-blog-list').append(result);
                },
                error: function (xhr, type) {
                    alert('Ajax error!');
                }
            }
        )
    }
</script>
</head>
