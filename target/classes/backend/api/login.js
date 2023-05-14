function loginApi(data) {
    return $axios({
        //向后端请求数据并返回
        'url': '/employee/login',
        'method': 'post',
        data
    })
}

function logoutApi() {
    return $axios({
        'url': '/employee/logout',
        'method': 'post',
    })
}
