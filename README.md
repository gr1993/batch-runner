# 프로젝트 구조

이 프로젝트는 Gradle 기반 멀티모듈 구조로 구성되어 있으며, 각 모듈의 역할은 다음과 같다

<pre><code>```
[batch-common]
    └── batch-core와 batch-web에서 공통으로 사용하는 도메인 및 유틸 클래스를 포함한 공통 모듈

[batch-core]
    └── 실제 배치 Job 실행을 담당하는 비즈니스 로직 중심 모듈
    └── Spring Batch와 Quartz를 사용하여 정기적인 Job 실행 처리

[batch-web]
    └── 관리자 및 모니터링 기능을 제공하는 웹 기반 실행 및 관리 모듈
    └── 지도 기반 UI (Geo 관련 기능)
    └── 배치 Job 실행 트리거 및 관리 UI 제공
    └── Spring Batch 메타 테이블을 활용한 실행 로그 조회 및 모니터링 제공
``` </code></pre>


# 프로젝트 개요

카카오 버스앱을 모티브로 한 것으로 지도상에 버스 위치들을 1분 간격으로 볼 수 있고  
특정정류장에 버스 도착시간을 예측하여 검색할 수 있도록 데이터를 수집하는 배치 서버 구현

### 관리자 기능
* 관심 정류소 보기
	* 지도에 정류장의 위치 정보 표시
	* 관심 정류소의 노선에 속한 모든 버스 위치가 1분 간격으로 갱신
	* 특정 정류소에 버스 도착시간 조회
* 작업 관리
	* 현재 스케줄러에 등록된 Job 리스트 조회
	* Job 수동실행, 스케줄 재개 및 중단, 스케줄 조정 기능 제공 (REST API로 스케줄을 동적으로 관리)
	* Job의 실행 이력 조회 모달(페이지네이션)


### 배치 스케줄러
매 1분마다 동작하는 Job이 2개 이상이므로 다중 배치서버에서 Job 실행 중복이  
일어나지 않도록 Quartz를 사용한 스케줄 클러스터링을 진행할 예정이다.

1. 매 1분마다 관심 정류장의 노선에 속한 버스 차량들의 위치를 조회 및 저장
2. 매 1분마다 관심 정류장의 도착 예상 정보를 조회 및 저장
3. 초기에 엑셀데이터를 읽어 전체 정류소 정보를 DB에 저장


# 개발 환경

### batch-core 기술
* Spring Boot 3.4.5 (JDK 17)
* spring-boot-starter-quartz : 스케줄러
* spring-boot-starter-batch
* JPA
* postgresql


### batch-web 기술
* Spring Boot 3.4.5 (JDK 17)
* thymeleaf
* JPA
* postgresql
* Kakao Map API


# ERD

테이블 생성 DDL 파일은 batch-core 모듈 resources/sql/schema.sql 에 위치하고 있다.


# 외부 API

공공데이터포털 API에 활용신청하고 승인이 되었어도 인증키가 해당 기관에 동기화되어 유효할 때까지 상당히 오래걸렸다. (일주일 정도 걸린 듯 하다)

### 공공데이터포털 API
* 노선ID와 구간정보로 차량들의 위치정보를 조회
	* 문서 링크 : https://www.data.go.kr/data/15000332/openapi.do
	* 서비스 URL : http://ws.bus.go.kr/api/rest/buspos/getBusPosByRouteSt
* 특정 정류소에 대한 버스 도착예정 정보 제공
	* 문서 링크 : https://www.data.go.kr/data/15000314/openapi.do
	* 서비스 URL : http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll
* 노선에 대한 정보 제공
	* 문서 링크 : https://www.data.go.kr/data/15000193/openapi.do
	* 서비스 URL : http://ws.bus.go.kr/api/rest/busRouteInfo/getStaionByRoute

### 서울 열린데이터광장
* 서울시 버스 노선 정보 조회
	* 문서 링크 : https://data.seoul.go.kr/dataList/OA-1095/F/1/datasetView.do
	* xlsx 파일 제공
* 서울시 버스정류소 위치정보(사용안함)
	* 문서 링크 : https://data.seoul.go.kr/dataList/OA-15067/S/1/datasetView.do
	* xlsx 파일 제공