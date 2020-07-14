<template>
    <div class="col">
        <ErrorNotification :error_message="error_message"/>
        <Clusters :entries="entries" :entryCount="entryCount"/>
        <RegisterCluster id="registerCluster" @updateClusterList="fetchClusters"/>
    </div>
</template>

<script>
  // @ is an alias to /src
  import Clusters from '../components/Clusters';
  import RegisterCluster from '../components/RegisterCluster'
  import ErrorNotification from '../components/ErrorNotification';


  export default {
    name: 'ViewClusters',
    props: {
      error_message: String,
    },
    components: {
      Clusters,
      RegisterCluster,
      ErrorNotification
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
        }).catch(error => {
          if (error.code === 503) {
            let message = "Failed to fetch clusters.";
            this.$emit('showError', message);
          }
        });
      }
    }
  };
</script>
