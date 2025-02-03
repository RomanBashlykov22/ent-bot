CREATE TABLE users (
    id VARCHAR(255) PRIMARY KEY
);

CREATE TABLE topics (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE subtopics (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    topic_id BIGINT NOT NULL REFERENCES topics(id)
);

CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    topic_id BIGINT NOT NULL REFERENCES topics(id),
    subtopic_id BIGINT NOT NULL REFERENCES subtopics(id),
    question_text TEXT NOT NULL,
    correct_option VARCHAR(255) NOT NULL
);

CREATE TABLE question_options (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL REFERENCES questions(id),
    option TEXT NOT NULL
);

CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL REFERENCES users(id),
    topic_id BIGINT NOT NULL REFERENCES topics(id),
    correct_answers INT NOT NULL,
    total_questions INT NOT NULL,
    session_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


