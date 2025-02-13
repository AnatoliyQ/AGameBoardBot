DROP TABLE IF EXISTS game_session_players CASCADE;
DROP TABLE IF EXISTS game_sessions CASCADE;
DROP TABLE IF EXISTS board_games CASCADE;
DROP TABLE IF EXISTS app_users CASCADE;
DROP TYPE IF EXISTS GAME_TYPE;

DO LANGUAGE plpgsql 'BEGIN CREATE TYPE GAME_TYPE AS ENUM (''PVP'', ''PVE''); EXCEPTION WHEN duplicate_object THEN NULL; END;';

CREATE TABLE IF NOT EXISTS board_games (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    type GAME_TYPE NOT NULL,
    players VARCHAR(255),
    owner VARCHAR(255),
    image_url TEXT,
    description TEXT
);

CREATE TABLE IF NOT EXISTS app_users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    telegram_username VARCHAR(100) UNIQUE NOT NULL,
    is_employee BOOLEAN DEFAULT true
);

CREATE TABLE IF NOT EXISTS game_sessions (
    id SERIAL PRIMARY KEY,
    game_id BIGINT NOT NULL,
    play_date DATE NOT NULL,
    FOREIGN KEY (game_id) REFERENCES board_games(id)
);

CREATE TABLE IF NOT EXISTS game_session_players (
    session_id BIGINT,
    player_username VARCHAR(100),
    is_winner BOOLEAN,
    PRIMARY KEY (session_id, player_username),
    FOREIGN KEY (session_id) REFERENCES game_sessions(id),
    FOREIGN KEY (player_username) REFERENCES app_users(telegram_username)
);

-- Добавим индексы для оптимизации запросов
CREATE INDEX IF NOT EXISTS idx_game_session_players_username 
ON game_session_players(player_username);

CREATE INDEX IF NOT EXISTS idx_game_sessions_game_id 
ON game_sessions(game_id);