function follow(Event) {
    var element = event.currentTarget;
    var userId = element.value;
    data = {};
    data["userId"] = userId;
    $.ajax({
        url: "/followUser",
        type: 'post',
        data: data,
        dataType: 'json',
        success: function (response) {
            window.location.reload();
        }
    })
}

function unfollow() {
    var element = event.currentTarget;
    var userId = element.value;
    data = {};
    data["userId"] = userId;
    $.ajax({
        url: "/unfollowUser",
        type: 'post',
        data: data,
        dataType: 'json',
        success: function (response) {
            window.location.reload();
        }
    })
}

function followq() {
    var element = event.currentTarget;
    var questionId = element.value;
    data = {};
    data["questionId"] = questionId;
    $.ajax({
        url: "/followQuestion",
        type: 'post',
        data: data,
        dataType: 'json',
        success: function (response) {
            window.location.reload();
        }
    })
}

function unfollowq() {
    var element = event.currentTarget;
    var questionId = element.value;
    data = {};
    data["questionId"] = questionId;
    $.ajax({
        url: "/unfollowQuestion",
        type: 'post',
        data: data,
        dataType: 'json',
        success: function (response) {
            window.location.reload();
        }
    })
}
