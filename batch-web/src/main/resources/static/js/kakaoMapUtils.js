// 카카오 맵을 활용한 유틸 클래스

// 마커를 생성 (카카오가 마커이미지를 공식적으로 제공하는 것은 아니라 샘플을 확인할 수 있는 사이트는 존재하지 않음)
// 직접 이미지를 다운로드 받아서 정적리소스로 활용 (다운로드 링크 : https://www.flaticon.com/search?word=bus)
var bugImage = "/image/bus.png";
var stopImage = "/image/bus-stop.png";
function createMarker(position, type, title) {
    var markerOption = { position };

    if (type) {
        var image = bugImage;
        switch(type) {
            case 'stop' : image = stopImage; break;
        }

        var imageSize = new kakao.maps.Size(35, 35);
        var markerImage = new kakao.maps.MarkerImage(image, imageSize);
        markerOption.image = markerImage;
    }

    if (title) {
        markerOption.title = title; // 마우스 오버 시 표시될 텍스트
    }

    var marker = new kakao.maps.Marker(markerOption);
    return marker;
}