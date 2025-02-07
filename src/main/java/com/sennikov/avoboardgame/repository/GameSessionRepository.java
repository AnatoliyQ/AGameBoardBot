package com.sennikov.avoboardgame.repository;

import com.sennikov.avoboardgame.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findAllByOrderByPlayDateDesc();

    @Query(nativeQuery = true, value = """
        SELECT 
            u.telegram_username as username,
            CONCAT(u.first_name, ' ', u.last_name) as fullName,
            COUNT(CASE WHEN g.type = 'PVP' AND gsp.is_winner = true THEN 1 END) as pvpWins,
            COUNT(CASE WHEN g.type = 'PVE' AND gsp.is_winner = true THEN 1 END) as pveWins
        FROM app_users u
        LEFT JOIN game_session_players gsp ON u.telegram_username = gsp.player_username
        LEFT JOIN game_sessions gs ON gsp.session_id = gs.id
        LEFT JOIN board_games g ON gs.game_id = g.id
        GROUP BY u.telegram_username, u.first_name, u.last_name
        ORDER BY (COUNT(CASE WHEN gsp.is_winner = true THEN 1 END)) DESC
    """)
    List<Map<String, Object>> findPlayerWinsStatistics();

    @Query(nativeQuery = true, value = """
        SELECT 
            u.telegram_username as username,
            CONCAT(u.first_name, ' ', u.last_name) as fullName,
            COUNT(DISTINCT gs.play_date) as daysPlayed,
            COUNT(DISTINCT gs.id) as gamesPlayed
        FROM app_users u
        LEFT JOIN game_session_players gsp ON u.telegram_username = gsp.player_username
        LEFT JOIN game_sessions gs ON gsp.session_id = gs.id
        GROUP BY u.telegram_username, u.first_name, u.last_name
        ORDER BY daysPlayed DESC
    """)
    List<Map<String, Object>> findPlayerGameStatistics();

    @Query(nativeQuery = true, value = """
        SELECT 
            g.id as id,
            g.name as name,
            COUNT(gs.id) as sessionsCount
        FROM board_games g
        LEFT JOIN game_sessions gs ON g.id = gs.game_id
        GROUP BY g.id, g.name
        ORDER BY sessionsCount DESC
    """)
    List<Map<String, Object>> findGameStatistics();

    @Query(nativeQuery = true, value = """
        SELECT 
            g.id as id,
            g.name as name,
            COUNT(gs.id) as gamesCount
        FROM board_games g
        JOIN game_sessions gs ON g.id = gs.game_id
        JOIN game_session_players gsp ON gs.id = gsp.session_id
        WHERE gsp.player_username = :username
        GROUP BY g.id, g.name
        ORDER BY gamesCount DESC
        LIMIT 3
    """)
    List<Map<String, Object>> findTopGamesByPlayer(String username);

    @Query(nativeQuery = true, value = """
        SELECT 
            g.id as id,
            g.name as name,
            COUNT(CASE WHEN gsp.is_winner = true THEN 1 END) as winsCount
        FROM board_games g
        JOIN game_sessions gs ON g.id = gs.game_id
        JOIN game_session_players gsp ON gs.id = gsp.session_id
        WHERE gsp.player_username = :username
        GROUP BY g.id, g.name
        HAVING COUNT(CASE WHEN gsp.is_winner = true THEN 1 END) > 0
        ORDER BY winsCount DESC
        LIMIT 3
    """)
    List<Map<String, Object>> findTopWinningGamesByPlayer(String username);
}