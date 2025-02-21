-- posts 테이블 생성
CREATE TABLE posts
(
    id BIGSERIAL PRIMARY KEY,                       -- 자동 증가하는 기본 키
    title      VARCHAR(255) NOT NULL,               -- 제목
    content    TEXT         NOT NULL,               -- 본문 내용
    user_id    BIGINT       NOT NULL,               -- 사용자의 ID (외래 키)
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(), -- 생성 시간 (BaseEntity)
    updated_at TIMESTAMP    NULL                    -- 수정 시간 (BaseEntity)
);

-- users 테이블 생성
CREATE TABLE users
(
    id BIGSERIAL PRIMARY KEY,                        -- 자동 증가하는 기본 키
    name       VARCHAR(255) NOT NULL,                -- 사용자 이름
    username   VARCHAR(255) NOT NULL,                -- 사용자명
    password   VARCHAR(255) NOT NULL,                -- 패스워드
    role       VARCHAR(50)  NOT NULL DEFAULT 'USER', -- 사용자 역할 (기본값: USER)
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),  -- 생성 시간 (BaseEntity)
    updated_at TIMESTAMP    NULL                     -- 수정 시간 (BaseEntity)
);

-- 외래 키 제약 조건 추가 (user_id는 users 테이블의 id를 참조)
ALTER TABLE posts
    ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id);
