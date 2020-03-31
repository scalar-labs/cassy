import Vue from 'vue'
import VueRouter from 'vue-router'
import ViewClusters from '../views/ViewClusters.vue'
import ViewBackups from '../views/ViewBackups';

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'ViewClusters',
    component: ViewClusters
  },
  {
    path: '/clusters/:cluster_id/backups',
    name: 'ViewBackups',
    component: ViewBackups
  }
]

const router = new VueRouter({
  routes
})

export default router
