<template>
  <div class="app">
    <header class="app-header">
      <h1 class="app-title">GAME BOARD BOT</h1>
      <img 
        v-if="isMainMenu" 
        src="@/assets/logo.svg"
        alt="Logo" 
        class="main-logo"
      />
    </header>

    <main class="app-main">
      <div class="menu">
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
          <button class="back-button" @click="showTest">
            Тест
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
        v-if="showGamesList"
        :gamesList="games"
        :loading="loading"
        @back="goBack"
        @select-game="selectGame"
      />
      <suggest-time-and-place
        v-if="showSuggestForm"
        :selectedGame="selectedGame"
        @back="goBack"
      />
      <game-details
        v-if="showGameDetails"
        :gameId="selectedGameId"
        @back="goBack"
      />
      <add-player
        v-if="showAddPlayer"
        @back="goBack"
        @player-added="handlePlayerAdded"
      />
      <add-game-session
        v-if="showAddSession"
        @back="goBack"
        @session-added="handleSessionAdded"
      />
      <game-statistics
        v-if="showStatistics"
        @back="goBack"
      />
    </main>
  </div>
</template>

<script>
import axios from 'axios'
import GameList from './GameList.vue'
import SuggestTimeAndPlace from './SuggestTimeAndPlace.vue'
import GameDetails from './GameDetails.vue'
import AddPlayer from './AddPlayer.vue'
import AddGameSession from './AddGameSession.vue'
import GameStatistics from './GameStatistics.vue'

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

  created() {
    console.log('Component created')
    if (this.webApp) {
      console.log('WebApp exists, expanding...')
      this.webApp.expand()
    } else {
      console.log('WebApp not found')
    }
  },

  mounted() {
    console.log('Component mounted')
    console.log('App mounted, checking menu state:', {
      showGamesList: this.showGamesList,
      showSuggestForm: this.showSuggestForm,
      showStatistics: this.showStatistics,
      isMainMenu: this.isMainMenu
    })
  },

  data() {
    return {
      webApp: window.Telegram?.WebApp,
      games: [],
      loading: false,
      showGamesList: false,
      showSuggestForm: false,
      showGameDetails: false,
      showAddPlayer: false,
      showAddSession: false,
      showStatistics: false,
      selectedGame: null,
      selectedGameId: null
    }
  },

  computed: {
    isAdmin() {
      return [332120503, 1204992915].includes(Number(this.webApp?.initDataUnsafe?.user?.id))
    },
    isMainMenu() {
      return !this.showGamesList && 
             !this.showSuggestForm && 
             !this.showGameDetails && 
             !this.showAddPlayer && 
             !this.showAddSession && 
             !this.showStatistics
    }
  },

  methods: {
    logButton(name) {
      console.log(`Rendering button: ${name}, isMainMenu: ${this.isMainMenu}, isAdmin: ${this.isAdmin}`)
      return true
    },

    showGames() {
      this.showGamesList = true
    },

    showSuggest() {
      this.showSuggestForm = true
    },

    showStatisticsView() {
      console.log('Opening statistics...')
      this.showStatistics = true
    },

    showAddPlayerForm() {
      this.showAddPlayer = true
    },

    showAddSessionForm() {
      this.showAddSession = true
    },

    goBack() {
      this.showGamesList = false
      this.showSuggestForm = false
      this.showGameDetails = false
      this.showAddPlayer = false
      this.showAddSession = false
      this.showStatistics = false
      this.selectedGame = null
      this.selectedGameId = null
    },

    selectGame(game) {
      this.selectedGame = game
      this.selectedGameId = game.id
      this.showGameDetails = true
      this.showGamesList = false
    },

    handlePlayerAdded() {
    },

    handleSessionAdded() {
    },

    showTest() {
      if (this.webApp) {
        this.webApp.showPopup({
          title: 'Тест',
          message: 'тест',
          buttons: [{text: 'OK'}]
        })
      } else {
        alert('тест')
      }
    }
  }
}
</script>

<style>
.app {
  min-height: var(--tg-viewport-height, 100vh);
  max-height: var(--tg-viewport-height, 100vh);
  background-color: #1C1B1F;
  padding: 8px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.app-header {
  text-align: center;
  margin-bottom: 20px; /* Уменьшили отступ */
}

.app-title {
  color: #FFD54F;
  font-size: 36px;
  font-weight: bold;
  margin: 10px 0; /* Уменьшили отступ */
}

.main-logo {
  width: 120px; /* Уменьшили размер */
  height: 120px;
  margin: 20px auto; /* Уменьшили отступ */
  display: block;
}

.menu-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px; /* Уменьшили отступ между кнопками */
  max-width: 320px;
  margin: 0 auto;
  padding: 0 20px;
}

.menu-button {
  width: 100%;
  padding: 16px;
  border: none;
  border-radius: 32px;
  background-color: #FFD54F;
  color: #000000;
  font-size: 16px;
  font-weight: bold;
  text-transform: uppercase;
  cursor: pointer;
}

.admin-button {
  background-color: #FF9800;
}

.app-main {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}
</style> 