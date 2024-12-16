CREATE TABLE articles (
    article_id BIGINT PRIMARY KEY,
    name VARCHAR(1024) NOT NULL,
    trending BOOLEAN NOT NULL,
    tags TEXT[] NOT NULL
);

CREATE TABLE comments (
    comment_id BIGINT PRIMARY KEY,
    article_id BIGINT REFERENCES articles(article_id) NOT NULL,
    content TEXT NOT NULL
);