package com.example.batch_runner.external.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class BusArrivalInfoDto {
    public String arrmsg1;       // 첫번째 도착예정 버스의 도착정보 메시지
    public String arrmsg2;       // 두번째 도착예정 버스의 도착정보 메시지
    public String arsId;         // 정류소 번호
    public int avgCf1;           // 첫번째 버스의 이동평균 보정계수
    public int avgCf2;           // 두번째 버스의 이동평균 보정계수
    public int brdrde_Num1;      // 첫번째 버스의 뒷차 인원
    public int brdrde_Num2;      // 두번째 버스의 뒷차 인원
    public int brerde_Div1;      // 첫번째 버스의 뒷차 정보 구분
    public int brerde_Div2;      // 두번째 버스의 뒷차 정보 구분
    public String busRouteAbrv;  // 노선 약칭
    public String busRouteId;    // 노선 ID
    public int busType1;         // 첫번째 도착 버스의 차량 유형
    public int busType2;         // 두번째 도착 버스의 차량 유형
    public String deTourAt;      // 우회 여부 (00:정상, 11:우회)
    public String dir;           // 방향 (현재 빈값)
    public int expCf1;           // 첫번째 버스의 지수평활 보정계수
    public int expCf2;           // 두번째 버스의 지수평활 보정계수
    public int exps1;            // 첫번째 버스의 도착예정시간(초)
    public int exps2;            // 두번째 버스의 도착예정시간(초)
    public String firstTm;       // 첫차시간
    public int full1;            // 첫번째 버스의 만차 여부
    public int full2;            // 두번째 버스의 만차 여부
    public int goal1;            // 첫번째 버스 종점 도착예정시간(초)
    public int goal2;            // 두번째 버스 종점 도착예정시간(초)
    public int isArrive1;        // 첫번째 버스 도착여부 (0:운행중, 1:도착)
    public int isArrive2;        // 두번째 버스 도착여부
    public int isLast1;          // 첫번째 버스 막차 여부
    public int isLast2;          // 두번째 버스 막차 여부
    public int kalCf1;           // 첫번째 버스 기타1 보정계수
    public int kalCf2;           // 두번째 버스 기타1 보정계수
    public int kals1;            // 첫번째 버스 기타1 도착예정시간(초)
    public int kals2;            // 두번째 버스 기타1 도착예정시간(초)
    public String lastTm;        // 막차시간
    public String mkTm;          // 제공 시각
    public int namin2Sec1;       // 첫번째 버스의 2번째 주요정류소 여행시간
    public int namin2Sec2;       // 두번째 버스의 2번째 주요정류소 여행시간
    public int neuCf1;           // 첫번째 버스 기타2 보정계수
    public int neuCf2;           // 두번째 버스 기타2 보정계수
    public int neus1;            // 첫번째 버스 기타2 도착예정시간(초)
    public int neus2;            // 두번째 버스 기타2 도착예정시간(초)
    public String nextBus;       // 막차 운행여부 (N:아님, Y:막차)
    public int nmain2Ord1;
    public int nmain2Ord2;
    public String nmain2Stnid1;
    public String nmain2Stnid2;
    public int nmain3Ord1;
    public int nmain3Ord2;
    public int nmain3Sec1;
    public int nmain3Sec2;
    public String nmain3Stnid1;
    public String nmain3Stnid2;
    public int nmainOrd1;
    public int nmainOrd2;
    public int nmainSec1;
    public int nmainSec2;
    public String nmainStnid1;
    public String nmainStnid2;
    public String nstnId1;       // 첫번째 버스 다음 정류소 ID
    public String nstnId2;       // 두번째 버스 다음 정류소 ID
    public int nstnOrd1;         // 첫번째 버스 다음 정류소 순번
    public int nstnOrd2;         // 두번째 버스 다음 정류소 순번
    public int nstnSec1;         // 첫번째 버스 다음 정류소 여행시간
    public int nstnSec2;
    public int nstnSpd1;         // 첫번째 버스 다음 정류소 예상속도
    public int nstnSpd2;
    public String plainNo1;      // 첫번째 도착 차량 번호
    public String plainNo2;      // 두번째 도착 차량 번호
    public String repTm1;        // 첫번째 도착 차량의 최종 보고시간
    public int rerdie_Div1;      // 첫번째 버스 재차 구분
    public int rerdie_Div2;
    public int reride_Num1;      // 첫번째 버스 재차 인원 or 혼잡도
    public int reride_Num2;
    public int routeType;        // 노선 유형 (1~9)
    public String rtNm;          // 노선 이름
    public int sectOrd1;         // 첫번째 버스 현재 구간 순번
    public int sectOrd2;
    public String stId;          // 정류소 고유 ID
    public String stNm;          // 정류소 이름
    public int staOrd;           // 요청한 정류소의 순번
    public String stationNm1;    // 첫번째 도착 버스의 종점 이름
    public String stationNm2;
    public int term;             // 배차간격 (분)
    public int traSpd1;          // 첫번째 버스 속도 (km/h)
    public int traSpd2;
    public int traTime1;         // 첫번째 버스의 전체 남은 시간 (초)
    public int traTime2;
    public String vehId1;        // 첫번째 도착 차량 ID
    public String vehId2;
}