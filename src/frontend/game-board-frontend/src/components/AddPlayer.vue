<template>
  <div class="add-player-screen" @click="hideKeyboard">
    <h2>Добавить игрока</h2>
    <button
      class="back-button"
      @click="$emit('back')"
    >
      Назад
    </button>

    <form class="add-player-form" @submit.prevent="savePlayer" @click.stop>
      <div class="form-group">
        <label>Имя:</label>
        <input
          v-model="player.firstName"
          type="text"
          required
          placeholder="Введите имя"
          @keydown.enter.prevent="hideKeyboard"
          @blur="hideKeyboard"
        />
      </div>

      <div class="form-group">
        <label>Фамилия:</label>
        <input
          v-model="player.lastName"
          type="text"
          required
          placeholder="Введите фамилию"
          @keydown.enter.prevent="hideKeyboard"
          @blur="hideKeyboard"
        />
      </div>

      <div class="form-group">
        <label>Telegram username:</label>
        <input
          v-model="player.telegramUsername"
          type="text"
          required
          placeholder="Введите телеграм ник"
          @keydown.enter.prevent="hideKeyboard"
          @blur="hideKeyboard"
        />
      </div>

      <div class="form-group checkbox-group">
        <label class="checkbox-label">
          <input
            type="checkbox"
            v-model="player.isEmployee"
          />
          <span>Сотрудник компании</span>
        </label>
      </div>

      <button type="submit" class="submit-button">Сохранить</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AddPlayer',
  inject: ['webApp'],

  data() {
    return {
      player: {
        firstName: '',
        lastName: '',
        telegramUsername: null,
        isEmployee: false
      }
    }
  },

  methods: {
    hideKeyboard() {
      if (this.webApp) {
        this.webApp.closeScanQrPopup()
        this.webApp.closeKeyboard()
      }
    },

    async savePlayer() {
      try {
        await axios.post('/api/users', this.player)
        this.$emit('player-added')
        this.$emit('back')
      } catch (error) {
        console.error('Error saving player:', error)
        window.Telegram?.WebApp?.showAlert('Ошибка при сохранении игрока')
      }
    }
  }
}
</script>

<style scoped>
.add-player-screen {
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

.add-player-form {
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

.form-group input[type="text"],
.form-group input[type="number"] {
  padding: 14px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  background: var(--secondary-bg);
  color: var(--text-color);
  font-size: 16px;
  transition: all 0.3s ease;
}

.checkbox-group {
  flex-direction: row;
  align-items: center;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: 20px;
  height: 20px;
  cursor: pointer;
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