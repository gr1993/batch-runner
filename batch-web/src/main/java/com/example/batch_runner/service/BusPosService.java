package com.example.batch_runner.service;

import com.example.batch_runner.domain.RouteBusPos;
import com.example.batch_runner.repository.FavoriteRouteRepository;
import com.example.batch_runner.repository.RouteBusPosRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusPosService {

    private final RouteBusPosRepository routeBusPosRepository;
    private final FavoriteRouteRepository favoriteRouteRepository;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private Map<Long, RouteBusPos> tempBusPosMap = new HashMap<>();

    @PostConstruct
    public void startDetectLocationChange() {
        // 주기적으로 버스 위치 변경 감지 (주기 : 1초)
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::detectLocationChangeAndUpdate, 0, 1, TimeUnit.SECONDS);
    }


    /**
     * SSE로 버스 위치를 전송받기 위해 등록
     */
    public SseEmitter registerSse() {
        SseEmitter emitter = new SseEmitter(60 * 1000L);

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        List<String> routeIds = favoriteRouteRepository.findRouteIdsGroupByRouteId();
        List<RouteBusPos> busPosList = routeBusPosRepository.findAllByRouteIdIn(routeIds);
        try {
            emitter.send(busPosList);
        } catch (IOException e) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    /**
     * 버스 위치 변경 감지 및 갱신
     */
    private void detectLocationChangeAndUpdate() {
        List<String> routeIds = favoriteRouteRepository.findRouteIdsGroupByRouteId();
        List<RouteBusPos> busPosList = routeBusPosRepository.findAllByRouteIdIn(routeIds);
        if (hasLocationChanged(busPosList)) {
            pushLocationToClients(busPosList);
        }
    }

    /**
     * 변경 여부 검사
     */
    private boolean hasLocationChanged(List<RouteBusPos> busPosList) {
        // 수가 맞지 않으면 갱신
        if (tempBusPosMap.size() != busPosList.size()) {
            saveTempBusPos(busPosList);
            return true;
        }
        // 값이 다르거나 임시 저장소에 정보가 없으면 갱신
        for(RouteBusPos busPos : busPosList) {
            RouteBusPos tempBusPos = tempBusPosMap.get(busPos.getVehId());
            if (tempBusPos == null) {
                saveTempBusPos(busPosList);
                return true;
            }
            if (!tempBusPos.getPosX().equals(busPos.getPosX()) || !tempBusPos.getPosY().equals(busPos.getPosY())) {
                saveTempBusPos(busPosList);
                return true;
            }
        }
        return false;
    }

    private void saveTempBusPos(List<RouteBusPos> busPosList) {
        tempBusPosMap = busPosList.stream()
                .collect(Collectors.toMap(RouteBusPos::getVehId, pos -> pos));
    }

    /**
     * 스케줄러에서 즐겨찾기에 등록한 노선의 버스위치 변경된 경우 호출
     */
    private void pushLocationToClients(List<RouteBusPos> busPosList) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(busPosList);
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
