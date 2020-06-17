import Vue from 'vue'
import { api } from './api'
import App from './App.vue'
import router from './router'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import  '@/assets/custom.scss'
Vue.prototype.$api = api;
Vue.config.productionTip = false;

Vue.use(require('vue-moment'));
Vue.use(BootstrapVue);
new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
