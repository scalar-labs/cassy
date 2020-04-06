import Vue from 'vue'
import VueRouter from 'vue-router'
import ViewClusters from '../views/ViewClusters.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'ViewClusters',
    component: ViewClusters
  },
]

const router = new VueRouter({
  routes
})

export default router
