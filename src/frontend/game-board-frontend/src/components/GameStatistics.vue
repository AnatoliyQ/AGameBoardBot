<template>
  <div class="statistics-screen">
    <div class="header">
      <h2>Статистика</h2>
      <button class="back-button" @click="$emit('back')">
        Назад
      </button>
    </div>

    <div class="tabs">
      <button 
        v-for="tab in tabs" 
        :key="tab.id"
        :class="['tab-button', { active: currentTab === tab.id }]"
        @click="currentTab = tab.id"
      >
        {{ tab.name }}
      </button>
    </div>

    <div class="loading" v-if="loading">
      Загрузка статистики...
    </div>

    <div v-else>
      <div v-if="currentTab === 'winners'" class="statistics-table" data-tab="winners">
        <div class="table-header">
          <div class="header-cell">Игрок</div>
          <div 
            class="header-cell sortable"
            @click="sort('pvp')"
          >
            PVP {{ getSortIcon('pvp') }}
          </div>
          <div 
            class="header-cell sortable"
            @click="sort('pve')"
          >
            PVE {{ getSortIcon('pve') }}
          </div>
        </div>
        <div v-for="player in sortedWinners" 
             :key="player.username" 
             class="table-row clickable"
             @click="showPlayerDetails(player.username)">
          <div class="cell">{{ player.fullName }}</div>
          <div class="cell">{{ player.pvpWins }}</div>
          <div class="cell">{{ player.pveWins }}</div>
        </div>
      </div>

      <div v-if="currentTab === 'players'" class="statistics-table" data-tab="players">
        <div class="table-header">
          <div class="header-cell">Игрок</div>
          <div 
            class="header-cell sortable"
            @click="sort('days')"
          >
            Дней {{ getSortIcon('days') }}
          </div>
          <div 
            class="header-cell sortable"
            @click="sort('games')"
          >
            Игр {{ getSortIcon('games') }}
          </div>
        </div>
        <div v-for="player in sortedPlayers" 
             :key="player.username" 
             class="player-row"
             @click="showPlayerDetails(player.username)"
             tabindex="0"
             @keyup.enter="showPlayerDetails(player.username)"
             role="button"
             aria-label="Показать детали игрока">
          <div class="player-name">
            <span>{{ player.fullName }}</span>
          </div>
          <div class="player-stats">
            <div class="cell">{{ player.daysPlayed }}</div>
            <div class="cell">{{ player.gamesPlayed }}</div>
          </div>
        </div>
      </div>

      <div v-if="currentTab === 'games'" class="statistics-table" data-tab="games">
        <div class="table-header">
          <div class="header-cell">Игра</div>
          <div class="header-cell">Количество сессий</div>
        </div>
        <div v-for="game in topGames" :key="game.id" class="table-row">
          <div class="cell">{{ game.name }}</div>
          <div class="cell">{{ game.sessionsCount }}</div>
        </div>
      </div>
    </div>

    <div v-if="selectedPlayer" class="player-modal">
      <div class="player-modal-content">
        <div class="player-modal-header">
          <h3>{{ selectedPlayer.fullName }}</h3>
          <button class="close-button" @click="selectedPlayer = null">×</button>
        </div>
        
        <div class="player-details">
          <h4>Любимые игры</h4>
          <div class="games-list">
            <div v-for="game in selectedPlayer.topGames" :key="game.id" class="game-item">
              <span class="game-name">{{ game.name }}</span>
              <span class="game-count">{{ game.count }} игр</span>
            </div>
          </div>

          <h4>Больше всего побед</h4>
          <div class="games-list">
            <div v-for="game in selectedPlayer.topWinningGames" :key="game.id" class="game-item">
              <span class="game-name">{{ game.name }}</span>
              <span class="game-count">{{ game.winsCount }} побед</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'GameStatistics',

  data() {
    return {
      loading: true,
      currentTab: 'winners',
      tabs: [
        { id: 'winners', name: 'Топ победителей' },
        { id: 'players', name: 'Топ игроков' },
        { id: 'games', name: 'Топ игр' }
      ],
      winners: [],
      players: [],
      games: [],
      sortField: null,
      sortDirection: 'desc',
      selectedPlayer: null
    }
  },

  computed: {
    sortedWinners() {
      if (!this.sortField || !this.winners.length) return this.winners;
      
      return [...this.winners].sort((a, b) => {
        const aValue = a[`${this.sortField}Wins`];
        const bValue = b[`${this.sortField}Wins`];
        return this.sortDirection === 'desc' ? bValue - aValue : aValue - bValue;
      });
    },

    sortedPlayers() {
      if (!this.sortField || !this.players.length) return this.players;
      
      return [...this.players].sort((a, b) => {
        let aValue, bValue;
        
        switch(this.sortField) {
          case 'days':
            aValue = a.daysPlayed;
            bValue = b.daysPlayed;
            break;
          case 'games':
            aValue = a.gamesPlayed;
            bValue = b.gamesPlayed;
            break;
          default:
            return 0;
        }
        
        return this.sortDirection === 'desc' ? bValue - aValue : aValue - bValue;
      });
    },

    topGames() {
      return this.games;
    }
  },

  methods: {
    async fetchStatistics() {
      try {
        const userId = window.Telegram?.WebApp?.initDataUnsafe?.user?.id || '';
        const headers = { 'X-Telegram-User-Id': userId };

        const [winnersResponse, playersResponse, gamesResponse] = await Promise.all([
          axios.get('/api/statistics/winners', { headers }),
          axios.get('/api/statistics/players', { headers }),
          axios.get('/api/statistics/games', { headers })
        ]);

        this.winners = winnersResponse.data;
        this.players = playersResponse.data;
        this.games = gamesResponse.data;
      } catch (error) {
        console.error('Error fetching statistics:', error);
        window.Telegram?.WebApp?.showAlert('Ошибка при загрузке статистики');
      } finally {
        this.loading = false;
      }
    },

    sort(field) {
      if (this.sortField === field) {
        this.sortDirection = this.sortDirection === 'desc' ? 'asc' : 'desc';
      } else {
        this.sortField = field;
        this.sortDirection = 'desc';
      }
    },

    getSortIcon(field) {
      if (this.sortField !== field) return '';
      return this.sortDirection === 'desc' ? '↓' : '↑';
    },

    async showPlayerDetails(username) {
      try {
        const userId = window.Telegram?.WebApp?.initDataUnsafe?.user?.id || '';
        const headers = { 'X-Telegram-User-Id': userId };
        
        const response = await axios.get(`/api/statistics/players/${username}`, { headers });
        console.log('Player details response:', {
          fullResponse: response.data,
          topGames: response.data.topGames,
          topWinningGames: response.data.topWinningGames
        });
        this.selectedPlayer = response.data;
      } catch (error) {
        console.error('Error fetching player details:', error);
        window.Telegram?.WebApp?.showAlert('Ошибка при загрузке данных игрока');
      }
    }
  },

  created() {
    this.fetchStatistics();
  }
}
</script>

