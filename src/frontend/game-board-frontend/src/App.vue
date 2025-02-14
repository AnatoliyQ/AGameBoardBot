<template>
  <div class="app">
    <header>
      <h1>GAME BOARD</h1>
      <img
        v-if="!showGamesList && !showSuggestForm && !showGameDetails && !showAddPlayer && !showAddSession && !showStatistics" 
        src="@/assets/logo.svg"
        alt="Logo" 
        class="main-logo"
      />
    </header>

    <main>
      <div v-if="!showGamesList && !showSuggestForm && !showGameDetails && !showAddPlayer && !showAddSession && !showStatistics" class="menu">
        <div class="menu-buttons">
          <button class="back-button" @click="showGames">
            Показать игры
          </button>
          <button class="back-button" @click="showSuggest">
            Предложить катку
          </button>
          <button class="back-button" @click="showStatisticsView">
            Статистика
          </button>
          <button
            v-if="isAdmin"
            class="back-button admin-button" 
            @click="showAddPlayerForm"
          >
            Добавить игрока
          </button>
          <button 
            v-if="isAdmin"
            class="back-button admin-button" 
            @click="showAddSessionForm"
          >
            Добавить статистику
          </button>
        </div>
      </div>

      <game-list
        v-else-if="showGamesList"
        :gamesList="games"
        :loading="loading"
        @back="goBack"
        @select-game="selectGame"
      />

      <suggest-time-and-place
        v-else-if="showSuggestForm"
        :selectedGame="selectedGame"
        @back="goBack"
      />

      <game-details
        v-else-if="showGameDetails"
        :gameId="selectedGameId"
        @back="goBack"
      />

      <add-player
        v-else-if="showAddPlayer"
        @back="goBack"
        @player-added="handlePlayerAdded"
      />

      <add-game-session
        v-else-if="showAddSession"
        @back="goBack"
        @session-added="handleSessionAdded"
      />

      <game-statistics
        v-else-if="showStatistics"
        @back="goBack"
      >
        <div v-if="currentTab === 'winners'" class="statistics-table" data-tab="winners">
          <!-- ... -->
        </div>
        <div v-if="currentTab === 'players'" class="statistics-table" data-tab="players">
          <!-- ... -->
        </div>
        <div v-if="currentTab === 'games'" class="statistics-table" data-tab="games">
          <!-- ... -->
        </div>
      </game-statistics>
    </main>

    <footer class="footer">
      <p>
        Developed by 
        <a href="https://t.me/AnatolSn" target="_blank" rel="noopener">Anatolii Sennikov</a> 
        for AVO © 2025
      </p>
    </footer>
  </div>
</template>

<script>
import GameList from './components/GameList.vue'
import SuggestTimeAndPlace from './components/SuggestTimeAndPlace.vue'
import GameDetails from './components/GameDetails.vue'
import AddPlayer from './components/AddPlayer.vue'
import AddGameSession from './components/AddGameSession.vue'
import GameStatistics from './components/GameStatistics.vue'
import axios from 'axios'

