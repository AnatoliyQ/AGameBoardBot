<template>
  <div class="suggest-screen">
    <h2>Предложить катку</h2>
    <button class="back-button" @click="$emit('back')">
      Назад
    </button>

    <form class="suggest-form" @submit.prevent="suggest">
      <div class="form-group">
        <label>Выберите игру:</label>
        <select 
          v-model="selectedGameId"
          required
          class="game-select"
        >
          <option value="">Выберите игру</option>
          <option 
            v-for="game in games" 
            :key="game.id" 
            :value="game.id"
          >
            {{ game.name }} ({{ game.players }} игроков)
          </option>
        </select>
      </div>

      <div v-if="selectedGame" class="selected-game">
        <h3>{{ selectedGame.name }}</h3>
        <div class="game-info">
          <span>{{ selectedGame.players }} игроков</span>
          <span>{{ selectedGame.type }}</span>
        </div>
      </div>

      <div class="form-group">
        <label>Дата:</label>
        <input 
          type="date" 
          v-model="date"
          :min="minDate"
          required
        />
      </div>

      <div class="form-group">
        <label>Время:</label>
        <input 
          type="time" 
          v-model="time"
          required
        />
      </div>

      <div class="form-group">
        <label>Место:</label>
        <select
          v-model="location"
          required
          class="game-select"
        >
          <option value="">Выберите место</option>
          <option value="office">Офис</option>
          <option value="slon">Слон</option>
        </select>
      </div>

      <button type="submit" class="submit-button">Предложить</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      games: [],
      selectedGameId: '',
      date: '',
      time: '',
      location: '',
      minDate: new Date().toISOString().split('T')[0],
      timezoneOffset: new Date().getTimezoneOffset()
    }
  },

  computed: {
    selectedGame() {
      return this.games.find(game => game.id === this.selectedGameId)
    }
  },

  async created() {
    try {
      const response = await axios.get('/api/games')
      this.games = response.data
    } catch (error) {
      console.error('Error loading games:', error)
      alert('Ошибка при загрузке списка игр')
    }
  },

  methods: {
    async suggest() {
      if (!this.selectedGameId || !this.date || !this.time || !this.location) {
        alert('Пожалуйста, заполните все поля')
        return
      }

      const localDateTime = new Date(`${this.date}T${this.time}`)
      
      try {
        await axios.post('/api/sessions', {
          gameId: this.selectedGameId,
          dateTime: localDateTime.toISOString(),
          location: this.location,
          timezoneOffset: this.timezoneOffset
        })
        
        alert('Предложение успешно отправлено!')
        this.$emit('back')
      } catch (error) {
        console.error('Error creating session:', error)
        alert('Произошла ошибка при отправке предложения')
      }
    }
  }
}
</script>

<style scoped>
.suggest-screen {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 20px;
}

.suggest-form {
  background: var(--secondary-bg);
  border: 1px solid var(--primary-color);
  border-radius: 16px;
  padding: 32px;
  max-width: 400px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-group label {
  color: var(--primary-color);
  font-size: 16px;
  font-weight: 500;
}

.form-group input,
.form-group select {
  padding: 14px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  background: var(--secondary-bg);
  color: var(--text-color);
  font-size: 16px;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px var(--shadow-color);
}

h2 {
  font-size: 28px;
  margin: 0;
  color: var(--primary-color);
  font-weight: 700;
}

.selected-game {
  background: var(--secondary-bg);
  border: 1px solid var(--primary-color);
  border-radius: 12px;
  padding: 20px;
  margin: 8px 0;
}

.selected-game h3 {
  color: var(--primary-color);
  margin: 0 0 12px 0;
  font-size: 20px;
}

.game-info {
  color: var(--text-secondary);
  display: flex;
  gap: 16px;
}

.game-select {
  padding: 14px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  background: var(--secondary-bg);
  color: var(--text-color);
  font-size: 16px;
  width: 100%;
  cursor: pointer;
  transition: all 0.3s ease;
}

.game-select:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px var(--shadow-color);
}

.game-select option {
  background-color: var(--background-color);
  color: var(--text-color);
  padding: 8px;
}

.submit-button {
  background-color: var(--primary-color);
  color: var(--background-color);
  border: none;
  border-radius: 24px;
  padding: 16px 32px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 16px;
  text-transform: uppercase;
  letter-spacing: 1px;
  width: 100%;
}

.submit-button:hover {
  background-color: var(--primary-hover);
  transform: translateY(-2px);
}

.submit-button:active {
  transform: translateY(0);
}
</style>