DROP TABLE IF EXISTS game_session_players CASCADE;
DROP TABLE IF EXISTS game_sessions CASCADE;
DROP TABLE IF EXISTS board_games CASCADE;
DROP TABLE IF EXISTS app_users CASCADE;
DROP TYPE IF EXISTS GAME_TYPE;

DO LANGUAGE plpgsql 'BEGIN CREATE TYPE GAME_TYPE AS ENUM (''PVP'', ''PVE''); EXCEPTION WHEN duplicate_object THEN NULL; END;';

-- Базовая таблица пользователей
CREATE TABLE IF NOT EXISTS app_users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    telegram_username VARCHAR(32) UNIQUE NOT NULL,
    is_employee BOOLEAN DEFAULT true
);

-- Таблица настольных игр
CREATE TABLE IF NOT EXISTS board_games (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    type GAME_TYPE NOT NULL,
    players VARCHAR(50),
    owner VARCHAR(32) REFERENCES app_users(telegram_username) DEFERRABLE INITIALLY DEFERRED,
    image_url TEXT,
    description TEXT
);

-- Таблица игровых сессий
CREATE TABLE IF NOT EXISTS game_sessions (
    id SERIAL PRIMARY KEY,
    game_id INTEGER NOT NULL REFERENCES board_games(id) DEFERRABLE INITIALLY DEFERRED,
    play_date DATE NOT NULL
);

-- Таблица участников игровых сессий с отметкой победителей
CREATE TABLE IF NOT EXISTS game_session_players (
    session_id INTEGER REFERENCES game_sessions(id) DEFERRABLE INITIALLY DEFERRED,
    player_username VARCHAR(32) REFERENCES app_users(telegram_username) DEFERRABLE INITIALLY DEFERRED,
    is_winner BOOLEAN NOT NULL,
    PRIMARY KEY (session_id, player_username)
);

-- Индексы для оптимизации запросов
CREATE INDEX IF NOT EXISTS idx_game_session_players_username 
ON game_session_players(player_username);

CREATE INDEX IF NOT EXISTS idx_game_sessions_game_id 
ON game_sessions(game_id);

CREATE INDEX IF NOT EXISTS idx_board_games_owner 
ON board_games(owner);