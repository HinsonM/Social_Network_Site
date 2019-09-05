<!--
从几个角度来看：
从基本程序结构的角度来看；
从freemarkder语言中的变量等的使用方法来看；
最终业务逻辑，事实上不用完全懂，只需要看懂需要的即可
-->
<!DOCTYPE html>
<html lang="en">
<#include "head.ftl">
<body>
<#include "navbar.ftl">
<div class="container" style="margin-top: 50px">
    <div class="js-blog-list">

    </div>
    <div class="container text-center">
        <button class="btn btn-default js-load-more">加载更多</button>
    </div>
    <br><br>
</div>
<div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel"
     aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">发布</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <input type="text" class="form-control" id="question" placeholder="微博标题">
                </div>
                <div class="form-group">
                    <label for="textarea">微博内容</label>
                    <textarea class="form-control" id="textarea" rows="3" placeholder="不多于140字"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onclick="add()">发布</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="messageModal" tabindex="-1" role="dialog" aria-labelledby="messageModalLabel"
     aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel_">发送私信</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
<!--                <input type="hidden" name="id" id="toid" value="3">-->
                <div class="form-group">
                    <label for="textarea">内容</label>
                    <textarea class="form-control" id="message" rows="3" placeholder="私信内容"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onclick="send()">发布</button>
            </div>
        </div>
    </div>
</div>
</body>
</html>