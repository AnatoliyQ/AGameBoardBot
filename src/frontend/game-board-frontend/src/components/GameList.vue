<template>
  <div class="games-screen">
    <h2>Список игр</h2>
    <button
      class="back-button"
      @click="$emit('back')"
    >
      Назад
    </button>

    <div v-if="loading || loadingOwners" class="loader">
      Загрузка...
    </div>

    <div v-else-if="gamesList && gamesList.length" class="games-container">
      <div 
        v-for="game in gamesList" 
        :key="game.id" 
        class="game-card"
        @click="selectGame(game.id)"
      >
        <img 
          v-if="game.imageUrl" 
          :src="game.imageUrl" 
          :alt="game.name"
          class="game-image"
        />
        <div class="game-name">{{ game.name }}</div>
        <div class="game-info">
          <div class="info-item">
            <span class="info-label">Игроки:</span>
            <span class="info-value">{{ game.players }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">Тип:</span>
            <span class="info-value">{{ game.type }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">Владелец:</span>
            <span class="info-value">
              <a 
                :href="`https://t.me/${game.owner}`" 
                class="owner-link" 
                target="_blank"
                @click.stop
              >
                {{ owners.has(game.owner) ? getOwnerFullName(game.owner) : game.owner }}
              </a>
            </span>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="no-games">
      Нет доступных игр
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'GameList',

  props: {
    gamesList: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
      owners: new Map(),
      loadingOwners: true
    }
  },

  watch: {
    gamesList: {
      immediate: true,
      handler(newGames) {
        if (newGames.length > 0) {
          this.loadOwners(newGames)
        }
      }
    }
  },

  methods: {
    async loadOwners(games) {
      this.loadingOwners = true
      const uniqueOwners = [...new Set(games.map(game => game.owner))]
      
      if (uniqueOwners.length === 0) {
        this.loadingOwners = false
        return
      }

      try {
        const params = new URLSearchParams()
        uniqueOwners.forEach(owner => params.append('usernames', owner))
        
        const response = await axios.get('/api/users/batch', { params })
        
        if (response.data) {
          this.owners.clear()
          Object.entries(response.data).forEach(([username, userData]) => {
            if (userData && userData.firstName && userData.lastName) {
              this.owners.set(username, userData)
            } else {
              this.owners.set(username, { 
                username,
                firstName: username,
                lastName: ''
              })
            }
          })
        }
      } catch (err) {
        console.error('Failed to load owners:', err)
        // В случае ошибки используем username как fallback
        uniqueOwners.forEach(owner => {
          if (!this.owners.has(owner)) {
            this.owners.set(owner, { 
              username: owner,
              firstName: owner,
              lastName: ''
            })
          }
        })
      } finally {
        this.loadingOwners = false
      }
    },

    getOwnerFullName(owner) {
      const userData = this.owners.get(owner)
      if (!userData) return owner
      return userData.firstName && userData.lastName 
        ? `${userData.firstName} ${userData.lastName}`.trim()
        : userData.username
    },

    selectGame(id) {
      this.$emit('select-game', Number(id))
    }
  }
}
</script>

<style scoped>
.games-screen {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 20px;
}

h2 {
  font-size: 24px;
  margin: 0;
  color: var(--primary-color);
  font-weight: 700;
}

.back-button {
  width: 200px;
  padding: 12px 20px;
  background-color: var(--primary-color);
  color: var(--background-color);
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.back-button:hover {
  background-color: var(--primary-hover);
  transform: translateY(-2px);
}

.back-button:active {
  transform: translateY(0);
}

.games-container {
  width: 100%;
  max-width: 600px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.game-card {
  background: var(--secondary-bg);
  border: 2px solid var(--border-color);
  border-radius: 24px;
  padding: 24px;
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  animation: fadeIn 0.5s ease-out;
  backdrop-filter: blur(10px);
}

.game-card:hover {
  transform: translateY(-4px);
  border-color: var(--primary-color);
  box-shadow: 0 6px 20px var(--shadow-color);
}

.game-name {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 2px solid var(--border-color);
}

.game-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.info-label {
  color: var(--text-secondary);
}

.info-value {
  color: var(--text-color);
  font-weight: 500;
}

.loader {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: var(--tg-theme-hint-color, #999);
  font-size: 16px;
}

.loader::after {
  content: '';
  width: 20px;
  height: 20px;
  margin-left: 10px;
  border: 2px solid #2481cc;
  border-radius: 50%;
  border-top-color: transparent;
  animation: spin 1s linear infinite;
}

.no-games {
  color: var(--tg-theme-hint-color, #999);
  font-size: 16px;
  text-align: center;
  padding: 20px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Адаптивность для маленьких экранов */
@media (max-width: 480px) {
  .games-container {
    padding: 0 10px;
  }

  .game-card {
    padding: 15px;
  }

  .game-name {
    font-size: 18px;
  }

  .info-item {
    font-size: 13px;
  }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.owner-link {
  color: var(--primary-color);
  text-decoration: none;
  transition: all 0.3s ease;
}

.owner-link:hover {
  text-decoration: underline;
  opacity: 0.8;
}

.game-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 16px;
}
</style>