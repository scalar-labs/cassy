<template>
    <div>
        <Clusters :clusters="clusters"/>
    </div>
</template>

<script>
  // @ is an alias to /src
  import Clusters from '../components/Clusters';

  export default {
    name: 'ViewClusters',
    components: {
      Clusters,
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
