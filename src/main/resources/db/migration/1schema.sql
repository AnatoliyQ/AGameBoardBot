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

CREATE TABLE game_session_players (
    session_id INTEGER REFERENCES game_sessions(id),
    player_username VARCHAR(32) REFERENCES app_users(telegram_username) DEFERRABLE INITIALLY DEFERRED,
    is_winner BOOLEAN NOT NULL,
    PRIMARY KEY (session_id, player_username)
);

-- Добавим индексы для оптимизации запросов
CREATE INDEX IF NOT EXISTS idx_game_session_players_username 
ON game_session_players(player_username);

CREATE INDEX IF NOT EXISTS idx_game_sessions_game_id 
ON game_sessions(game_id);