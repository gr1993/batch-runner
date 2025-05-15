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