export default {
  name: 'App',

  components: {
    GameList,
    SuggestTimeAndPlace,
    GameDetails,
    AddPlayer,
    AddGameSession,
    GameStatistics
  },

  data() {
    return {
      showGamesList: false,
      showSuggestForm: false,
      showGameDetails: false,
      showAddPlayer: false,
      showAddSession: false,
      showStatistics: false,
      games: [],
      loading: false,
      selectedGame: null,
      selectedGameId: null,
      telegramUser: null,
      currentTab: 'winners'
    }
  },

  computed: {
    isAdmin() {
      return this.telegramUser && [332120503, 1204992915].includes(this.telegramUser.id);
    }
  },

  provide() {
    return {
      webApp: window.Telegram?.WebApp
    }
  },

  async created() {
    await this.initTelegramWebApp();
  },

  methods: {
    async initTelegramWebApp() {
      if (!window.Telegram?.WebApp) {
        console.error('Telegram WebApp is not available');
        return;
      }

      const webApp = window.Telegram.WebApp;
      
      webApp.expand();
      webApp.ready();

      const user = webApp.initDataUnsafe?.user;
      if (user) {
        this.telegramUser = user;
        console.log('Telegram user:', user);
        
        axios.defaults.headers.common['X-Telegram-User-Id'] = user.id;
        
        webApp.showPopup({
          title: 'Добро пожаловать!',
          message: `Привет, ${user.first_name}!`,
          buttons: [{
            type: 'ok'
          }]
        });
      } else {
        console.error('No user data available');
      }
    },

    showAddPlayerForm() {
      if (this.isAdmin) {
        this.showAddPlayer = true;
      } else {
        window.Telegram?.WebApp?.showAlert('У вас нет прав для этого действия');
      }
    },

    handlePlayerAdded() {
      window.Telegram?.WebApp?.showPopup({
        title: 'Успешно',
        message: 'Игрок успешно добавлен',
        buttons: [{
          type: 'ok'
        }]
      });
      this.goBack();
    },

    async showGames() {
      this.showGamesList = true
      this.loading = true
      try {
        const response = await axios.get('/api/games')
        this.games = response.data
      } catch (err) {
        console.error('Failed to load games:', err)
      } finally {
        this.loading = false
      }
    },

    showSuggest() {
      this.showSuggestForm = true
      this.loadGames()
    },

    async loadGames() {
      try {
        const response = await axios.get('/api/games')
        this.games = response.data
      } catch (err) {
        console.error('Failed to load games:', err)
        alert('Ошибка при загрузке списка игр')
      } finally {
        this.loading = false
      }
    },

    selectGame(gameId) {
      this.selectedGameId = gameId;
      this.showGamesList = false;
      this.showGameDetails = true; // Показываем детали игры вместо формы
    },

    showAddSessionForm() {
      if (this.isAdmin) {
        this.showAddSession = true;
      } else {
        window.Telegram?.WebApp?.showAlert('У вас нет прав для этого действия');
      }
    },

    handleSessionAdded() {
      window.Telegram?.WebApp?.showPopup({
        title: 'Успешно',
        message: 'Статистика игры добавлена',
        buttons: [{
          type: 'ok'
        }]
      });
      this.goBack();
    },

    showStatisticsView() {
      this.showStatistics = true;
    },

    goBack() {
      if (this.showGameDetails) {
        this.showGameDetails = false;
        this.showGamesList = true;
        this.selectedGameId = null;
      } else {
        this.showGamesList = false;
        this.showSuggestForm = false;
        this.showGameDetails = false;
        this.showAddPlayer = false;
        this.showAddSession = false;
        this.showStatistics = false;
        this.selectedGame = null;
        this.selectedGameId = null;
      }
    }
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&display=swap');

:root {
  --primary-color: #FFD700;
  --primary-hover: #FFE44D;
  --background-color: #1E1E2E;
  --secondary-bg: rgba(255, 215, 0, 0.05); /* Полупрозрачный желтый для фона */
  --text-color: #FFFFFF;
  --text-secondary: rgba(255, 215, 0, 0.7); /* Полупрозрачный желтый для вторичного текста */
  --border-color: rgba(255, 215, 0, 0.2); /* Полупрозрачный желтый для границ */
  --shadow-color: rgba(255, 215, 0, 0.1); /* Тень с желтым оттенком */
}

* {
  font-family: 'Montserrat', sans-serif;
  box-sizing: border-box;
}

body {
  background: var(--background-color);
  color: var(--text-color);
  margin: 0;
  padding: 0;
}

.back-button {
  background-color: var(--primary-color);
  color: var(--background-color);
  border: none;
  border-radius: 24px; /* Увеличенное скругление */
  padding: 14px 28px;
  font-size: 16px;
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
  box-shadow: 0 6px 20px var(--shadow-color);
}

.back-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 10px var(--shadow-color);
}

.card {
  background: var(--secondary-bg);
  border: 1px solid var(--border-color);
  border-radius: 24px;
  padding: 24px;
  box-shadow: 0 4px 15px var(--shadow-color);
  backdrop-filter: blur(10px);
}

.form-group input,
.form-group select {
  background: var(--secondary-bg);
  border: 2px solid var(--border-color);
  border-radius: 16px;
  padding: 14px;
  color: var(--text-color);
  font-size: 16px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.form-group input:focus,
.form-group select:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px var(--shadow-color);
}

h1 {
  font-size: 36px;
  font-weight: 800;
  background: linear-gradient(45deg, var(--primary-color), var(--primary-hover));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 2px 10px var(--shadow-color);
  margin-bottom: 32px;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.menu-buttons {
  animation: fadeIn 0.5s ease-out;
}

.loader {
  color: var(--primary-color);
  text-align: center;
  padding: 20px;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0% { opacity: 0.6; }
  50% { opacity: 1; }
  100% { opacity: 0.6; }
}

.menu {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 40vh;
}

.menu-buttons {
  display: flex;
  flex-direction: column;
  gap: 20px; /* Расстояние между кнопками */
  align-items: center;
  width: 100%;
  max-width: 300px; /* Ограничение ширины кнопок */
}

.menu-buttons .back-button {
  width: 100%; /* Кнопки на всю ширину контейнера */
}

header {
  text-align: center;
  padding: 20px;
}

.main-logo {
  width: 150px;
  height: 150px;
  margin: 20px 0 20px;
}

h1 {
  text-align: center;
}

.admin-button {
  background-color: var(--primary-color);
}

.footer {
  text-align: center;
  padding: 20px;
  margin-top: auto;
  font-size: 14px;
  color: var(--text-secondary);
}

.footer a {
  color: var(--primary-color);
  text-decoration: none;
  transition: color 0.3s ease;
}

.footer a:hover {
  color: var(--primary-hover);
}

.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

main {
  flex: 1;
}
</style>