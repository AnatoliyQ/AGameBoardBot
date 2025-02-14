<template>
  <div class="game-details">
    <div class="content-wrapper">
      <button class="back-button" @click="$emit('back')">
        ← Назад к списку
      </button>

      <div v-if="loading" class="loader">
        Загрузка...
      </div>

      <div v-else-if="game" class="game-content">
        <img 
          v-if="game.imageUrl" 
          :src="game.imageUrl" 
          :alt="game.name" 
          class="game-image"
        />
        <h2>{{ game.name }}</h2>
        <p class="description">{{ game.description }}</p>

        <div class="game-meta">
          <div class="meta-item">
            <span class="meta-label">Игроки:</span>
            <span class="meta-value">{{ game.players }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">Тип:</span>
            <span class="meta-value">{{ game.type }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">Владелец:</span>
            <span class="meta-value">
              <a 
                v-if="game"
                :href="`https://t.me/${game.owner}`" 
                class="owner-link" 
                target="_blank"
              >
                {{ ownerFullName }}
              </a>
            </span>
          </div>
        </div>

        <a v-if="game.storeUrl" :href="game.storeUrl" target="_blank" class="store-link">
          Купить игру
        </a>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GameDetails',

  props: {
    gameId: {
      type: Number,
      required: true
    }
  },

  data() {
    return {
      game: null,
      owner: null,
      loading: true
    }
  },

  computed: {
    ownerFullName() {
      return this.owner ? `${this.owner.firstName} ${this.owner.lastName}` : this.game?.owner
    }
  },

  async created() {
    await this.loadGameDetails()
  },

  methods: {
    async loadGameDetails() {
      this.loading = true
      try {
        const response = await this.$axios.get(`/api/games/${this.$route.params.id}`)
        this.game = response.data
        await this.loadOwnerDetails()
      } catch (error) {
        console.error('Error loading game details:', error)
      } finally {
        this.loading = false
      }
    },

    async loadOwnerDetails() {
      if (!this.game?.owner) return
      
      try {
        const response = await this.$axios.get(`/api/users/${this.game.owner}`)
        this.owner = response.data
      } catch (error) {
        console.error('Error loading owner details:', error)
      }
    }
  }
}
</script>

<style scoped>
.game-details {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.back-button {
  margin-bottom: 40px;
  width: 200px;
}

.game-image {
  width: 100%;
  border-radius: 16px;
  border: 1px solid var(--primary-color);
}

.game-meta {
  background: var(--secondary-bg);
  border: 1px solid var(--primary-color);
  border-radius: 16px;
  padding: 20px;
  margin: 20px 0;
}

.meta-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid var(--primary-color);
}

.meta-item:last-child {
  border-bottom: none;
}

.meta-label {
  color: var(--text-secondary);
}

.meta-value {
  color: var(--text-color);
  font-weight: 500;
}

.store-link {
  background: var(--primary-color);
  color: var(--background-color);
  padding: 12px 24px;
  border-radius: 16px;
  text-decoration: none;
  font-weight: 600;
  display: inline-block;
  transition: all 0.3s ease;
}

.store-link:hover {
  background: var(--primary-hover);
  transform: translateY(-1px);
}

.loader {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  color: var(--tg-theme-hint-color, #999);
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
</style>