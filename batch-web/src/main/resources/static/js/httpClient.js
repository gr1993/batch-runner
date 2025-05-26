// fetch API를 사용하여 구현한 httpClient 클래스

// get 요청
function get(url, callback) {
    fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      }
    })
    .then(response => response.text())
    .then(text => {
        const data = text ? JSON.parse(text) : [];
        if (callback) callback(data);
    })
    .catch(error => console.error(error));
}

// post 요청
function post(url, body, callback) {
    const options = {
        method: 'POST',
        headers: {}
    };

    if (body) {
        options.headers['Content-Type'] = 'application/json';
        options.body = JSON.stringify(body);
    }

    fetch(url, options)
        .then(response => response.text())
        .then(text => {
            const data = text ? JSON.parse(text) : [];
            if (callback) callback(data);
        })
        .catch(error => console.error(error));
}

// delete 요청
function doDelete(url, callback) {
    fetch(url, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      }
    })
    .then(response => response.text())
    .then(text => {
        const data = text ? JSON.parse(text) : [];
        if (callback) callback(data);
    })
    .catch(error => console.error(error));
}