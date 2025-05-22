// fetch API를 사용하여 구현한 httpClient 클래스

// get 요청
function get(url, callback) {
    fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      }
    })
    .then(response => response.json())
    .then(data => {
      if (callback) callback(data);
    })
    .catch(error => console.error(error));
}