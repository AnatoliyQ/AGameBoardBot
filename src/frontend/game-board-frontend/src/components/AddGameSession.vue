<template>
  <div class="add-session-screen" @click="hideKeyboard">
    <h2>Добавить сыгранную игру</h2>
    <button
      class="back-button"
      @click="$emit('back')"
    >
      Назад
    </button>

    <form class="add-session-form card" @submit.prevent="saveSession" @click.stop>
      <div class="form-group">
        <label>Игра:</label>
        <select 
          v-model="selectedGame" 
          required
          @change="handleGameSelect"
          @keydown.enter.prevent="hideKeyboard"
          @blur="hideKeyboard"
        >
          <option value="">Выберите игру</option>
          <option 
            v-for="game in games" 
            :key="game.id" 
            :value="game"
          >
            {{ game.name }}
          </option>
        </select>
      </div>

      <div class="form-group">
        <label>Игроки:</label>
        <div class="players-select">
          <div 
            v-for="user in users" 
            :key="user.telegramUsername" 
            class="player-checkbox"
          >
            <input 
              type="checkbox"
              :value="user.telegramUsername"
              v-model="selectedPlayers"
            >
            <span class="player-name">
              {{ user.firstName }} {{ user.lastName }}
              <small class="username">@{{ user.telegramUsername }}</small>
            </span>
          </div>
        </div>
      </div>

      <div v-if="selectedGame?.type === 'PVP' && selectedPlayers.length > 0" class="form-group">
        <label>Победители:</label>
        <div class="winners-select">
          <div 
            v-for="username in selectedPlayers" 
            :key="username" 
            class="winner-checkbox"
          >
            <input 
              type="checkbox"
              :value="username"
              v-model="winners"
            >
            <span class="player-name">
              {{ getUserFullName(username) }}
            </span>
          </div>
        </div>
      </div>

      <div v-if="selectedGame?.type === 'PVE'" class="form-group">
        <label class="checkbox-label">
          <input
            type="checkbox"
            v-model="isVictory"
          />
          <span>Победа</span>
        </label>
      </div>

      <button type="submit" class="submit-button">Сохранить</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AddGameSession',
  inject: ['webApp'],

  data() {
    return {
      games: [],
      users: [],
      selectedGame: null,
      selectedPlayers: [],
      winners: [],
      isVictory: false
    }
  },

  async created() {
    await this.loadGames()
    await this.loadUsers()
  },

  methods: {
    async loadGames() {
      try {
        const response = await axios.get('/api/games')
        this.games = response.data
      } catch (error) {
        console.error('Error loading games:', error)
        window.Telegram?.WebApp?.showAlert('Ошибка при загрузке списка игр')
      }
    },

    async loadUsers() {
      try {
        const response = await axios.get('/api/users')
        this.users = response.data
      } catch (error) {
        console.error('Error loading users:', error)
        window.Telegram?.WebApp?.showAlert('Ошибка при загрузке списка игроков')
      }
    },

    getUserFullName(username) {
      const user = this.users.find(u => u.telegramUsername === username)
      return user ? `${user.firstName} ${user.lastName}` : username
    },

    getUserName(username) {
      return this.getUserFullName(username)
    },

    handleGameSelect() {
      this.selectedPlayers = []
      this.winners = []
      this.isVictory = false
    },

    hideKeyboard() {
      if (this.webApp) {
        this.webApp.closeScanQrPopup()
        this.webApp.closeKeyboard()
      }
    },

    handleInput(event) {
      const value = event.target.value;
      if (value.includes('\n')) {
        event.target.blur();
        window.Telegram?.WebApp?.closeScanQrPopup?.();
        window.Telegram?.WebApp?.closeKeyboard?.();
      }
    },

    async saveSession() {
      if (this.selectedPlayers.length === 0) {
        window.Telegram?.WebApp?.showAlert('Выберите хотя бы одного игрока')
        return
      }

      if (this.selectedGame.type === 'PVP' && this.winners.length === 0) {
        window.Telegram?.WebApp?.showAlert('Выберите хотя бы одного победителя')
        return
      }

      const session = {
        gameId: this.selectedGame.id,
        playDate: new Date().toISOString().split('T')[0],
        players: this.selectedPlayers.map(username => ({
          username,
          isWinner: this.determineWinner(username)
        }))
      }

      if (this.selectedGame.type === 'PVP') {
        session.winnerUsername = this.winners[0] // Для обратной совместимости оставляем первого победителя
      } else if (this.selectedGame.type === 'PVE') {
        session.isVictory = this.isVictory
      }

      try {
        const response = await axios.post('/api/statistics/sessions', session, {
          headers: {
            'X-Telegram-User-Id': this.webApp?.initDataUnsafe?.user?.id || ''
          }
        })
        
        if (response.status === 200) {
          window.Telegram?.WebApp?.showAlert('Статистика успешно сохранена')
          this.$emit('session-added')
          this.$emit('back')
        }
      } catch (error) {
        console.error('Error saving session:', error)
        window.Telegram?.WebApp?.showAlert(
          error.response?.status === 403 
            ? 'У вас нет прав для сохранения статистики' 
            : 'Ошибка при сохранении статистики'
        )
      }
    },

    determineWinner(username) {
      switch (this.selectedGame.type) {
        case 'PVP':
          return this.winners.includes(username)
        case 'PVE':
          return this.isVictory
        default:
          return false
      }
    },

    handleEnter(event) {
      event.preventDefault();
      if (this.webApp) {
        this.webApp.closeScanQrPopup();
        this.webApp.closeKeyboard();
      }
      event.target.blur();
    }
  }
}
</script>

<style scoped>
.add-session-screen {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.add-session-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 600;
  color: var(--text-color);
}

.form-group select,
.form-group input[type="text"] {
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--background-color);
  color: var(--text-color);
}

.players-select {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 10px;
  background: var(--background-color);
}

.player-checkbox,
.team-player-checkbox {
  display: flex;
  align-items: center;
  padding: 8px;
  gap: 10px;
  cursor: pointer;
}

.player-checkbox input[type="checkbox"],
.team-player-checkbox input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: var(--primary-color);
}

.player-name {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  color: var(--text-secondary);
  font-size: 0.8em;
}

.teams-container {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.team-player-checkbox {
  padding: 5px;
  border-radius: 4px;
}

/* Убираем hover-эффект */
.player-checkbox:hover,
.team-player-checkbox:hover {
  background-color: transparent;
}

.submit-button {
  background-color: var(--primary-color);
  color: var(--background-color);
  border: none;
  border-radius: 8px;
  padding: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-button:hover {
  background-color: var(--primary-hover);
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

h2 {
  color: var(--text-color);
  text-align: center;
  margin-bottom: 20px;
}

.winners-select {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 10px;
  background: var(--background-color);
}

.winner-checkbox {
  display: flex;
  align-items: center;
  padding: 8px;
  gap: 10px;
  cursor: pointer;
}

.winner-checkbox input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: var(--primary-color);
}
</style> 