<template>
    <div>
        <Clusters :clusters="clusters"/>
        <RegisterCluster />
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
        clusters: {},
      };
    },
    mounted() {
      this.$api.get('clusters/')
      .then((response) => {
        if (response.status === 200) {
          this.clusters = response.data;
        }
      });
    },
  };
</script>
