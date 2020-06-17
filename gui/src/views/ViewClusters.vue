<template>
    <div class="col">
        <Clusters :entries="entries" :entryCount="entryCount"/>
        <RegisterCluster id="registerCluster" @updateClusterList="fetchClusters"/>
    </div>
</template>

<script>
  // @ is an alias to /src
  import Clusters from '../components/Clusters';
  import RegisterCluster from '../components/RegisterCluster'


  export default {
    name: 'ViewClusters',
    components: {
      Clusters,
      RegisterCluster
    },
    data() {
      return {
        entries: [],
        entryCount: 0,
        autoUpdateTimer: ''
      };
    },
    mounted() {
      this.fetchClusters();
      this.autoUpdateTimer = setInterval(this.fetchClusters, 10000);
    },
    beforeDestroy() {
      clearInterval(this.autoUpdateTimer);
    },
    methods: {
      fetchClusters() {
        this.$api.get('clusters/')
        .then((response) => {
          if (response.status === 200) {
            this.entries = response.data.entries;
            this.entryCount = this.entries.length;
          }
        });
      }
    }
  };
</script>
