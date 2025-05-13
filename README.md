# 프로젝트 구조

<pre><code>```
[batch-core (Spring Batch + Quartz)]
    └── 실제 Job 실행 담당
    └── Web 의존성 최소화

[batch-runner (Spring Boot Web)]
    └── REST API로 Job 실행 트리거 (JobLauncher)
    └── DB에서 실행 로그 조회 (Spring Batch 메타테이블 사용)
    └── 실행 결과 모니터링 UI 제공
``` </code></pre>


# 프로젝트 개요

카카오 버스앱을 모티브로 한 것으로 지도상에 버스 위치들을 1분 간격으로 볼 수 있고  
특정정류장에 버스 도착시간을 예측하여 검색할 수 있도록 데이터를 수집하는 배치 서버 구현



## 배치 스케줄러

1. 매 1분마다 서울시 버스 위치를 조회
2. 매 1분마다 버스가 어느 정류장에 가까운지 → 도착 예측


## 공공 API

###노선ID와 구간정보로 차량들의 위치정보를 조회
* 문서 링크 : https://www.data.go.kr/data/15000332/openapi.do
* 서비스 URL : http://ws.bus.go.kr/api/rest/buspos/getBusPosByRouteSt