// fetch API를 사용하여 구현한 httpClient 클래스

// get 요청
function get(url, callback) {
    request(url, 'GET', null, callback);
}

// post 요청
function post(url, body, callback) {
    request(url, 'POST', body, callback);
}

// put 요청
function put(url, body, callback) {
    request(url, 'PUT', body, callback);
}

// delete 요청
function doDelete(url, callback) {
    request(url, 'DELETE', null, callback);
}

function request(url, method = 'GET', body = null, callback = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json'
        }
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    fetch(url, options)
        .then(response => {
            if (!response.ok) {
                return response.text().then(errText => {
                    throw new Error(`HTTP ${response.status}: ${errText}`);
                });
            }
            return response.text();
        })
        .then(text => {
            const data = text ? JSON.parse(text) : [];
            if (callback) callback(data);
        })
        .catch(error => {
            alert(`요청 실패(${error.message})`);
        });
}