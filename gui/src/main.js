import Vue from 'vue'
import { api } from './api'
import App from './App.vue'
import router from './router'
import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import  '@/assets/custom.scss'
Vue.prototype.$api = api;
Vue.config.productionTip = false

Vue.use(require('vue-moment'));
new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