<style scoped>
.statistics-screen {
  padding: 16px;
  height: 100%;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  background: var(--background-color);
  color: var(--text-color);
}

.header {
  position: sticky;
  top: 0;
  background: var(--background-color);
  z-index: 2;
  padding: 12px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-color);
}

.header h2 {
  font-family: 'Montserrat', sans-serif;
  font-weight: 700;
  font-size: 24px;
  color: var(--primary-color);
  margin: 0;
}

/* Стили для табов */
.tabs {
  position: sticky;
  top: 60px;
  background: var(--background-color);
  z-index: 2;
  padding: 12px 0;
  display: flex;
  gap: 12px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  border-bottom: 1px solid var(--border-color);
}

.tab-button {
  padding: 12px 24px;
  border: none;
  border-radius: 16px;
  background: var(--secondary-bg);
  color: var(--text-color);
  font-family: 'Montserrat', sans-serif;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.tab-button.active {
  background: var(--primary-color);
  color: var(--background-color);
  box-shadow: 0 4px 15px var(--shadow-color);
}

.tab-button:hover:not(.active) {
  background: var(--border-color);
}

.statistics-table {
  margin-top: 16px;
  background: var(--secondary-bg);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 15px var(--shadow-color);
  animation: fadeIn 0.3s ease-out;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  background: var(--primary-color);
  color: var(--background-color);
  font-weight: 700;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  border-bottom: 1px solid var(--border-color);
}

.table-row:last-child {
  border-bottom: none;
}

.header-cell, .cell {
  padding: 16px;
  display: flex;
  align-items: center;
  font-family: 'Montserrat', sans-serif;
}

.header-cell.sortable {
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.header-cell.sortable:hover {
  background: var(--primary-hover);
}

.statistics-table:has(.table-header:not(:has([class*="sortable"]))) {
  .table-header, .table-row {
    grid-template-columns: 2fr 1fr;
  }
}

.statistics-table[data-tab="players"] {
  .table-header, .table-row {
    grid-template-columns: 2fr 1fr 1fr;
  }
}

.loading {
  text-align: center;
  padding: 40px;
  color: var(--text-color);
  font-family: 'Montserrat', sans-serif;
  font-size: 16px;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes pulse {
  0% { opacity: 0.6; }
  50% { opacity: 1; }
  100% { opacity: 0.6; }
}

.back-button {
  background-color: var(--primary-color);
  color: var(--background-color);
  border: none;
  border-radius: 24px;
  padding: 12px 24px;
  font-family: 'Montserrat', sans-serif;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 1px;
  box-shadow: 0 4px 15px var(--shadow-color);
}

.back-button:hover {
  background-color: var(--primary-hover);
  transform: translateY(-2px);
}

.back-button:active {
  transform: translateY(0);
}

.table-row.clickable {
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.table-row.clickable:hover {
  background-color: var(--border-color);
}

.player-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.player-modal-content {
  background: var(--background-color);
  border-radius: 16px;
  padding: 24px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.player-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.player-modal-header h3 {
  font-family: 'Montserrat', sans-serif;
  font-size: 32px;
  font-weight: 700;
  color: var(--primary-color);
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 32px;
  color: var(--text-color);
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.player-details {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.player-details h4 {
  font-family: 'Montserrat', sans-serif;
  font-size: 24px;
  font-weight: 700;
  color: var(--text-color);
  margin: 0;
}

.games-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.game-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: var(--secondary-bg);
  border-radius: 8px;
  transition: background-color 0.3s ease;
}

.game-item:hover {
  background: var(--border-color);
}

.game-name {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
  flex: 1;
}

.game-count {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary-color);
  padding-left: 16px;
  white-space: nowrap;
}

/* Удалим неиспользуемые стили */
.stats-summary,
.stat-item,
.section {
  display: none;
}

.player-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 8px;
  background: var(--secondary-bg);
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid transparent;
  position: relative;
}

.player-row:hover {
  background: var(--border-color);
  border-color: var(--primary-color);
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.player-row::after {
  content: '→';
  position: absolute;
  right: 16px;
  opacity: 0;
  transition: all 0.3s ease;
  color: var(--primary-color);
  font-size: 20px;
}

.player-row:hover::after {
  opacity: 1;
  right: 12px;
}

.player-row:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px var(--primary-color-light);
}

.player-name {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
}

.player-name::after {
  content: 'Нажмите для просмотра деталей';
  position: absolute;
  bottom: -24px;
  left: 50%;
  transform: translateX(-50%);
  background: var(--tooltip-bg, rgba(0, 0, 0, 0.8));
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  white-space: nowrap;
  pointer-events: none;
}

.player-row:hover .player-name::after {
  opacity: 1;
  visibility: visible;
  bottom: -28px;
}
</style>