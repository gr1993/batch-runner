-- 노선별 정류소 정보 테이블
CREATE TABLE route_stop_info (
    route_id     VARCHAR(20),
    route_name   VARCHAR(100),
    node_seq     INTEGER,
    node_id      VARCHAR(20),
    ars_id       VARCHAR(20),
    node_name    VARCHAR(200),
    pos_x        NUMERIC(10, 7),
    pos_y        NUMERIC(10, 7)
);

COMMENT ON TABLE route_stop_info IS '노선별 정류소 정보';

COMMENT ON COLUMN route_stop_info.route_id IS '노선 ID (노선별 고유 식별자)';
COMMENT ON COLUMN route_stop_info.route_name IS '노선명 (예: 구로13)';
COMMENT ON COLUMN route_stop_info.node_seq IS '정류소 순번 (노선 내 정류소의 순서)';
COMMENT ON COLUMN route_stop_info.node_id IS '정류소 ID (내부 시스템 고유 ID)';
COMMENT ON COLUMN route_stop_info.ars_id IS 'ARS ID (고객 안내용 정류소 번호)';
COMMENT ON COLUMN route_stop_info.node_name IS '정류소명 (예: 구로1동주민센터.연예인APT)';
COMMENT ON COLUMN route_stop_info.pos_x IS '경도 (X좌표)';
COMMENT ON COLUMN route_stop_info.pos_y IS '위도 (Y좌표)';

-- route_stop_info 테이블 PK 설정
ALTER TABLE route_stop_info
ADD CONSTRAINT pk_route_stop_info
PRIMARY KEY (route_id, node_seq);

-- 노선 ID에 대한 인덱스
CREATE INDEX idx_route_stop_info_route_id
ON route_stop_info (route_id);

-- 정류소 ID에 대한 인덱스
CREATE INDEX idx_route_stop_info_node_id
ON route_stop_info (node_id);


-- 스케줄러 작업 관리 테이블
CREATE TABLE scheduler_job (
    id BIGSERIAL PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL UNIQUE,
    job_group VARCHAR(100) NOT NULL DEFAULT 'DEFAULT',
    cron_expression VARCHAR(100) NOT NULL,
    use_yn CHAR(1) NOT NULL,
    description TEXT,
    job_params_json JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE scheduler_job IS '스케줄러 작업 관리 테이블';

COMMENT ON COLUMN scheduler_job.id IS '식별자 (PK)';
COMMENT ON COLUMN scheduler_job.job_name IS 'Quartz Job 이름 (QRTZ_JOB_DETAILS의 JOB_NAME과 연동)';
COMMENT ON COLUMN scheduler_job.job_group IS 'Quartz Job 그룹명 (기본값: DEFAULT)';
COMMENT ON COLUMN scheduler_job.cron_expression IS 'Cron 표현식 (예: 0 0 * * * ?)';
COMMENT ON COLUMN scheduler_job.use_yn IS '사용 여부';
COMMENT ON COLUMN scheduler_job.description IS 'Job에 대한 설명';
COMMENT ON COLUMN scheduler_job.job_params_json IS 'Job 실행 시 넘길 파라미터 (JSON 형식)';
COMMENT ON COLUMN scheduler_job.created_at IS '생성 일시';
COMMENT ON COLUMN scheduler_job.updated_at IS '수정 일시';

-- scheduler_job 테이블 인덱스 추가
ALTER TABLE scheduler_job ADD CONSTRAINT uq_job_name_group UNIQUE (job_name, job_group);


-- 노선별 버스 실시간 위치 테이블
CREATE TABLE route_bus_pos (
    veh_id BIGINT PRIMARY KEY,
    route_id VARCHAR(20),
    plain_no VARCHAR(100),
    congetion INTEGER,
    pos_x NUMERIC(10, 7),
    pos_y NUMERIC(10, 7)
);

COMMENT ON TABLE route_bus_pos IS '노선별 버스 실시간 위치 테이블';

COMMENT ON COLUMN route_bus_pos.veh_id IS '버스 ID';
COMMENT ON COLUMN route_bus_pos.route_id IS '노선 ID';
COMMENT ON COLUMN route_bus_pos.plain_no IS '차량 번호';
COMMENT ON COLUMN route_bus_pos.congetion IS '차량 내부 혼잡도 (0: 없음, 3: 여유, 4: 보통, 5: 혼잡, 6: 매우혼잡)';
COMMENT ON COLUMN route_bus_pos.pos_x IS '맵매칭X좌표 (gpsX)';
COMMENT ON COLUMN route_bus_pos.pos_y IS '맵매칭Y좌표 (gpsY)';

-- route_bus_pos 테이블 인덱스 추가
CREATE INDEX idx_route_bus_pos_route_id ON route_bus_pos(route_id);


-- 즐겨찾기 노선 등록 테이블
CREATE TABLE favorite_route (
    route_id VARCHAR(20),
    node_id VARCHAR(20)
);

-- favorite_route 테이블 PK 설정
ALTER TABLE favorite_route
ADD CONSTRAINT pk_favorite_route
PRIMARY KEY (route_id, node_id);

COMMENT ON TABLE favorite_route IS '즐겨찾기 노선 등록 테이블';

COMMENT ON COLUMN favorite_route.route_id IS '노션 ID';
COMMENT ON COLUMN favorite_route.node_id IS '정류소 ID';