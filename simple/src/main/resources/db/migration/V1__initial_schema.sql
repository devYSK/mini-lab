-- users table
CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    password   VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP
);

-- boards table
CREATE TABLE boards
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP
);

-- posts table
CREATE TABLE posts
(
    id         BIGSERIAL PRIMARY KEY,
    board_id   BIGINT REFERENCES boards (id),
    user_id    BIGINT REFERENCES users (id),
    title      VARCHAR(200) NOT NULL,
    content    TEXT         NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP
);

-- comments table
CREATE TABLE comments
(
    id         BIGSERIAL PRIMARY KEY,
    post_id    BIGINT REFERENCES posts (id),
    user_id    BIGINT REFERENCES users (id),
    content    TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP
);

-- short_urls table
CREATE TABLE short_urls
(
    id         BIGSERIAL PRIMARY KEY,
    post_id    BIGINT REFERENCES posts (id),
    short_url  VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP
);
