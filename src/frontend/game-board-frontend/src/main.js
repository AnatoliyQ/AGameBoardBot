import { createApp } from 'vue';
import App from './App.vue';

// Создаем приложение после загрузки DOM
document.addEventListener('DOMContentLoaded', () => {
  const app = createApp(App);
  
  // Добавляем Telegram Web App в глобальные свойства
  app.config.globalProperties.$telegram = window.Telegram.WebApp;
  
  // Монтируем приложение
  app.mount('#app');
});