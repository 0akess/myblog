CREATE TABLE IF NOT EXISTS post (
                      id BIGSERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      content TEXT NOT NULL,
                      image_url VARCHAR(255),
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      likes INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS post_tags (
                           post_id BIGINT,
                           tags VARCHAR(255),
                           FOREIGN KEY (post_id) REFERENCES post(id)
);


CREATE TABLE IF NOT EXISTS comment (
                         id BIGSERIAL PRIMARY KEY,
                         post_id BIGINT NOT NULL,
                         content TEXT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (post_id) REFERENCES post(id)
);